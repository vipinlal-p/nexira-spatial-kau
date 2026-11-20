# Nexira Spatial – KAU Paddy Field Mobile GIS

A production-quality mobile GIS application built for the Department of Soil Science, Kerala Agricultural University (KAU), managing paddy field survey parcels, Krishi Bhavan announcements, 1:1 farmer-officer communications, and comprehensive administrative controls.

---

## Architecture & Tech Stack
- **Mobile App**: Native Android (Kotlin, Jetpack Compose, Material 3, Material Icons Extended).
- **Mapping Engine**: `osmdroid` (OpenStreetMap-based spatial map rendering) using Esri World Street Map tiles (clean, Google Maps style appearance with zero API key restrictions).
- **Spatial Data**: Real-time survey number polygon shapefile rendering, bounding box overlays, and live GPS location tracking.
- **Backend & API**: Python FastAPI (`uvicorn`, `SQLAlchemy`, `GeoAlchemy2`, `PyJWT`, `passlib`) with PostgreSQL + PostGIS spatial migration schemas.

---

## Core Features
1. **Role-Based Access Control (RBAC)**:
   - **Farmer**: View associated paddy fields by survey number, zoom to plot, view announcements, and query officers.
   - **Agricultural Officer**: View and query all paddy fields in jurisdiction, broadcast public advisories, and reply to farmer inquiries.
   - **Administrator**: Comprehensive admin dashboard featuring GIS shapefile/GeoJSON ingestion, user role management, Krishi Bhavan district jurisdiction management, and communication audit logs.
2. **Interactive GIS Map**:
   - Real-time plotting of paddy field survey number polygons with distinct symbology.
   - Clickable survey numbers in list view that pan/zoom directly to the selected parcel on the map.
   - On-demand current location GPS centering button.
3. **Communication & Announcements**:
   - Krishi Bhavan public broadcast advisories.
   - 1:1 messaging channel between farmers and agricultural officers.

---

## Getting Started & Build Instructions

### 1. Android App (Kotlin / Compose)
- Open the project in **Android Studio**.
- Ensure Android SDK 37 / minSdk 24 is configured.
- Build and run the app:
  ```bash
  ./gradlew assembleDebug
  ```
- Install the APK (`app/build/outputs/apk/debug/app-debug.apk`) onto your device or emulator.

### 2. Backend API (Python FastAPI + PostGIS)
- Navigate to `backend/`:
  ```bash
  cd backend
  pip install -r requirements.txt
  python -m uvicorn app.main:app --reload
  ```

---

## Demo Credentials
- **Farmer**: `rajan.farmer@gmail.com` / `password123`
- **Agricultural Officer**: `officer.mannuthy@kau.in` / `password123`
- **Administrator**: `admin@kau.in` / `password123`
