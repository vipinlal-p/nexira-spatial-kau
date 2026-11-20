import json
from fastapi import APIRouter, Depends, HTTPException, status, Query
from sqlalchemy.orm import Session
from sqlalchemy import text
from typing import Optional, Dict, Any
from backend.app.database import get_db
from backend.app.models import User, PaddyField
from backend.app.auth import get_current_user, require_role
from backend.app.schemas import PaddyFieldCreate

router = APIRouter(prefix="/fields", tags=["GIS Paddy Fields"])

@router.get("", summary="Get Paddy Fields as GeoJSON FeatureCollection")
def get_paddy_fields(
    krishi_bhavan_id: Optional[int] = Query(None, description="Filter by Krishi Bhavan ID"),
    survey_number: Optional[str] = Query(None, description="Filter by survey number search"),
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    # Role-based jurisdiction filtering
    query_str = """
        pf.id, pf.survey_number, pf.farmer_id, pf.krishi_bhavan_id,
        pf.area_ha, pf.crop_season, pf.soil_type, pf.attributes,
        ST_AsGeoJSON(pf.geom) as geojson,
        u.name as farmer_name, u.phone as farmer_phone
        FROM paddy_fields pf
        JOIN users u ON pf.farmer_id = u.id
        WHERE 1=1
    """
    params = {}

    if current_user.role == "farmer":
        # Farmers can view their associated paddy fields
        query_str += " AND pf.farmer_id = :farmer_id"
        params["farmer_id"] = current_user.id
    elif current_user.role in ["officer", "admin"]:
        if krishi_bhavan_id:
            query_str += " AND pf.krishi_bhavan_id = :kb_id"
            params["kb_id"] = krishi_bhavan_id
        elif current_user.krishi_bhavan_id:
            query_str += " AND pf.krishi_bhavan_id = :kb_id"
            params["kb_id"] = current_user.krishi_bhavan_id

    if survey_number:
        query_str += " AND pf.survey_number ILIKE :survey"
        params["survey"] = f"%{survey_number}%"

    sql = text(f"SELECT {query_str}")
    result = db.execute(sql, params).fetchall()

    features = []
    for row in result:
        geom_dict = json.loads(row.geojson) if row.geojson else {}
        try:
            attrs = json.loads(row.attributes) if row.attributes else {}
        except Exception:
            attrs = {"raw": row.attributes}

        feature = {
            "type": "Feature",
            "id": row.id,
            "geometry": geom_dict,
            "properties": {
                "id": row.id,
                "survey_number": row.survey_number,
                "farmer_id": row.farmer_id,
                "farmer_name": row.farmer_name,
                "farmer_phone": row.farmer_phone,
                "krishi_bhavan_id": row.krishi_bhavan_id,
                "area_ha": float(row.area_ha) if row.area_ha else 0.0,
                "crop_season": row.crop_season,
                "soil_type": row.soil_type,
                "attributes": attrs
            }
        }
        features.append(feature)

    return {
        "type": "FeatureCollection",
        "features": features
    }

@router.get("/{field_id}", summary="Get specific paddy field by ID")
def get_paddy_field_by_id(field_id: int, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    sql = text("""
        SELECT pf.id, pf.survey_number, pf.farmer_id, pf.krishi_bhavan_id,
               pf.area_ha, pf.crop_season, pf.soil_type, pf.attributes,
               ST_AsGeoJSON(pf.geom) as geojson,
               u.name as farmer_name, u.phone as farmer_phone
        FROM paddy_fields pf
        JOIN users u ON pf.farmer_id = u.id
        WHERE pf.id = :field_id
    """)
    row = db.execute(sql, {"field_id": field_id}).fetchone()
    if not row:
        raise HTTPException(status_code=404, detail="Paddy field not found")

    if current_user.role == "farmer" and row.farmer_id != current_user.id:
        raise HTTPException(status_code=403, detail="Access denied to this paddy field")

    geom_dict = json.loads(row.geojson) if row.geojson else {}
    try:
        attrs = json.loads(row.attributes) if row.attributes else {}
    except Exception:
        attrs = {"raw": row.attributes}

    return {
        "type": "Feature",
        "id": row.id,
        "geometry": geom_dict,
        "properties": {
            "id": row.id,
            "survey_number": row.survey_number,
            "farmer_id": row.farmer_id,
            "farmer_name": row.farmer_name,
            "farmer_phone": row.farmer_phone,
            "krishi_bhavan_id": row.krishi_bhavan_id,
            "area_ha": float(row.area_ha) if row.area_ha else 0.0,
            "crop_season": row.crop_season,
            "soil_type": row.soil_type,
            "attributes": attrs
        }
    }

@router.post("", summary="Create a new paddy field polygon (Officer/Admin only)")
def create_paddy_field(
    field_in: PaddyFieldCreate,
    current_user: User = Depends(require_role(["officer", "admin"])),
    db: Session = Depends(get_db)
):
    geojson_str = json.dumps(field_in.geom_geojson)
    attrs_str = json.dumps(field_in.attributes or {})

    sql = text("""
        INSERT INTO paddy_fields (survey_number, farmer_id, krishi_bhavan_id, area_ha, crop_season, soil_type, geom, attributes)
        VALUES (:survey_number, :farmer_id, :krishi_bhavan_id, :area_ha, :crop_season, :soil_type, ST_GeomFromGeoJSON(:geojson), :attrs)
        RETURNING id
    """)
    result = db.execute(sql, {
        "survey_number": field_in.survey_number,
        "farmer_id": field_in.farmer_id,
        "krishi_bhavan_id": field_in.krishi_bhavan_id,
        "area_ha": field_in.area_ha,
        "crop_season": field_in.crop_season,
        "soil_type": field_in.soil_type,
        "geojson": geojson_str,
        "attrs": attrs_str
    })
    db.commit()
    new_id = result.fetchone()[0]
    return {"message": "Paddy field created successfully", "id": new_id}
