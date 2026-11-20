from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from backend.app.database import engine, Base
from backend.app.routers import auth, fields, announcements, messages

# Create tables if not already created by migrations
# Base.metadata.create_all(bind=engine)

app = FastAPI(
    title="Nexira Spatial – KAU Paddy Field Mobile GIS API",
    description="Backend API for Department of Soil Science, Kerala Agricultural University (KAU) Paddy Field GIS and Farmer-Officer communication.",
    version="1.0.0"
)

# CORS middleware for Flutter web/mobile & desktop
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(auth.router)
app.include_router(fields.router)
app.include_router(announcements.router)
app.include_router(messages.router)

@app.get("/", tags=["Health Check"])
def root():
    return {
        "status": "online",
        "app": "Nexira Spatial – KAU Paddy Field Mobile GIS API",
        "docs": "/docs"
    }
