-- Seed data for the local (H2) profile. Mirrors the PostgreSQL V2 migration.
-- Hibernate (ddl-auto=create) builds the tables; this runs afterwards.

INSERT INTO app_user (firebase_uid, email, display_name, role, active, created_at) VALUES
    ('admin-dev', 'admin@schoolfinder.cm', 'Platform Admin', 'ADMIN',   TRUE, CURRENT_TIMESTAMP),
    ('student-1', 'ada@example.cm',         'Ada N.',        'STUDENT', TRUE, CURRENT_TIMESTAMP),
    ('student-2', 'bih@example.cm',         'Bih T.',        'STUDENT', TRUE, CURRENT_TIMESTAMP),
    ('student-3', 'che@example.cm',         'Che M.',        'STUDENT', TRUE, CURRENT_TIMESTAMP);

INSERT INTO school (name, category, description, city, region, address, latitude, longitude, tuition_fee, currency, website, phone, email, cover_image_url, approved, created_at) VALUES
    ('University of Buea', 'UNIVERSITY', 'Anglo-Saxon state university known for science, technology and health programmes.', 'Buea', 'South-West', 'Molyko, Buea', 4.1559, 9.2891, 50000, 'XAF', 'https://www.ubuea.cm', '+237 233 32 21 34', 'info@ubuea.cm', NULL, TRUE, CURRENT_TIMESTAMP),
    ('University of Yaoundé I', 'UNIVERSITY', 'One of Cameroon''s oldest and largest universities, strong in sciences and medicine.', 'Yaoundé', 'Centre', 'Ngoa-Ekelle, Yaoundé', 3.8634, 11.5012, 50000, 'XAF', 'https://www.uy1.uninet.cm', '+237 222 23 44 91', 'contact@uy1.cm', NULL, TRUE, CURRENT_TIMESTAMP),
    ('University of Douala', 'UNIVERSITY', 'Major state university with strong business, engineering and economics faculties.', 'Douala', 'Littoral', 'Bali, Douala', 4.0470, 9.6890, 50000, 'XAF', 'https://www.univ-douala.cm', '+237 233 40 75 69', 'info@univ-douala.cm', NULL, TRUE, CURRENT_TIMESTAMP),
    ('The University of Bamenda', 'UNIVERSITY', 'State university serving the North-West region across multiple campuses.', 'Bambili', 'North-West', 'Bambili, Bamenda', 5.9930, 10.2510, 50000, 'XAF', 'https://www.uniba.cm', '+237 233 36 27 65', 'info@uniba.cm', NULL, TRUE, CURRENT_TIMESTAMP),
    ('Catholic University of Central Africa', 'UNIVERSITY', 'Private Catholic university (UCAC) recognised for management, law and social sciences.', 'Yaoundé', 'Centre', 'Nkolbisson, Yaoundé', 3.8730, 11.4380, 900000, 'XAF', 'https://www.ucac-icy.net', '+237 222 23 74 00', 'info@ucac.cm', NULL, TRUE, CURRENT_TIMESTAMP),
    ('The ICT University', 'UNIVERSITY', 'Private university focused on information and communication technology.', 'Yaoundé', 'Centre', 'Messassi, Yaoundé', 3.9180, 11.5360, 750000, 'XAF', 'https://ictuniversity.org', '+237 242 61 23 23', 'info@ictuniversity.edu.cm', NULL, TRUE, CURRENT_TIMESTAMP),
    ('Siantou Higher Institute', 'VOCATIONAL', 'Higher professional institute offering diplomas in IT, health and business.', 'Yaoundé', 'Centre', 'Elig-Essono, Yaoundé', 3.8760, 11.5210, 450000, 'XAF', 'https://siantou.com', '+237 222 22 12 34', 'contact@siantou.cm', NULL, TRUE, CURRENT_TIMESTAMP),
    ('Government Bilingual High School Molyko', 'HIGH_SCHOOL', 'Large public bilingual secondary and high school in Buea.', 'Buea', 'South-West', 'Molyko, Buea', 4.1520, 9.2880, 25000, 'XAF', NULL, '+237 233 32 20 10', NULL, NULL, TRUE, CURRENT_TIMESTAMP),
    ('Saker Baptist College', 'SECONDARY', 'Reputable girls'' boarding secondary school in Limbe.', 'Limbe', 'South-West', 'Bonjongo Road, Limbe', 4.0210, 9.2050, 350000, 'XAF', NULL, '+237 233 33 21 88', 'info@sakerbaptist.cm', NULL, TRUE, CURRENT_TIMESTAMP),
    ('Sacred Heart College Mankon', 'SECONDARY', 'Catholic boys'' secondary school in Bamenda with a long academic tradition.', 'Bamenda', 'North-West', 'Mankon, Bamenda', 5.9610, 10.1460, 300000, 'XAF', NULL, '+237 233 36 11 22', NULL, NULL, TRUE, CURRENT_TIMESTAMP);

INSERT INTO program (school_id, name, level, duration_months, tuition_fee)
SELECT id, 'BSc Computer Science', 'Bachelor', 36, 60000 FROM school WHERE name = 'University of Buea';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee)
SELECT id, 'BSc Nursing', 'Bachelor', 36, 80000 FROM school WHERE name = 'University of Buea';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee)
SELECT id, 'MD Medicine', 'Doctorate', 84, 100000 FROM school WHERE name = 'University of Yaoundé I';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee)
SELECT id, 'BSc Economics', 'Bachelor', 36, 50000 FROM school WHERE name = 'University of Douala';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee)
SELECT id, 'BSc Software Engineering', 'Bachelor', 48, 800000 FROM school WHERE name = 'The ICT University';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee)
SELECT id, 'HND Software Engineering', 'Diploma', 24, 450000 FROM school WHERE name = 'Siantou Higher Institute';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee)
SELECT id, 'GCE A-Level Science', 'A-Level', 24, 25000 FROM school WHERE name = 'Government Bilingual High School Molyko';

INSERT INTO review (school_id, user_id, rating, comment, status, created_at)
SELECT s.id, u.id, 5, 'Great campus and supportive lecturers.', 'APPROVED', CURRENT_TIMESTAMP
FROM school s, app_user u WHERE s.name = 'University of Buea' AND u.firebase_uid = 'student-1';
INSERT INTO review (school_id, user_id, rating, comment, status, created_at)
SELECT s.id, u.id, 4, 'Strong programmes but large class sizes.', 'APPROVED', CURRENT_TIMESTAMP
FROM school s, app_user u WHERE s.name = 'University of Buea' AND u.firebase_uid = 'student-2';
INSERT INTO review (school_id, user_id, rating, comment, status, created_at)
SELECT s.id, u.id, 4, 'Good for tech, modern facilities.', 'APPROVED', CURRENT_TIMESTAMP
FROM school s, app_user u WHERE s.name = 'The ICT University' AND u.firebase_uid = 'student-1';
INSERT INTO review (school_id, user_id, rating, comment, status, created_at)
SELECT s.id, u.id, 5, 'Excellent academic results year after year.', 'APPROVED', CURRENT_TIMESTAMP
FROM school s, app_user u WHERE s.name = 'Saker Baptist College' AND u.firebase_uid = 'student-3';
