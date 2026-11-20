from pydantic import BaseModel, EmailStr
from typing import Optional, Dict, Any, List
from datetime import datetime

class Token(BaseModel):
    access_token: str
    token_type: str
    role: str
    user_id: int
    name: str

class TokenData(BaseModel):
    email: Optional[str] = None
    role: Optional[str] = None
    user_id: Optional[int] = None

class LoginRequest(BaseModel):
    email: EmailStr
    password: str

class UserResponse(BaseModel):
    id: int
    name: str
    phone: str
    email: str
    role: str
    krishi_bhavan_id: Optional[int]

    class Config:
        from_attributes = True

class PaddyFieldCreate(BaseModel):
    survey_number: str
    farmer_id: int
    krishi_bhavan_id: int
    area_ha: float
    crop_season: str
    soil_type: str
    geom_geojson: Dict[str, Any] # GeoJSON geometry object
    attributes: Optional[Dict[str, Any]] = None

class AnnouncementCreate(BaseModel):
    title: str
    body: str
    krishi_bhavan_id: int

class AnnouncementResponse(BaseModel):
    id: int
    officer_id: int
    krishi_bhavan_id: int
    title: str
    body: str
    created_at: datetime

    class Config:
        from_attributes = True

class MessageCreate(BaseModel):
    receiver_id: int
    body: str

class MessageResponse(BaseModel):
    id: int
    sender_id: int
    receiver_id: int
    body: str
    created_at: datetime
    read_at: Optional[datetime]

    class Config:
        from_attributes = True
