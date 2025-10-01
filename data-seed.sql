-- Dummy Data Script for Simple Analytics
-- This script populates the database with sample data for testing

-- Clear existing data (in correct order due to foreign key constraints)
DELETE FROM api_key;
DELETE FROM application;
DELETE FROM tenet;
DELETE FROM plan;

-- Insert Plans
INSERT INTO plan (id, name, monthly_credit_limit, rate_limit, start_date, duration, cost) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'Free Tier', 1000, 10, '2025-01-01', 'P30D', 0),
('550e8400-e29b-41d4-a716-446655440002', 'Starter Plan', 10000, 50, '2025-01-01', 'P30D', 29),
('550e8400-e29b-41d4-a716-446655440003', 'Professional', 100000, 200, '2025-01-01', 'P30D', 99),
('550e8400-e29b-41d4-a716-446655440004', 'Enterprise', 1000000, 1000, '2025-01-01', 'P30D', 299),
('550e8400-e29b-41d4-a716-446655440005', 'Custom Plan', 5000000, 5000, '2025-01-01', 'P30D', 999);

-- Insert Tenets
INSERT INTO tenet (id, name, created_at, plan_id) VALUES
('660e8400-e29b-41d4-a716-446655440001', 'Acme Corporation', '2025-09-01 10:00:00', '550e8400-e29b-41d4-a716-446655440003'),
('660e8400-e29b-41d4-a716-446655440002', 'TechStart Inc', '2025-09-15 14:30:00', '550e8400-e29b-41d4-a716-446655440002'),
('660e8400-e29b-41d4-a716-446655440003', 'Global Analytics Ltd', '2025-08-20 09:15:00', '550e8400-e29b-41d4-a716-446655440004'),
('660e8400-e29b-41d4-a716-446655440004', 'Startup Ventures', '2025-09-25 16:45:00', '550e8400-e29b-41d4-a716-446655440001'),
('660e8400-e29b-41d4-a716-446655440005', 'Enterprise Solutions', '2025-09-10 11:20:00', '550e8400-e29b-41d4-a716-446655440004'),
('660e8400-e29b-41d4-a716-446655440006', 'Digital Innovations', '2025-09-05 13:00:00', '550e8400-e29b-41d4-a716-446655440002'),
('660e8400-e29b-41d4-a716-446655440007', 'Data Insights Co', '2025-09-18 08:30:00', '550e8400-e29b-41d4-a716-446655440003');

-- Insert Applications
INSERT INTO application (id, name, source, created_at, credits_used, tenet_id) VALUES
-- Acme Corporation Applications
('770e8400-e29b-41d4-a716-446655440001', 'Acme Web Analytics', 'web', '2025-09-02 10:30:00', 15000, '660e8400-e29b-41d4-a716-446655440001'),
('770e8400-e29b-41d4-a716-446655440002', 'Acme Mobile App', 'mobile', '2025-09-03 11:00:00', 8500, '660e8400-e29b-41d4-a716-446655440001'),
('770e8400-e29b-41d4-a716-446655440003', 'Acme API Gateway', 'api', '2025-09-04 09:45:00', 22000, '660e8400-e29b-41d4-a716-446655440001'),

-- TechStart Inc Applications  
('770e8400-e29b-41d4-a716-446655440004', 'TechStart Dashboard', 'web', '2025-09-16 15:00:00', 3200, '660e8400-e29b-41d4-a716-446655440002'),
('770e8400-e29b-41d4-a716-446655440005', 'TechStart Mobile', 'mobile', '2025-09-17 10:15:00', 1800, '660e8400-e29b-41d4-a716-446655440002'),

-- Global Analytics Ltd Applications
('770e8400-e29b-41d4-a716-446655440006', 'Global Web Platform', 'web', '2025-08-21 10:00:00', 150000, '660e8400-e29b-41d4-a716-446655440003'),
('770e8400-e29b-41d4-a716-446655440007', 'Global Mobile Analytics', 'mobile', '2025-08-22 11:30:00', 85000, '660e8400-e29b-41d4-a716-446655440003'),
('770e8400-e29b-41d4-a716-446655440008', 'Global Data Pipeline', 'backend', '2025-08-23 14:20:00', 200000, '660e8400-e29b-41d4-a716-446655440003'),
('770e8400-e29b-41d4-a716-446655440009', 'Global IoT Analytics', 'iot', '2025-08-24 16:45:00', 120000, '660e8400-e29b-41d4-a716-446655440003'),

-- Startup Ventures Applications
('770e8400-e29b-41d4-a716-446655440010', 'Startup MVP Analytics', 'web', '2025-09-26 17:00:00', 450, '660e8400-e29b-41d4-a716-446655440004'),

-- Enterprise Solutions Applications
('770e8400-e29b-41d4-a716-446655440011', 'Enterprise Portal', 'web', '2025-09-11 12:00:00', 180000, '660e8400-e29b-41d4-a716-446655440005'),
('770e8400-e29b-41d4-a716-446655440012', 'Enterprise Mobile Suite', 'mobile', '2025-09-12 13:30:00', 95000, '660e8400-e29b-41d4-a716-446655440005'),
('770e8400-e29b-41d4-a716-446655440013', 'Enterprise API Analytics', 'api', '2025-09-13 15:45:00', 250000, '660e8400-e29b-41d4-a716-446655440005'),

-- Digital Innovations Applications
('770e8400-e29b-41d4-a716-446655440014', 'Digital Web Tracker', 'web', '2025-09-06 14:00:00', 5500, '660e8400-e29b-41d4-a716-446655440006'),
('770e8400-e29b-41d4-a716-446655440015', 'Digital App Analytics', 'mobile', '2025-09-07 15:30:00', 3200, '660e8400-e29b-41d4-a716-446655440006'),

-- Data Insights Co Applications
('770e8400-e29b-41d4-a716-446655440016', 'Data Insights Dashboard', 'web', '2025-09-19 09:00:00', 12000, '660e8400-e29b-41d4-a716-446655440007'),
('770e8400-e29b-41d4-a716-446655440017', 'Data Insights API', 'api', '2025-09-20 10:30:00', 18000, '660e8400-e29b-41d4-a716-446655440007');

-- Insert API Keys
INSERT INTO api_key (id, name, created_at, application_id) VALUES
-- Acme Web Analytics API Keys
('880e8400-e29b-41d4-a716-446655440001', 'Production API Key', '2025-09-02 10:35:00', '770e8400-e29b-41d4-a716-446655440001'),
('880e8400-e29b-41d4-a716-446655440002', 'Development API Key', '2025-09-02 10:36:00', '770e8400-e29b-41d4-a716-446655440001'),
('880e8400-e29b-41d4-a716-446655440003', 'Testing API Key', '2025-09-02 10:37:00', '770e8400-e29b-41d4-a716-446655440001'),

-- Acme Mobile App API Keys
('880e8400-e29b-41d4-a716-446655440004', 'Mobile Prod Key', '2025-09-03 11:05:00', '770e8400-e29b-41d4-a716-446655440002'),
('880e8400-e29b-41d4-a716-446655440005', 'Mobile Dev Key', '2025-09-03 11:06:00', '770e8400-e29b-41d4-a716-446655440002'),

-- Acme API Gateway API Keys
('880e8400-e29b-41d4-a716-446655440006', 'Gateway Master Key', '2025-09-04 09:50:00', '770e8400-e29b-41d4-a716-446655440003'),
('880e8400-e29b-41d4-a716-446655440007', 'Gateway Service Key', '2025-09-04 09:51:00', '770e8400-e29b-41d4-a716-446655440003'),
('880e8400-e29b-41d4-a716-446655440008', 'Gateway Analytics Key', '2025-09-04 09:52:00', '770e8400-e29b-41d4-a716-446655440003'),

-- TechStart Dashboard API Keys
('880e8400-e29b-41d4-a716-446655440009', 'Dashboard Main Key', '2025-09-16 15:05:00', '770e8400-e29b-41d4-a716-446655440004'),
('880e8400-e29b-41d4-a716-446655440010', 'Dashboard Backup Key', '2025-09-16 15:06:00', '770e8400-e29b-41d4-a716-446655440004'),

-- TechStart Mobile API Keys
('880e8400-e29b-41d4-a716-446655440011', 'Mobile Primary Key', '2025-09-17 10:20:00', '770e8400-e29b-41d4-a716-446655440005'),

-- Global Web Platform API Keys
('880e8400-e29b-41d4-a716-446655440012', 'Global Web Prod', '2025-08-21 10:05:00', '770e8400-e29b-41d4-a716-446655440006'),
('880e8400-e29b-41d4-a716-446655440013', 'Global Web Staging', '2025-08-21 10:06:00', '770e8400-e29b-41d4-a716-446655440006'),
('880e8400-e29b-41d4-a716-446655440014', 'Global Web Dev', '2025-08-21 10:07:00', '770e8400-e29b-41d4-a716-446655440006'),
('880e8400-e29b-41d4-a716-446655440015', 'Global Web Test', '2025-08-21 10:08:00', '770e8400-e29b-41d4-a716-446655440006'),

-- Global Mobile Analytics API Keys
('880e8400-e29b-41d4-a716-446655440016', 'Global Mobile iOS', '2025-08-22 11:35:00', '770e8400-e29b-41d4-a716-446655440007'),
('880e8400-e29b-41d4-a716-446655440017', 'Global Mobile Android', '2025-08-22 11:36:00', '770e8400-e29b-41d4-a716-446655440007'),
('880e8400-e29b-41d4-a716-446655440018', 'Global Mobile Web', '2025-08-22 11:37:00', '770e8400-e29b-41d4-a716-446655440007'),

-- Global Data Pipeline API Keys
('880e8400-e29b-41d4-a716-446655440019', 'Pipeline Ingestion Key', '2025-08-23 14:25:00', '770e8400-e29b-41d4-a716-446655440008'),
('880e8400-e29b-41d4-a716-446655440020', 'Pipeline Processing Key', '2025-08-23 14:26:00', '770e8400-e29b-41d4-a716-446655440008'),
('880e8400-e29b-41d4-a716-446655440021', 'Pipeline Export Key', '2025-08-23 14:27:00', '770e8400-e29b-41d4-a716-446655440008'),

-- Global IoT Analytics API Keys
('880e8400-e29b-41d4-a716-446655440022', 'IoT Sensor Key', '2025-08-24 16:50:00', '770e8400-e29b-41d4-a716-446655440009'),
('880e8400-e29b-41d4-a716-446655440023', 'IoT Gateway Key', '2025-08-24 16:51:00', '770e8400-e29b-41d4-a716-446655440009'),

-- Startup MVP Analytics API Keys
('880e8400-e29b-41d4-a716-446655440024', 'MVP Primary Key', '2025-09-26 17:05:00', '770e8400-e29b-41d4-a716-446655440010'),

-- Enterprise Portal API Keys
('880e8400-e29b-41d4-a716-446655440025', 'Enterprise Portal Prod', '2025-09-11 12:05:00', '770e8400-e29b-41d4-a716-446655440011'),
('880e8400-e29b-41d4-a716-446655440026', 'Enterprise Portal Staging', '2025-09-11 12:06:00', '770e8400-e29b-41d4-a716-446655440011'),
('880e8400-e29b-41d4-a716-446655440027', 'Enterprise Portal Dev', '2025-09-11 12:07:00', '770e8400-e29b-41d4-a716-446655440011'),

-- Enterprise Mobile Suite API Keys
('880e8400-e29b-41d4-a716-446655440028', 'Enterprise Mobile Prod', '2025-09-12 13:35:00', '770e8400-e29b-41d4-a716-446655440012'),
('880e8400-e29b-41d4-a716-446655440029', 'Enterprise Mobile Dev', '2025-09-12 13:36:00', '770e8400-e29b-41d4-a716-446655440012'),

-- Enterprise API Analytics API Keys
('880e8400-e29b-41d4-a716-446655440030', 'Enterprise API Master', '2025-09-13 15:50:00', '770e8400-e29b-41d4-a716-446655440013'),
('880e8400-e29b-41d4-a716-446655440031', 'Enterprise API Service', '2025-09-13 15:51:00', '770e8400-e29b-41d4-a716-446655440013'),
('880e8400-e29b-41d4-a716-446655440032', 'Enterprise API Monitor', '2025-09-13 15:52:00', '770e8400-e29b-41d4-a716-446655440013'),

-- Digital Web Tracker API Keys
('880e8400-e29b-41d4-a716-446655440033', 'Digital Web Primary', '2025-09-06 14:05:00', '770e8400-e29b-41d4-a716-446655440014'),
('880e8400-e29b-41d4-a716-446655440034', 'Digital Web Secondary', '2025-09-06 14:06:00', '770e8400-e29b-41d4-a716-446655440014'),

-- Digital App Analytics API Keys
('880e8400-e29b-41d4-a716-446655440035', 'Digital App Key', '2025-09-07 15:35:00', '770e8400-e29b-41d4-a716-446655440015'),

-- Data Insights Dashboard API Keys
('880e8400-e29b-41d4-a716-446655440036', 'Insights Dashboard Prod', '2025-09-19 09:05:00', '770e8400-e29b-41d4-a716-446655440016'),
('880e8400-e29b-41d4-a716-446655440037', 'Insights Dashboard Test', '2025-09-19 09:06:00', '770e8400-e29b-41d4-a716-446655440016'),

-- Data Insights API API Keys
('880e8400-e29b-41d4-a716-446655440038', 'Insights API Primary', '2025-09-20 10:35:00', '770e8400-e29b-41d4-a716-446655440017'),
('880e8400-e29b-41d4-a716-446655440039', 'Insights API Secondary', '2025-09-20 10:36:00', '770e8400-e29b-41d4-a716-446655440017'),
('880e8400-e29b-41d4-a716-446655440040', 'Insights API Backup', '2025-09-20 10:37:00', '770e8400-e29b-41d4-a716-446655440017');

-- Display summary
SELECT 
    'Data Summary' as info,
    (SELECT COUNT(*) FROM plan) as plans_count,
    (SELECT COUNT(*) FROM tenet) as tenets_count,
    (SELECT COUNT(*) FROM application) as applications_count,
    (SELECT COUNT(*) FROM api_key) as api_keys_count;