from fastapi import APIRouter, Depends, HTTPException, Query
from sqlalchemy.orm import Session
from typing import List, Optional
from backend.app.database import get_db
from backend.app.models import User, Announcement
from backend.app.auth import get_current_user, require_role
from backend.app.schemas import AnnouncementCreate, AnnouncementResponse

router = APIRouter(prefix="/announcements", tags=["Announcements"])

@router.get("", response_model=List[AnnouncementResponse], summary="Get public announcements")
def get_announcements(
    krishi_bhavan_id: Optional[int] = Query(None),
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    query = db.query(Announcement)
    kb_id = krishi_bhavan_id or current_user.krishi_bhavan_id
    if kb_id:
        query = query.filter(Announcement.krishi_bhavan_id == kb_id)
    return query.order_by(Announcement.created_at.desc()).all()

@router.post("", response_model=AnnouncementResponse, summary="Broadcast public announcement (Officer/Admin)")
def create_announcement(
    ann_in: AnnouncementCreate,
    current_user: User = Depends(require_role(["officer", "admin"])),
    db: Session = Depends(get_db)
):
    new_ann = Announcement(
        officer_id=current_user.id,
        krishi_bhavan_id=ann_in.krishi_bhavan_id,
        title=ann_in.title,
        body=ann_in.body
    )
    db.add(new_ann)
    db.commit()
    db.refresh(new_ann)
    return new_ann
