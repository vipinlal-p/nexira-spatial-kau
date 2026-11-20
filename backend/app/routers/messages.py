from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from sqlalchemy import or_, and_
from typing import List
from datetime import datetime
from backend.app.database import get_db
from backend.app.models import User, Message
from backend.app.auth import get_current_user
from backend.app.schemas import MessageCreate, MessageResponse

router = APIRouter(prefix="/messages", tags=["1:1 Messaging & Queries"])

@router.get("", response_model=List[MessageResponse], summary="Get messages for current user")
def get_my_messages(current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    messages = db.query(Message).filter(
        or_(Message.sender_id == current_user.id, Message.receiver_id == current_user.id)
    ).order_by(Message.created_at.asc()).all()
    return messages

@router.get("/{other_user_id}", response_model=List[MessageResponse], summary="Get conversation with specific user")
def get_conversation(other_user_id: int, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    messages = db.query(Message).filter(
        or_(
            and_(Message.sender_id == current_user.id, Message.receiver_id == other_user_id),
            and_(Message.sender_id == other_user_id, Message.receiver_id == current_user.id)
        )
    ).order_by(Message.created_at.asc()).all()

    # Mark messages received by current user as read
    for msg in messages:
        if msg.receiver_id == current_user.id and not msg.read_at:
            msg.read_at = datetime.utcnow()
    db.commit()

    return messages

@router.post("", response_model=MessageResponse, summary="Send 1:1 message or query")
def send_message(
    msg_in: MessageCreate,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    receiver = db.query(User).filter(User.id == msg_in.receiver_id).first()
    if not receiver:
        raise HTTPException(status_code=404, detail="Receiver not found")

    new_msg = Message(
        sender_id=current_user.id,
        receiver_id=msg_in.receiver_id,
        body=msg_in.body
    )
    db.add(new_msg)
    db.commit()
    db.refresh(new_msg)
    return new_msg
