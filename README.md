# Nexira Spatial – KAU Paddy Field Mobile GIS

A production-quality GIS-enabled mobile and web application for the Department of Soil Science, Kerala Agricultural University (KAU), managing paddy fields and facilitating seamless communication between Farmers and Agricultural Officers under Krishi Bhavans in Kerala, India.

---

## Architecture & Tech Stack
- **Mobile & Web App**: Flutter (Dart), single codebase for Android, iOS, and Web (`flutter_map`, `latlong2`, `dio`, `flutter_riverpod`).
- **Backend API**: Python FastAPI (`uvicorn`, `SQLAlchemy`, `GeoAlchemy2`, `PyJWT`, `passlib`).
- **Spatial Database**: PostgreSQL + PostGIS (spatial polygons, GeoJSON features, spatial indices).

---

## Setup & Installation Instructions

### 1. Database & PostGIS Setup
Ensure PostgreSQL is installed and PostGIS extension is enabled:
```sql
CREATE DATABASE kau_paddy_gis;
\c kau_paddy_gis;
CREATE EXTENSION IF NOT EXISTS postgis;
```

### 2. Run Backend Migrations & Seed Data
Navigate to the `backend/` directory:
```bash
cd backend
python -m venv venv
source venv/bin/activate  # On Windows use: venv\Scripts\activate
pip install -r requirements.txt

# Configure .env file
cp .env.example .env
# Update DATABASE_URL in .env if needed

# Apply migrations and seed data
psql -U postgres -d kau_paddy_gis -f migrations/001_initial_schema.sql
psql -U postgres -d kau_paddy_gis -f migrations/002_seed_data.sql

# Start FastAPI Server
uvicorn backend.app.main:app --reload --host 0.0.0.0 --port 8000
```

### 3. Run Flutter Mobile / Web App
Navigate to the `flutter_app/` directory:
```bash
cd flutter_app
flutter pub get

# Run on Android Emulator or Web
flutter run
```

---

## Demo Credentials
- **Farmer**: `rajan.farmer@gmail.com` / `password123`
- **Agricultural Officer**: `officer.mannuthy@kau.in` / `password123`
- **Administrator**: `admin@kau.in` / `password123`
