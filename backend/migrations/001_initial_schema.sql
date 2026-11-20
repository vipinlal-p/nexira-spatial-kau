-- Enable PostGIS Extension
CREATE EXTENSION IF NOT EXISTS postgis;

-- Krishi Bhavans Table
CREATE TABLE IF NOT EXISTS krishi_bhavans (
    id SERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    district VARCHAR(100) NOT NULL,
    jurisdiction_geom GEOMETRY(MultiPolygon, 4326) NOT NULL
);

-- Users Table (Farmers, Officers, Admins)
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    phone VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (role IN ('farmer', 'officer', 'admin')),
    krishi_bhavan_id INTEGER REFERENCES krishi_bhavans(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Paddy Fields Table with PostGIS MultiPolygon and JSONB attributes
CREATE TABLE IF NOT EXISTS paddy_fields (
    id SERIAL PRIMARY KEY,
    survey_number VARCHAR(100) NOT NULL,
    farmer_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    krishi_bhavan_id INTEGER REFERENCES krishi_bhavans(id) ON DELETE CASCADE,
    area_ha NUMERIC(10, 4) NOT NULL,
    crop_season VARCHAR(50) NOT NULL, -- e.g., 'Virippu', 'Mundakan', 'Puncha'
    soil_type VARCHAR(100) NOT NULL,
    geom GEOMETRY(MultiPolygon, 4326) NOT NULL,
    attributes JSONB DEFAULT '{}'::jsonb,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Spatial Indexes for Performance
CREATE INDEX IF NOT EXISTS idx_krishi_bhavans_geom ON krishi_bhavans USING GIST (jurisdiction_geom);
CREATE INDEX IF NOT EXISTS idx_paddy_fields_geom ON paddy_fields USING GIST (geom);
CREATE INDEX IF NOT EXISTS idx_paddy_fields_survey ON paddy_fields (survey_number);

-- Announcements Table
CREATE TABLE IF NOT EXISTS announcements (
    id SERIAL PRIMARY KEY,
    officer_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    krishi_bhavan_id INTEGER REFERENCES krishi_bhavans(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    body TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Messages Table (1:1 messaging between Farmers and Officers)
CREATE TABLE IF NOT EXISTS messages (
    id SERIAL PRIMARY KEY,
    sender_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    receiver_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    body TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP WITH TIME ZONE NULL
);

CREATE INDEX IF NOT EXISTS idx_messages_sender_receiver ON messages (sender_id, receiver_id);
