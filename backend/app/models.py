from sqlalchemy import Column, Integer, String, Numeric, Text, TIMESTAMP, ForeignKey, func
from sqlalchemy.orm import relationship
from geoalchemy2 import Geometry
from backend.app.database import Base

class KrishiBhavan(Base):
    __tablename__ = "krishi_bhavans"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String(150), nullable=False)
    district = Column(String(100), nullable=False)
    jurisdiction_geom = Column(Geometry("MULTIPOLYGON", srid=4326), nullable=False)

    users = relationship("User", back_populates="krishi_bhavan")
    paddy_fields = relationship("PaddyField", back_populates="krishi_bhavan")
    announcements = relationship("Announcement", back_populates="krishi_bhavan")

class User(Base):
    __tablename__ = "users"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String(150), nullable=False)
    phone = Column(String(20), unique=True, nullable=False, index=True)
    email = Column(String(150), unique=True, nullable=False, index=True)
    password_hash = Column(String(255), nullable=False)
    role = Column(String(50), nullable=False) # 'farmer', 'officer', 'admin'
    krishi_bhavan_id = Column(Integer, ForeignKey("krishi_bhavans.id", ondelete="SET NULL"))
    created_at = Column(TIMESTAMP(timezone=True), server_default=func.now())

    krishi_bhavan = relationship("KrishiBhavan", back_populates="users")
    paddy_fields = relationship("PaddyField", back_populates="farmer")
    announcements = relationship("Announcement", back_populates="officer")

class PaddyField(Base):
    __tablename__ = "paddy_fields"

    id = Column(Integer, primary_key=True, index=True)
    survey_number = Column(String(100), nullable=False, index=True)
    farmer_id = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"))
    krishi_bhavan_id = Column(Integer, ForeignKey("krishi_bhavans.id", ondelete="CASCADE"))
    area_ha = Column(Numeric(10, 4), nullable=False)
    crop_season = Column(String(50), nullable=False)
    soil_type = Column(String(100), nullable=False)
    geom = Column(Geometry("MULTIPOLYGON", srid=4326), nullable=False)
    attributes = Column(Text, nullable=True) # Stored as JSON string or dict via SQLAlchemy JSON/Text
    created_at = Column(TIMESTAMP(timezone=True), server_default=func.now())

    farmer = relationship("User", back_populates="paddy_fields")
    krishi_bhavan = relationship("KrishiBhavan", back_populates="paddy_fields")

class Announcement(Base):
    __tablename__ = "announcements"

    id = Column(Integer, primary_key=True, index=True)
    officer_id = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"))
    krishi_bhavan_id = Column(Integer, ForeignKey("krishi_bhavans.id", ondelete="CASCADE"))
    title = Column(String(200), nullable=False)
    body = Column(Text, nullable=False)
    created_at = Column(TIMESTAMP(timezone=True), server_default=func.now())

    officer = relationship("User", back_populates="announcements")
    krishi_bhavan = relationship("KrishiBhavan", back_populates="announcements")

class Message(Base):
    __tablename__ = "messages"

    id = Column(Integer, primary_key=True, index=True)
    sender_id = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"))
    receiver_id = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"))
    body = Column(Text, nullable=False)
    created_at = Column(TIMESTAMP(timezone=True), server_default=func.now())
    read_at = Column(TIMESTAMP(timezone=True), nullable=True)

    sender = relationship("User", foreign_keys=[sender_id])
    receiver = relationship("User", foreign_keys=[receiver_id])
