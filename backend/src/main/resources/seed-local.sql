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
    ('PKFokam Institute of Excellence', 'UNIVERSITY', 'Private institution of higher education in Yaoundé offering computing, business, banking and finance.', 'Yaoundé', 'Centre', 'Santa Barbara, Yaoundé', 3.8400, 11.5300, 700000, 'XAF', NULL, NULL, NULL, NULL, TRUE, CURRENT_TIMESTAMP),
    ('Yaoundé International Business School (YIBS)', 'UNIVERSITY', 'Affiliated with the University of Bamenda (UBa). Offers HND, Bachelor (BTech) and Master programmes across business & finance, management, communication, tourism & hotel management and computer engineering. Day & evening sessions.', 'Yaoundé', 'Centre', 'Bastos, Yaoundé', 3.8950, 11.5180, 850000, 'XAF', NULL, NULL, NULL, NULL, TRUE, CURRENT_TIMESTAMP),
    ('CITEC Higher Institute of Technology (CITEC)', 'UNIVERSITY', 'Private higher institute in Yaoundé offering programmes in information technology, networking, software engineering and business management.', 'Yaoundé', 'Centre', 'Nsam, Yaoundé', 3.8350, 11.5160, 600000, 'XAF', NULL, NULL, NULL, NULL, TRUE, CURRENT_TIMESTAMP),
    ('National Advanced School of Engineering (ENSPY)', 'UNIVERSITY', 'Public grande école of engineering (Polytechnique) under the University of Yaoundé I — trains engineers in civil, computer, electrical and telecommunications engineering.', 'Yaoundé', 'Centre', 'Melen, Yaoundé', 3.8620, 11.4940, 50000, 'XAF', NULL, NULL, NULL, NULL, TRUE, CURRENT_TIMESTAMP),
    ('National School of Administration and Magistracy (ENAM)', 'UNIVERSITY', 'Public professional school training senior civil servants and magistrates in administration, magistracy, customs and treasury.', 'Yaoundé', 'Centre', 'Quartier du Lac, Yaoundé', 3.8700, 11.5100, 50000, 'XAF', NULL, NULL, NULL, NULL, TRUE, CURRENT_TIMESTAMP),
    ('Sub-regional Institute of Statistics and Applied Economics (ISSEA)', 'UNIVERSITY', 'CEMAC sub-regional institute training statisticians, statistical engineers and applied economists for Central Africa.', 'Yaoundé', 'Centre', 'Nkolbisson, Yaoundé', 3.8740, 11.4350, 50000, 'XAF', NULL, NULL, NULL, NULL, TRUE, CURRENT_TIMESTAMP);

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
INSERT INTO program (school_id, name, level, duration_months, tuition_fee)
SELECT id, 'HND Accounting', 'Diploma', 24, 400000 FROM school WHERE name = 'Yaoundé International Business School (YIBS)';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee)
SELECT id, 'BTech Banking & Finance', 'Bachelor', 36, 550000 FROM school WHERE name = 'Yaoundé International Business School (YIBS)';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee)
SELECT id, 'BTech Software Engineering', 'Bachelor', 36, 600000 FROM school WHERE name = 'Yaoundé International Business School (YIBS)';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee)
SELECT id, 'MBA Business Administration', 'Master', 24, 850000 FROM school WHERE name = 'Yaoundé International Business School (YIBS)';
-- YIBS — School of Medical & Biomedical Sciences
INSERT INTO program (school_id, name, level, duration_months, tuition_fee)
SELECT id, 'HND Nursing', 'Diploma', 36, 600000 FROM school WHERE name = 'Yaoundé International Business School (YIBS)';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee)
SELECT id, 'HND Midwifery', 'Diploma', 36, 600000 FROM school WHERE name = 'Yaoundé International Business School (YIBS)';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee)
SELECT id, 'HND Medical Laboratory Sciences', 'Diploma', 36, 550000 FROM school WHERE name = 'Yaoundé International Business School (YIBS)';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee)
SELECT id, 'HND Pharmacy Technology', 'Diploma', 24, 500000 FROM school WHERE name = 'Yaoundé International Business School (YIBS)';
-- YIBS — School of Home Economics & Social Works
INSERT INTO program (school_id, name, level, duration_months, tuition_fee)
SELECT id, 'HND Bakery & Food Processing', 'Diploma', 24, 350000 FROM school WHERE name = 'Yaoundé International Business School (YIBS)';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee)
SELECT id, 'HND Beauty Care & Cosmetics', 'Diploma', 24, 300000 FROM school WHERE name = 'Yaoundé International Business School (YIBS)';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee)
SELECT id, 'HND Hairdressing', 'Diploma', 24, 300000 FROM school WHERE name = 'Yaoundé International Business School (YIBS)';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee)
SELECT id, 'HND Fashion Design & Clothing', 'Diploma', 24, 350000 FROM school WHERE name = 'Yaoundé International Business School (YIBS)';
-- YIBS — School of Computer Engineering
INSERT INTO program (school_id, name, level, duration_months, tuition_fee)
SELECT id, 'BTech Computer Science & Network', 'Bachelor', 36, 550000 FROM school WHERE name = 'Yaoundé International Business School (YIBS)';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee)
SELECT id, 'BTech Network & Security', 'Bachelor', 36, 600000 FROM school WHERE name = 'Yaoundé International Business School (YIBS)';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee)
SELECT id, 'HND Computer Graphics & Web Design', 'Diploma', 24, 450000 FROM school WHERE name = 'Yaoundé International Business School (YIBS)';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee)
SELECT id, 'BTech Cloud Computing & Virtualization', 'Bachelor', 36, 600000 FROM school WHERE name = 'Yaoundé International Business School (YIBS)';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee)
SELECT id, 'HND Software Engineering', 'Diploma', 24, 600000 FROM school WHERE name = 'CITEC Higher Institute of Technology (CITEC)';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee)
SELECT id, 'BSc Network & Security', 'Bachelor', 36, 650000 FROM school WHERE name = 'CITEC Higher Institute of Technology (CITEC)';

-- University of Yaoundé I (additional programmes)
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'BSc Mathematics', 'Bachelor', 36, 50000 FROM school WHERE name = 'University of Yaoundé I';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'BSc Physics', 'Bachelor', 36, 50000 FROM school WHERE name = 'University of Yaoundé I';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'LLB Law', 'Bachelor', 36, 50000 FROM school WHERE name = 'University of Yaoundé I';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'BA English Modern Letters', 'Bachelor', 36, 50000 FROM school WHERE name = 'University of Yaoundé I';
-- University of Yaoundé II
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'BSc Management', 'Bachelor', 36, 50000 FROM school WHERE name = 'University of Yaoundé II';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'BSc Political Science', 'Bachelor', 36, 50000 FROM school WHERE name = 'University of Yaoundé II';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'BSc International Relations', 'Bachelor', 36, 50000 FROM school WHERE name = 'University of Yaoundé II';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'BSc Accounting & Finance', 'Bachelor', 36, 50000 FROM school WHERE name = 'University of Yaoundé II';
-- Catholic University of Central Africa (UCAC)
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'LLB Law', 'Bachelor', 36, 900000 FROM school WHERE name = 'Catholic University of Central Africa (UCAC)';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'BSc Accounting', 'Bachelor', 36, 900000 FROM school WHERE name = 'Catholic University of Central Africa (UCAC)';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'BSc Social & Political Sciences', 'Bachelor', 36, 900000 FROM school WHERE name = 'Catholic University of Central Africa (UCAC)';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'Doctor of Medicine', 'Doctorate', 84, 1200000 FROM school WHERE name = 'Catholic University of Central Africa (UCAC)';
-- Protestant University of Central Africa (UPAC)
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'BTh Theology', 'Bachelor', 36, 800000 FROM school WHERE name = 'Protestant University of Central Africa (UPAC)';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'BSc Management', 'Bachelor', 36, 800000 FROM school WHERE name = 'Protestant University of Central Africa (UPAC)';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'BSc Nursing', 'Bachelor', 36, 850000 FROM school WHERE name = 'Protestant University of Central Africa (UPAC)';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'BSc Computer Science', 'Bachelor', 36, 800000 FROM school WHERE name = 'Protestant University of Central Africa (UPAC)';
-- The ICT University
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'BSc Information Technology', 'Bachelor', 36, 750000 FROM school WHERE name = 'The ICT University';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'BSc Cybersecurity', 'Bachelor', 36, 800000 FROM school WHERE name = 'The ICT University';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'BSc Business Administration', 'Bachelor', 36, 700000 FROM school WHERE name = 'The ICT University';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'MSc Information Systems', 'Master', 24, 1000000 FROM school WHERE name = 'The ICT University';
-- PKFokam Institute of Excellence
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'BSc Computer Science', 'Bachelor', 36, 700000 FROM school WHERE name = 'PKFokam Institute of Excellence';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'BSc Accounting', 'Bachelor', 36, 700000 FROM school WHERE name = 'PKFokam Institute of Excellence';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'BBA Business Administration', 'Bachelor', 36, 700000 FROM school WHERE name = 'PKFokam Institute of Excellence';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'BSc Marketing', 'Bachelor', 36, 700000 FROM school WHERE name = 'PKFokam Institute of Excellence';
-- CITEC Higher Institute of Technology
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'HND Computer Graphics & Web Design', 'Diploma', 24, 550000 FROM school WHERE name = 'CITEC Higher Institute of Technology (CITEC)';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'BSc Information Systems', 'Bachelor', 36, 650000 FROM school WHERE name = 'CITEC Higher Institute of Technology (CITEC)';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'HND Database Management', 'Diploma', 24, 550000 FROM school WHERE name = 'CITEC Higher Institute of Technology (CITEC)';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'BSc E-commerce & Digital Marketing', 'Bachelor', 36, 600000 FROM school WHERE name = 'CITEC Higher Institute of Technology (CITEC)';
-- National Advanced School of Engineering (ENSPY)
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'BEng Civil Engineering', 'Bachelor', 60, 50000 FROM school WHERE name = 'National Advanced School of Engineering (ENSPY)';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'BEng Computer Engineering', 'Bachelor', 60, 50000 FROM school WHERE name = 'National Advanced School of Engineering (ENSPY)';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'BEng Electrical Engineering', 'Bachelor', 60, 50000 FROM school WHERE name = 'National Advanced School of Engineering (ENSPY)';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'BEng Telecommunications Engineering', 'Bachelor', 60, 50000 FROM school WHERE name = 'National Advanced School of Engineering (ENSPY)';
-- National School of Administration and Magistracy (ENAM)
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'Public Administration', 'Master', 24, 50000 FROM school WHERE name = 'National School of Administration and Magistracy (ENAM)';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'Magistracy', 'Master', 24, 50000 FROM school WHERE name = 'National School of Administration and Magistracy (ENAM)';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'Customs Administration', 'Diploma', 24, 50000 FROM school WHERE name = 'National School of Administration and Magistracy (ENAM)';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'Treasury & Finance', 'Diploma', 24, 50000 FROM school WHERE name = 'National School of Administration and Magistracy (ENAM)';
-- Sub-regional Institute of Statistics and Applied Economics (ISSEA)
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'Statistical Engineering', 'Master', 60, 50000 FROM school WHERE name = 'Sub-regional Institute of Statistics and Applied Economics (ISSEA)';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'Applied Statistics', 'Bachelor', 36, 50000 FROM school WHERE name = 'Sub-regional Institute of Statistics and Applied Economics (ISSEA)';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'Demography', 'Master', 24, 50000 FROM school WHERE name = 'Sub-regional Institute of Statistics and Applied Economics (ISSEA)';
INSERT INTO program (school_id, name, level, duration_months, tuition_fee) SELECT id, 'Economic Analysis', 'Bachelor', 36, 50000 FROM school WHERE name = 'Sub-regional Institute of Statistics and Applied Economics (ISSEA)';

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
