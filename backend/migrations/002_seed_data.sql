-- Insert Krishi Bhavan (Mannuthy Krishi Bhavan, Thrissur, Kerala)
INSERT INTO krishi_bhavans (id, name, district, jurisdiction_geom) VALUES
(1, 'Mannuthy Krishi Bhavan', 'Thrissur', ST_GeomFromText('MULTIPOLYGON(((76.27 10.53, 76.32 10.53, 76.32 10.58, 76.27 10.58, 76.27 10.53)))', 4326))
ON CONFLICT (id) DO NOTHING;

-- Insert Users (Password hash for 'password123' using bcrypt)
-- Hash: $2b$12$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW
INSERT INTO users (id, name, phone, email, password_hash, role, krishi_bhavan_id) VALUES
(1, 'Dr. Ananya Nair (Officer)', '9847012345', 'officer.mannuthy@kau.in', '$2b$12$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'officer', 1),
(2, 'Rajan P.K. (Farmer)', '9447123456', 'rajan.farmer@gmail.com', '$2b$12$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'farmer', 1),
(3, 'Sreedharan V. (Farmer)', '9747234567', 'sreedharan.farmer@gmail.com', '$2b$12$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'farmer', 1),
(4, 'Admin User', '9000000000', 'admin@kau.in', '$2b$12$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'admin', 1)
ON CONFLICT (id) DO NOTHING;

-- Insert Paddy Fields with GeoJSON MultiPolygons around Mannuthy (KAU Campus vicinity)
INSERT INTO paddy_fields (id, survey_number, farmer_id, krishi_bhavan_id, area_ha, crop_season, soil_type, geom, attributes) VALUES
(1, 'SY-142/2A', 2, 1, 0.7500, 'Virippu', 'Clay Loam',
 ST_GeomFromText('MULTIPOLYGON(((76.285 10.545, 76.290 10.545, 76.290 10.550, 76.285 10.550, 76.285 10.545)))', 4326),
 '{"variety": "Uma (MO 16)", "irrigation": "Canal", "fertilizer_status": "Applied NPK", "yield_estimate_tons": 3.2}'::jsonb),

(2, 'SY-143/1B', 2, 1, 1.2000, 'Mundakan', 'Alluvial Clay',
 ST_GeomFromText('MULTIPOLYGON(((76.292 10.546, 76.298 10.546, 76.298 10.552, 76.292 10.552, 76.292 10.546)))', 4326),
 '{"variety": "Jyothi", "irrigation": "Borewell", "fertilizer_status": "Pending Organic", "yield_estimate_tons": 5.0}'::jsonb),

(3, 'SY-155/4C', 3, 1, 0.5000, 'Puncha', 'Acid Sulfate',
 ST_GeomFromText('MULTIPOLYGON(((76.280 10.538, 76.284 10.538, 76.284 10.542, 76.280 10.542, 76.280 10.538)))', 4326),
 '{"variety": "Matta Thrissivaperoor", "irrigation": "Rainfed", "fertilizer_status": "Applied Lime", "yield_estimate_tons": 2.1}'::jsonb)
ON CONFLICT (id) DO NOTHING;

-- Insert Sample Announcements
INSERT INTO announcements (id, officer_id, krishi_bhavan_id, title, body) VALUES
(1, 1, 1, 'Paddy Harvesting Advisory - Virippu Season', 'Farmers in Mannuthy jurisdiction are advised to arrange combine harvesters ahead of expected monsoon showers next week.'),
(2, 1, 1, 'Pesticide Subsidy Distribution', 'Subsidy for organic neem-based pest control is available at Krishi Bhavan. Bring Aadhaar and land tax receipt.')
ON CONFLICT (id) DO NOTHING;

-- Insert Sample Messages
INSERT INTO messages (id, sender_id, receiver_id, body) VALUES
(1, 2, 1, 'Madam, my field SY-142/2A requires water pump inspection.'),
(2, 1, 2, 'Rajan, maintenance team has been notified and will visit tomorrow morning.')
ON CONFLICT (id) DO NOTHING;
