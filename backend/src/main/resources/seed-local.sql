-- Seed data for the local (H2) profile: universities in Yaoundé.
-- Mirrors the PostgreSQL V2 migration. Hibernate (ddl-auto=create) builds the tables; this runs after.

INSERT INTO app_user (firebase_uid, email, display_name, role, active, created_at) VALUES
    ('admin-dev', 'admin@unimatch.cm', 'Platform Admin', 'ADMIN',   TRUE, CURRENT_TIMESTAMP),
    ('student-1', 'ada@example.cm',     'Ada N.',        'STUDENT', TRUE, CURRENT_TIMESTAMP),
    ('student-2', 'bih@example.cm',     'Bih T.',        'STUDENT', TRUE, CURRENT_TIMESTAMP),
    ('student-3', 'che@example.cm',     'Che M.',        'STUDENT', TRUE, CURRENT_TIMESTAMP);

INSERT INTO school (name, category, description, city, region, address, latitude, longitude, tuition_fee, currency, website, phone, email, cover_image_url, approved, created_at) VALUES
    ('University of Yaoundé I', 'UNIVERSITY', 'Cameroon''s flagship public university, strong in sciences, medicine, the arts and engineering (incl. the National Advanced School of Engineering).', 'Yaoundé', 'Centre', 'Ngoa-Ekellé, Yaoundé', 3.8667, 11.4986, 50000, 'XAF', 'https://www.uy1.cm', NULL, NULL, NULL, TRUE, CURRENT_TIMESTAMP),
    ('University of Yaoundé II', 'UNIVERSITY', 'Public university based in Soa, specialised in law, economics, management, political science and governance.', 'Yaoundé', 'Centre', 'Soa, Yaoundé', 3.9810, 11.5650, 50000, 'XAF', NULL, NULL, NULL, NULL, TRUE, CURRENT_TIMESTAMP),
    ('Catholic University of Central Africa (UCAC)', 'UNIVERSITY', 'Private Catholic university renowned for management, law, social sciences and health.', 'Yaoundé', 'Centre', 'Nkolbisson, Yaoundé', 3.8730, 11.4380, 900000, 'XAF', 'https://www.ucac-icy.net', NULL, NULL, NULL, TRUE, CURRENT_TIMESTAMP),
    ('Protestant University of Central Africa (UPAC)', 'UNIVERSITY', 'Private university founded by Protestant churches, with faculties in theology, health sciences and management.', 'Yaoundé', 'Centre', 'Djoungolo, Yaoundé', 3.8900, 11.5210, 800000, 'XAF', NULL, NULL, NULL, NULL, TRUE, CURRENT_TIMESTAMP),
    ('The ICT University', 'UNIVERSITY', 'Private university focused on information and communication technology and business.', 'Yaoundé', 'Centre', 'Messassi, Yaoundé', 3.9180, 11.5360, 750000, 'XAF', 'https://ictuniversity.org', NULL, NULL, NULL, TRUE, CURRENT_TIMESTAMP),
    ('PKFokam Institute of Excellence', 'UNIVERSITY', 'Private institution of higher education in Yaoundé offering computing, business, banking and finance.', 'Yaoundé', 'Centre', 'Santa Barbara, Yaoundé', 3.8400, 11.5300, 700000, 'XAF', NULL, NULL, NULL, NULL, TRUE, CURRENT_TIMESTAMP);

INSERT INTO program (school_id, name, level, duration_months, tuition_fee)
SELECT id, 'BSc Computer Science', 'Bachelor', 36, 50000 FROM school WHERE name = 'University of Yaoundé I';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee)
SELECT id, 'Doctor of Medicine', 'Doctorate', 84, 50000 FROM school WHERE name = 'University of Yaoundé I';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee)
SELECT id, 'LLB Law', 'Bachelor', 36, 50000 FROM school WHERE name = 'University of Yaoundé II';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee)
SELECT id, 'BSc Economics', 'Bachelor', 36, 50000 FROM school WHERE name = 'University of Yaoundé II';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee)
SELECT id, 'BSc Management', 'Bachelor', 36, 900000 FROM school WHERE name = 'Catholic University of Central Africa (UCAC)';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee)
SELECT id, 'BSc Nursing', 'Bachelor', 36, 950000 FROM school WHERE name = 'Catholic University of Central Africa (UCAC)';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee)
SELECT id, 'BSc Health Sciences', 'Bachelor', 36, 850000 FROM school WHERE name = 'Protestant University of Central Africa (UPAC)';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee)
SELECT id, 'BSc Software Engineering', 'Bachelor', 48, 800000 FROM school WHERE name = 'The ICT University';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee)
SELECT id, 'MBA', 'Master', 24, 1200000 FROM school WHERE name = 'The ICT University';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee)
SELECT id, 'BSc Banking & Finance', 'Bachelor', 36, 700000 FROM school WHERE name = 'PKFokam Institute of Excellence';

INSERT INTO review (school_id, user_id, rating, comment, status, created_at)
SELECT s.id, u.id, 5, 'Great campus and supportive lecturers.', 'APPROVED', CURRENT_TIMESTAMP
FROM school s, app_user u WHERE s.name = 'University of Yaoundé I' AND u.firebase_uid = 'student-1';
INSERT INTO review (school_id, user_id, rating, comment, status, created_at)
SELECT s.id, u.id, 4, 'Strong programmes but large class sizes.', 'APPROVED', CURRENT_TIMESTAMP
FROM school s, app_user u WHERE s.name = 'University of Yaoundé I' AND u.firebase_uid = 'student-2';
INSERT INTO review (school_id, user_id, rating, comment, status, created_at)
SELECT s.id, u.id, 4, 'Good for tech, modern facilities.', 'APPROVED', CURRENT_TIMESTAMP
FROM school s, app_user u WHERE s.name = 'The ICT University' AND u.firebase_uid = 'student-1';
INSERT INTO review (school_id, user_id, rating, comment, status, created_at)
SELECT s.id, u.id, 5, 'Excellent reputation and strong discipline.', 'APPROVED', CURRENT_TIMESTAMP
FROM school s, app_user u WHERE s.name = 'Catholic University of Central Africa (UCAC)' AND u.firebase_uid = 'student-3';
