// Lightweight dev stand-in for the School Finder backend.
// Pure Node.js (no dependencies, no install) — serves the same REST contract and the same
// Cameroon seed data as the real Spring Boot API, so you can run/test locally without a JVM.
// Auth uses the same dev convention: the X-Debug-Uid header identifies the caller.
//
// Run:  node tools/mock-server.mjs       (listens on http://localhost:8080)
// This is NOT the production backend — the Kotlin/Spring app in backend/ is. Use Render or a
// JDK to run that one. This file just unblocks local testing on a machine without Java.

import http from 'node:http';

const PORT = process.env.PORT || 8080;

// ----------------------------- Seed data -----------------------------
const NOW = '2026-01-01T08:00:00Z';

const schools = [
  { id: 1, name: 'University of Yaoundé I', category: 'UNIVERSITY', description: "Cameroon's flagship public university, strong in sciences, medicine, the arts and engineering (incl. the National Advanced School of Engineering).", city: 'Yaoundé', region: 'Centre', address: 'Ngoa-Ekellé, Yaoundé', latitude: 3.8667, longitude: 11.4986, tuitionFee: 50000, currency: 'XAF', website: 'https://www.uy1.cm', phone: null, email: null, coverImageUrl: null },
  { id: 2, name: 'University of Yaoundé II', category: 'UNIVERSITY', description: 'Public university based in Soa, specialised in law, economics, management, political science and governance.', city: 'Yaoundé', region: 'Centre', address: 'Soa, Yaoundé', latitude: 3.9810, longitude: 11.5650, tuitionFee: 50000, currency: 'XAF', website: null, phone: null, email: null, coverImageUrl: null },
  { id: 3, name: 'Catholic University of Central Africa (UCAC)', category: 'UNIVERSITY', description: 'Private Catholic university renowned for management, law, social sciences and health.', city: 'Yaoundé', region: 'Centre', address: 'Nkolbisson, Yaoundé', latitude: 3.8730, longitude: 11.4380, tuitionFee: 900000, currency: 'XAF', website: 'https://www.ucac-icy.net', phone: null, email: null, coverImageUrl: null },
  { id: 4, name: 'Protestant University of Central Africa (UPAC)', category: 'UNIVERSITY', description: 'Private university founded by Protestant churches, with faculties in theology, health sciences and management.', city: 'Yaoundé', region: 'Centre', address: 'Djoungolo, Yaoundé', latitude: 3.8900, longitude: 11.5210, tuitionFee: 800000, currency: 'XAF', website: null, phone: null, email: null, coverImageUrl: null },
  { id: 5, name: 'The ICT University', category: 'UNIVERSITY', description: 'Private university focused on information and communication technology and business.', city: 'Yaoundé', region: 'Centre', address: 'Messassi, Yaoundé', latitude: 3.9180, longitude: 11.5360, tuitionFee: 750000, currency: 'XAF', website: 'https://ictuniversity.org', phone: null, email: null, coverImageUrl: null },
  { id: 6, name: 'PKFokam Institute of Excellence', category: 'UNIVERSITY', description: 'Private institution of higher education in Yaoundé offering computing, business, banking and finance.', city: 'Yaoundé', region: 'Centre', address: 'Santa Barbara, Yaoundé', latitude: 3.8400, longitude: 11.5300, tuitionFee: 700000, currency: 'XAF', website: null, phone: null, email: null, coverImageUrl: null },
  { id: 7, name: 'Yaoundé International Business School (YIBS)', category: 'UNIVERSITY', description: 'Affiliated with the University of Bamenda (UBa). Offers HND, Bachelor (BTech) and Master programmes across business & finance, management, communication, tourism & hotel management and computer engineering. Day & evening sessions.', city: 'Yaoundé', region: 'Centre', address: 'Bastos, Yaoundé', latitude: 3.8950, longitude: 11.5180, tuitionFee: 850000, currency: 'XAF', website: null, phone: null, email: null, coverImageUrl: null },
  { id: 8, name: 'CITEC Higher Institute of Technology (CITEC)', category: 'UNIVERSITY', description: 'Private higher institute in Yaoundé offering programmes in information technology, networking, software engineering and business management.', city: 'Yaoundé', region: 'Centre', address: 'Nsam, Yaoundé', latitude: 3.8350, longitude: 11.5160, tuitionFee: 600000, currency: 'XAF', website: null, phone: null, email: null, coverImageUrl: null },
  { id: 9, name: 'National Advanced School of Engineering (ENSPY)', category: 'UNIVERSITY', description: 'Public grande école of engineering (Polytechnique) under the University of Yaoundé I — trains engineers in civil, computer, electrical and telecommunications engineering.', city: 'Yaoundé', region: 'Centre', address: 'Melen, Yaoundé', latitude: 3.8620, longitude: 11.4940, tuitionFee: 50000, currency: 'XAF', website: null, phone: null, email: null, coverImageUrl: null },
  { id: 10, name: 'National School of Administration and Magistracy (ENAM)', category: 'UNIVERSITY', description: 'Public professional school training senior civil servants and magistrates in administration, magistracy, customs and treasury.', city: 'Yaoundé', region: 'Centre', address: 'Quartier du Lac, Yaoundé', latitude: 3.8700, longitude: 11.5100, tuitionFee: 50000, currency: 'XAF', website: null, phone: null, email: null, coverImageUrl: null },
  { id: 11, name: 'Sub-regional Institute of Statistics and Applied Economics (ISSEA)', category: 'UNIVERSITY', description: 'CEMAC sub-regional institute training statisticians, statistical engineers and applied economists for Central Africa.', city: 'Yaoundé', region: 'Centre', address: 'Nkolbisson, Yaoundé', latitude: 3.8740, longitude: 11.4350, tuitionFee: 50000, currency: 'XAF', website: null, phone: null, email: null, coverImageUrl: null },
];

// Brief history per university (by school id)
const histories = {
  1: "Established in 1962 as Cameroon's first university and reorganised into the University of Yaoundé I in 1993, it is the country's oldest and largest university, renowned for the sciences, medicine, the arts and engineering.",
  2: "Created in 1993 from the reform that split the University of Yaoundé, and based in Soa near Yaoundé, it specialises in law, economics, management, political science and governance.",
  3: "Founded in 1989 by the Catholic bishops of Central Africa and opened in 1991, UCAC is a leading private institution in Yaoundé, recognised for management, law, social sciences and health.",
  4: "Established in 2007 by the Protestant churches of Central Africa, UPAC in Yaoundé offers programmes in theology, health sciences and management.",
  5: "A private university with American academic roots, The ICT University operates from Yaoundé with a focus on information and communication technology, computing and business, offering on-campus and blended programmes.",
  6: "Founded by Dr Paul K. Fokam, the entrepreneur behind Afriland First Bank, the PKFokam Institute of Excellence in Yaoundé trains students in computing, business, banking and finance with a strong entrepreneurship focus.",
  7: "Yaoundé International Business School (YIBS) is a private bilingual higher-education institution in Yaoundé, academically affiliated with the University of Bamenda (UBa). Organised into specialised schools, it delivers professional HND, Bachelor (BTech) and Master (MTech / MBA) programmes — with day and evening sessions — across business, management, communication, tourism, computer engineering, health and home economics.",
  8: "CITEC is a private higher institute of technology in Yaoundé delivering professional HND and Bachelor programmes in software engineering, networks, security and information systems.",
  9: "Created in 1971, the National Advanced School of Engineering (Polytechnique) of the University of Yaoundé I is Cameroon's premier engineering grande école, training engineers in civil, computer, electrical and telecommunications disciplines.",
  10: "The National School of Administration and Magistracy (ENAM), established in 1959, trains Cameroon's senior civil servants, magistrates, customs and treasury officers.",
  11: "The Sub-regional Institute of Statistics and Applied Economics (ISSEA), a CEMAC institution founded in 1984 and hosted in Yaoundé, trains statisticians, statistical engineers and applied economists for Central Africa.",
};

const programs = [
  // University of Yaoundé I
  { id: 1, schoolId: 1, faculty: 'Faculty of Science', name: 'BSc Computer Science', level: 'Bachelor', durationMonths: 36, tuitionFee: 50000 },
  { id: 2, schoolId: 1, faculty: 'Faculty of Science', name: 'BSc Mathematics', level: 'Bachelor', durationMonths: 36, tuitionFee: 50000 },
  { id: 3, schoolId: 1, faculty: 'Faculty of Science', name: 'BSc Physics', level: 'Bachelor', durationMonths: 36, tuitionFee: 50000 },
  { id: 4, schoolId: 1, faculty: 'Faculty of Medicine & Biomedical Sciences', name: 'Doctor of Medicine', level: 'Doctorate', durationMonths: 84, tuitionFee: 50000 },
  { id: 5, schoolId: 1, faculty: 'Faculty of Laws & Political Science', name: 'LLB Law', level: 'Bachelor', durationMonths: 36, tuitionFee: 50000 },
  { id: 6, schoolId: 1, faculty: 'Faculty of Arts, Letters & Social Sciences', name: 'BA English Modern Letters', level: 'Bachelor', durationMonths: 36, tuitionFee: 50000 },
  // University of Yaoundé II
  { id: 7, schoolId: 2, faculty: 'Faculty of Laws & Political Science', name: 'LLB Law', level: 'Bachelor', durationMonths: 36, tuitionFee: 50000 },
  { id: 8, schoolId: 2, faculty: 'Faculty of Laws & Political Science', name: 'BSc Political Science', level: 'Bachelor', durationMonths: 36, tuitionFee: 50000 },
  { id: 9, schoolId: 2, faculty: 'Faculty of Economics & Management', name: 'BSc Economics', level: 'Bachelor', durationMonths: 36, tuitionFee: 50000 },
  { id: 10, schoolId: 2, faculty: 'Faculty of Economics & Management', name: 'BSc Management', level: 'Bachelor', durationMonths: 36, tuitionFee: 50000 },
  { id: 11, schoolId: 2, faculty: 'Faculty of Economics & Management', name: 'BSc Accounting & Finance', level: 'Bachelor', durationMonths: 36, tuitionFee: 50000 },
  { id: 12, schoolId: 2, faculty: 'International Relations Institute (IRIC)', name: 'BSc International Relations', level: 'Bachelor', durationMonths: 36, tuitionFee: 50000 },
  // Catholic University of Central Africa (UCAC)
  { id: 13, schoolId: 3, faculty: 'School of Social & Management Sciences', name: 'BSc Management', level: 'Bachelor', durationMonths: 36, tuitionFee: 900000 },
  { id: 14, schoolId: 3, faculty: 'School of Social & Management Sciences', name: 'BSc Accounting', level: 'Bachelor', durationMonths: 36, tuitionFee: 900000 },
  { id: 15, schoolId: 3, faculty: 'Faculty of Social Sciences & Management', name: 'LLB Law', level: 'Bachelor', durationMonths: 36, tuitionFee: 900000 },
  { id: 16, schoolId: 3, faculty: 'Faculty of Social Sciences & Management', name: 'BSc Social & Political Sciences', level: 'Bachelor', durationMonths: 36, tuitionFee: 900000 },
  { id: 17, schoolId: 3, faculty: 'School of Health Sciences', name: 'BSc Nursing', level: 'Bachelor', durationMonths: 36, tuitionFee: 950000 },
  { id: 18, schoolId: 3, faculty: 'School of Health Sciences', name: 'Doctor of Medicine', level: 'Doctorate', durationMonths: 84, tuitionFee: 1200000 },
  // Protestant University of Central Africa (UPAC)
  { id: 19, schoolId: 4, faculty: 'Faculty of Protestant Theology', name: 'BTh Theology', level: 'Bachelor', durationMonths: 36, tuitionFee: 800000 },
  { id: 20, schoolId: 4, faculty: 'Faculty of Health Sciences', name: 'BSc Health Sciences', level: 'Bachelor', durationMonths: 36, tuitionFee: 800000 },
  { id: 21, schoolId: 4, faculty: 'Faculty of Health Sciences', name: 'BSc Nursing', level: 'Bachelor', durationMonths: 36, tuitionFee: 850000 },
  { id: 22, schoolId: 4, faculty: 'Faculty of Management Sciences', name: 'BSc Management', level: 'Bachelor', durationMonths: 36, tuitionFee: 800000 },
  { id: 23, schoolId: 4, faculty: 'Faculty of Science & Technology', name: 'BSc Computer Science', level: 'Bachelor', durationMonths: 36, tuitionFee: 800000 },
  // The ICT University
  { id: 24, schoolId: 5, faculty: 'School of Computing & Engineering', name: 'BSc Software Engineering', level: 'Bachelor', durationMonths: 48, tuitionFee: 800000 },
  { id: 25, schoolId: 5, faculty: 'School of Computing & Engineering', name: 'BSc Information Technology', level: 'Bachelor', durationMonths: 36, tuitionFee: 750000 },
  { id: 26, schoolId: 5, faculty: 'School of Computing & Engineering', name: 'BSc Cybersecurity', level: 'Bachelor', durationMonths: 36, tuitionFee: 800000 },
  { id: 27, schoolId: 5, faculty: 'School of Computing & Engineering', name: 'MSc Information Systems', level: 'Master', durationMonths: 24, tuitionFee: 1000000 },
  { id: 28, schoolId: 5, faculty: 'School of Business & Management', name: 'BSc Business Administration', level: 'Bachelor', durationMonths: 36, tuitionFee: 700000 },
  { id: 29, schoolId: 5, faculty: 'School of Business & Management', name: 'MBA Business Administration', level: 'Master', durationMonths: 24, tuitionFee: 1200000 },
  // PKFokam Institute of Excellence
  { id: 30, schoolId: 6, faculty: 'School of Computer Science', name: 'BSc Computer Science', level: 'Bachelor', durationMonths: 36, tuitionFee: 700000 },
  { id: 31, schoolId: 6, faculty: 'School of Banking & Finance', name: 'BSc Banking & Finance', level: 'Bachelor', durationMonths: 36, tuitionFee: 700000 },
  { id: 32, schoolId: 6, faculty: 'School of Management & Economics', name: 'BSc Accounting', level: 'Bachelor', durationMonths: 36, tuitionFee: 700000 },
  { id: 33, schoolId: 6, faculty: 'School of Management & Economics', name: 'BBA Business Administration', level: 'Bachelor', durationMonths: 36, tuitionFee: 700000 },
  { id: 34, schoolId: 6, faculty: 'School of Management & Economics', name: 'BSc Marketing', level: 'Bachelor', durationMonths: 36, tuitionFee: 700000 },
  // Yaoundé International Business School (YIBS) — grouped by School per the brochure
  { id: 35, schoolId: 7, faculty: 'School of Business & Finance', name: 'HND Accounting', level: 'Diploma', durationMonths: 24, tuitionFee: 400000 },
  { id: 36, schoolId: 7, faculty: 'School of Business & Finance', name: 'BTech Banking & Finance', level: 'Bachelor', durationMonths: 36, tuitionFee: 550000 },
  { id: 37, schoolId: 7, faculty: 'School of Business & Finance', name: 'HND Marketing', level: 'Diploma', durationMonths: 24, tuitionFee: 400000 },
  { id: 38, schoolId: 7, faculty: 'School of Management', name: 'HND Project Management', level: 'Diploma', durationMonths: 24, tuitionFee: 450000 },
  { id: 39, schoolId: 7, faculty: 'School of Management', name: 'BTech Human Resource Management', level: 'Bachelor', durationMonths: 36, tuitionFee: 500000 },
  { id: 40, schoolId: 7, faculty: 'School of Management', name: 'HND Logistics & Transport Management', level: 'Diploma', durationMonths: 24, tuitionFee: 450000 },
  { id: 41, schoolId: 7, faculty: 'School of Management', name: 'HND Port & Shipping Management', level: 'Diploma', durationMonths: 24, tuitionFee: 450000 },
  { id: 42, schoolId: 7, faculty: 'School of Communication', name: 'HND Journalism', level: 'Diploma', durationMonths: 24, tuitionFee: 450000 },
  { id: 43, schoolId: 7, faculty: 'School of Communication', name: 'BTech Corporate Communication', level: 'Bachelor', durationMonths: 36, tuitionFee: 500000 },
  { id: 44, schoolId: 7, faculty: 'School of Communication', name: 'HND Advertising & Public Relations', level: 'Diploma', durationMonths: 24, tuitionFee: 450000 },
  { id: 45, schoolId: 7, faculty: 'School of Tourism & Hotel Management', name: 'HND Travel Agency Management', level: 'Diploma', durationMonths: 24, tuitionFee: 450000 },
  { id: 46, schoolId: 7, faculty: 'School of Tourism & Hotel Management', name: 'HND Hotel Management & Catering', level: 'Diploma', durationMonths: 24, tuitionFee: 450000 },
  { id: 47, schoolId: 7, faculty: 'School of Computer Engineering', name: 'BTech Software Engineering', level: 'Bachelor', durationMonths: 36, tuitionFee: 600000 },
  { id: 48, schoolId: 7, faculty: 'School of Computer Engineering', name: 'BTech Computer Science & Network', level: 'Bachelor', durationMonths: 36, tuitionFee: 550000 },
  { id: 49, schoolId: 7, faculty: 'School of Computer Engineering', name: 'BTech Network & Security', level: 'Bachelor', durationMonths: 36, tuitionFee: 600000 },
  { id: 50, schoolId: 7, faculty: 'School of Computer Engineering', name: 'HND Computer Graphics & Web Design', level: 'Diploma', durationMonths: 24, tuitionFee: 450000 },
  { id: 51, schoolId: 7, faculty: 'School of Computer Engineering', name: 'BTech Cloud Computing & Virtualization', level: 'Bachelor', durationMonths: 36, tuitionFee: 600000 },
  { id: 52, schoolId: 7, faculty: 'School of Computer Engineering', name: 'HND Database Management', level: 'Diploma', durationMonths: 24, tuitionFee: 450000 },
  { id: 53, schoolId: 7, faculty: 'School of Medical & Biomedical Sciences', name: 'HND Nursing', level: 'Diploma', durationMonths: 36, tuitionFee: 600000 },
  { id: 54, schoolId: 7, faculty: 'School of Medical & Biomedical Sciences', name: 'HND Midwifery', level: 'Diploma', durationMonths: 36, tuitionFee: 600000 },
  { id: 55, schoolId: 7, faculty: 'School of Medical & Biomedical Sciences', name: 'HND Medical Laboratory Sciences', level: 'Diploma', durationMonths: 36, tuitionFee: 550000 },
  { id: 56, schoolId: 7, faculty: 'School of Medical & Biomedical Sciences', name: 'HND Pharmacy Technology', level: 'Diploma', durationMonths: 24, tuitionFee: 500000 },
  { id: 57, schoolId: 7, faculty: 'School of Medical & Biomedical Sciences', name: 'HND Physiotherapy', level: 'Diploma', durationMonths: 36, tuitionFee: 550000 },
  { id: 58, schoolId: 7, faculty: 'School of Medical & Biomedical Sciences', name: 'HND Health Care Management', level: 'Diploma', durationMonths: 24, tuitionFee: 500000 },
  { id: 59, schoolId: 7, faculty: 'School of Home Economics & Social Works', name: 'HND Bakery & Food Processing', level: 'Diploma', durationMonths: 24, tuitionFee: 350000 },
  { id: 60, schoolId: 7, faculty: 'School of Home Economics & Social Works', name: 'HND Beauty Care & Cosmetics', level: 'Diploma', durationMonths: 24, tuitionFee: 300000 },
  { id: 61, schoolId: 7, faculty: 'School of Home Economics & Social Works', name: 'HND Esthetics', level: 'Diploma', durationMonths: 24, tuitionFee: 300000 },
  { id: 62, schoolId: 7, faculty: 'School of Home Economics & Social Works', name: 'HND Hairdressing', level: 'Diploma', durationMonths: 24, tuitionFee: 300000 },
  { id: 63, schoolId: 7, faculty: 'School of Home Economics & Social Works', name: 'HND Fashion Design & Clothing', level: 'Diploma', durationMonths: 24, tuitionFee: 350000 },
  { id: 64, schoolId: 7, faculty: "Master's Programs", name: 'MBA Business Administration', level: 'Master', durationMonths: 24, tuitionFee: 850000 },
  { id: 65, schoolId: 7, faculty: "Master's Programs", name: 'MTech Software Engineering', level: 'Master', durationMonths: 24, tuitionFee: 900000 },
  { id: 66, schoolId: 7, faculty: "Master's Programs", name: 'MTech Data Communication & Networking', level: 'Master', durationMonths: 24, tuitionFee: 900000 },
  // CITEC Higher Institute of Technology
  { id: 67, schoolId: 8, faculty: 'School of Computer Engineering', name: 'HND Software Engineering', level: 'Diploma', durationMonths: 24, tuitionFee: 600000 },
  { id: 68, schoolId: 8, faculty: 'School of Computer Engineering', name: 'BSc Network & Security', level: 'Bachelor', durationMonths: 36, tuitionFee: 650000 },
  { id: 69, schoolId: 8, faculty: 'School of Computer Engineering', name: 'HND Computer Graphics & Web Design', level: 'Diploma', durationMonths: 24, tuitionFee: 550000 },
  { id: 70, schoolId: 8, faculty: 'School of Computer Engineering', name: 'BSc Information Systems', level: 'Bachelor', durationMonths: 36, tuitionFee: 650000 },
  { id: 71, schoolId: 8, faculty: 'School of Computer Engineering', name: 'HND Database Management', level: 'Diploma', durationMonths: 24, tuitionFee: 550000 },
  { id: 72, schoolId: 8, faculty: 'School of Business & Technology', name: 'BSc E-commerce & Digital Marketing', level: 'Bachelor', durationMonths: 36, tuitionFee: 600000 },
  // National Advanced School of Engineering (ENSPY)
  { id: 73, schoolId: 9, faculty: 'Department of Civil Engineering', name: 'BEng Civil Engineering', level: 'Bachelor', durationMonths: 60, tuitionFee: 50000 },
  { id: 74, schoolId: 9, faculty: 'Department of Computer Engineering', name: 'BEng Computer Engineering', level: 'Bachelor', durationMonths: 60, tuitionFee: 50000 },
  { id: 75, schoolId: 9, faculty: 'Department of Electrical Engineering', name: 'BEng Electrical Engineering', level: 'Bachelor', durationMonths: 60, tuitionFee: 50000 },
  { id: 76, schoolId: 9, faculty: 'Department of Telecommunications', name: 'BEng Telecommunications Engineering', level: 'Bachelor', durationMonths: 60, tuitionFee: 50000 },
  // National School of Administration and Magistracy (ENAM)
  { id: 77, schoolId: 10, faculty: 'Division of Administration', name: 'Public Administration', level: 'Master', durationMonths: 24, tuitionFee: 50000 },
  { id: 78, schoolId: 10, faculty: 'Division of Magistracy', name: 'Magistracy', level: 'Master', durationMonths: 24, tuitionFee: 50000 },
  { id: 79, schoolId: 10, faculty: 'Division of Customs', name: 'Customs Administration', level: 'Diploma', durationMonths: 24, tuitionFee: 50000 },
  { id: 80, schoolId: 10, faculty: 'Division of Treasury', name: 'Treasury & Finance', level: 'Diploma', durationMonths: 24, tuitionFee: 50000 },
  // Sub-regional Institute of Statistics & Applied Economics (ISSEA)
  { id: 81, schoolId: 11, faculty: 'Department of Statistical Engineering', name: 'Statistical Engineering', level: 'Master', durationMonths: 60, tuitionFee: 50000 },
  { id: 82, schoolId: 11, faculty: 'Department of Applied Statistics', name: 'Applied Statistics', level: 'Bachelor', durationMonths: 36, tuitionFee: 50000 },
  { id: 83, schoolId: 11, faculty: 'Department of Demography', name: 'Demography', level: 'Master', durationMonths: 24, tuitionFee: 50000 },
  { id: 84, schoolId: 11, faculty: 'Department of Applied Economics', name: 'Economic Analysis', level: 'Bachelor', durationMonths: 36, tuitionFee: 50000 },
];

const users = [
  { id: 1, firebaseUid: 'admin-dev', email: 'admin@unimatch.cm', displayName: 'Platform Admin', role: 'ADMIN' },
  { id: 2, firebaseUid: 'student-1', email: 'ada@example.cm', displayName: 'Ada N.', role: 'STUDENT' },
  { id: 3, firebaseUid: 'student-2', email: 'bih@example.cm', displayName: 'Bih T.', role: 'STUDENT' },
  { id: 4, firebaseUid: 'student-3', email: 'che@example.cm', displayName: 'Che M.', role: 'STUDENT' },
];

let reviews = [
  { id: 1, schoolId: 1, userId: 2, rating: 5, comment: 'Great campus and supportive lecturers.', status: 'APPROVED', createdAt: NOW },
  { id: 2, schoolId: 1, userId: 3, rating: 4, comment: 'Strong programmes but large class sizes.', status: 'APPROVED', createdAt: NOW },
  { id: 3, schoolId: 5, userId: 2, rating: 4, comment: 'Good for tech, modern facilities.', status: 'APPROVED', createdAt: NOW },
  { id: 4, schoolId: 3, userId: 4, rating: 5, comment: 'Excellent reputation and strong discipline.', status: 'APPROVED', createdAt: NOW },
];

let favorites = []; // { userId, schoolId }
let seq = { user: 100, review: 100, favorite: 1 };

// ----------------------------- Helpers -----------------------------
const round1 = (n) => Math.round(n * 10) / 10;

function ratingOf(schoolId) {
  const rs = reviews.filter((r) => r.schoolId === schoolId && r.status === 'APPROVED');
  if (rs.length === 0) return { averageRating: 0, ratingCount: 0 };
  return { averageRating: round1(rs.reduce((a, r) => a + r.rating, 0) / rs.length), ratingCount: rs.length };
}

function summary(s) {
  const r = ratingOf(s.id);
  return { id: s.id, name: s.name, category: s.category, city: s.city, region: s.region, tuitionFee: s.tuitionFee, currency: s.currency, coverImageUrl: s.coverImageUrl, latitude: s.latitude, longitude: s.longitude, averageRating: r.averageRating, ratingCount: r.ratingCount };
}

function detail(s, userId) {
  const r = ratingOf(s.id);
  return {
    id: s.id, name: s.name, category: s.category, description: s.description, history: histories[s.id] || null,
    city: s.city, region: s.region,
    address: s.address, latitude: s.latitude, longitude: s.longitude, tuitionFee: s.tuitionFee, currency: s.currency,
    website: s.website, phone: s.phone, email: s.email, coverImageUrl: s.coverImageUrl,
    averageRating: r.averageRating, ratingCount: r.ratingCount,
    favorite: userId != null && favorites.some((f) => f.userId === userId && f.schoolId === s.id),
    programs: programs.filter((p) => p.schoolId === s.id).map((p) => ({ id: p.id, name: p.name, faculty: p.faculty, level: p.level, durationMonths: p.durationMonths, tuitionFee: p.tuitionFee })),
    images: [],
  };
}

function reviewDto(r) {
  const s = schools.find((x) => x.id === r.schoolId);
  const u = users.find((x) => x.id === r.userId);
  return { id: r.id, schoolId: r.schoolId, schoolName: s ? s.name : null, userDisplayName: u ? (u.displayName || u.email) : null, rating: r.rating, comment: r.comment, status: r.status, createdAt: r.createdAt };
}

// Resolve (and lazily create) the caller from the X-Debug-Uid header. Returns null for guests.
function currentUser(req) {
  const uid = req.headers['x-debug-uid'];
  if (!uid) return null;
  let u = users.find((x) => x.firebaseUid === uid);
  if (!u) {
    u = { id: ++seq.user, firebaseUid: uid, email: req.headers['x-debug-email'] || null, displayName: req.headers['x-debug-name'] || uid, role: (req.headers['x-debug-role'] || 'STUDENT').toUpperCase() };
    users.push(u);
  }
  return u;
}

function page(content, pageNum, size) {
  const from = pageNum * size;
  const slice = content.slice(from, from + size);
  return { content: slice, page: pageNum, size, totalElements: content.length, totalPages: Math.max(1, Math.ceil(content.length / size)) };
}

function send(res, status, body) {
  const json = body === undefined ? '' : JSON.stringify(body);
  res.writeHead(status, {
    'Content-Type': 'application/json',
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Headers': '*',
    'Access-Control-Allow-Methods': 'GET,POST,PUT,DELETE,OPTIONS',
  });
  res.end(json);
}

function readBody(req) {
  return new Promise((resolve) => {
    let data = '';
    req.on('data', (c) => (data += c));
    req.on('end', () => {
      try { resolve(data ? JSON.parse(data) : {}); } catch { resolve({}); }
    });
  });
}

// ----------------------------- Router -----------------------------
const server = http.createServer(async (req, res) => {
  const u = new URL(req.url, `http://localhost:${PORT}`);
  const path = u.pathname;
  const q = u.searchParams;
  const method = req.method;

  if (method === 'OPTIONS') return send(res, 204);

  // Health
  if (path === '/actuator/health') return send(res, 200, { status: 'UP' });

  // Meta
  if (path === '/api/v1/meta' && method === 'GET') {
    return send(res, 200, { appName: 'UniMatch Cameroon', tagline: 'Find Your Future University', country: 'Cameroon', currency: 'XAF', mapCenterLat: 3.8480, mapCenterLng: 11.5021, categories: ['UNIVERSITY'], firebaseEnabled: false });
  }

  // Compare (must precede /schools/:id)
  if (path === '/api/v1/schools/compare' && method === 'GET') {
    const ids = (q.get('ids') || '').split(',').map((x) => parseInt(x, 10)).filter(Boolean);
    if (ids.length < 2 || ids.length > 4) return send(res, 400, { message: 'Compare between 2 and 4 schools' });
    const found = ids.map((id) => schools.find((s) => s.id === id));
    if (found.some((s) => !s)) return send(res, 404, { message: 'One or more schools not found' });
    const me = currentUser(req);
    const details = found.map((s) => detail(s, me ? me.id : null));
    const withFee = details.filter((d) => d.tuitionFee != null);
    const cheapest = withFee.length ? withFee.reduce((a, b) => (b.tuitionFee < a.tuitionFee ? b : a)).id : null;
    const rated = details.filter((d) => d.ratingCount > 0);
    const highest = rated.length ? rated.reduce((a, b) => (b.averageRating > a.averageRating ? b : a)).id : null;
    return send(res, 200, { schools: details, cheapestSchoolId: cheapest, highestRatedSchoolId: highest });
  }

  // Search
  if (path === '/api/v1/schools' && method === 'GET') {
    let list = schools.slice();
    const term = (q.get('q') || '').trim().toLowerCase();
    if (term) list = list.filter((s) => s.name.toLowerCase().includes(term) || (s.description || '').toLowerCase().includes(term));
    const cat = q.get('category');
    if (cat) list = list.filter((s) => s.category === cat);
    const city = q.get('city');
    if (city) list = list.filter((s) => (s.city || '').toLowerCase() === city.trim().toLowerCase());
    const maxTuition = q.get('maxTuition');
    if (maxTuition) list = list.filter((s) => s.tuitionFee != null && s.tuitionFee <= parseFloat(maxTuition));
    const sort = q.get('sort') || 'name';
    if (sort === 'tuition') list.sort((a, b) => (a.tuitionFee || 0) - (b.tuitionFee || 0));
    else if (sort === 'newest') list.sort((a, b) => b.id - a.id);
    else list.sort((a, b) => a.name.localeCompare(b.name));
    let summaries = list.map(summary);
    const minRating = q.get('minRating');
    if (minRating) summaries = summaries.filter((s) => s.averageRating >= parseFloat(minRating));
    const pageNum = parseInt(q.get('page') || '0', 10);
    const size = Math.min(Math.max(parseInt(q.get('size') || '20', 10), 1), 100);
    return send(res, 200, page(summaries, pageNum, size));
  }

  // Reviews list / submit
  let m = path.match(/^\/api\/v1\/schools\/(\d+)\/reviews$/);
  if (m) {
    const sid = parseInt(m[1], 10);
    const school = schools.find((s) => s.id === sid);
    if (!school) return send(res, 404, { message: `School ${sid} not found` });
    if (method === 'GET') {
      const approved = reviews.filter((r) => r.schoolId === sid && r.status === 'APPROVED').sort((a, b) => b.id - a.id);
      const pageNum = parseInt(q.get('page') || '0', 10);
      const size = Math.min(Math.max(parseInt(q.get('size') || '20', 10), 1), 100);
      return send(res, 200, page(approved.map(reviewDto), pageNum, size));
    }
    if (method === 'POST') {
      const me = currentUser(req);
      if (!me) return send(res, 401, { message: 'Authentication required' });
      const body = await readBody(req);
      const rating = parseInt(body.rating, 10);
      if (!(rating >= 1 && rating <= 5)) return send(res, 400, { message: 'rating must be 1..5' });
      let existing = reviews.find((r) => r.schoolId === sid && r.userId === me.id);
      if (existing) { existing.rating = rating; existing.comment = body.comment || null; existing.status = 'APPROVED'; }
      else { existing = { id: ++seq.review, schoolId: sid, userId: me.id, rating, comment: body.comment || null, status: 'APPROVED', createdAt: new Date().toISOString() }; reviews.push(existing); }
      return send(res, 200, reviewDto(existing));
    }
  }

  // School detail
  m = path.match(/^\/api\/v1\/schools\/(\d+)$/);
  if (m && method === 'GET') {
    const s = schools.find((x) => x.id === parseInt(m[1], 10));
    if (!s) return send(res, 404, { message: `School ${m[1]} not found` });
    const me = currentUser(req);
    return send(res, 200, detail(s, me ? me.id : null));
  }

  // Favorites
  if (path === '/api/v1/favorites') {
    const me = currentUser(req);
    if (!me) return send(res, 401, { message: 'Authentication required' });
    if (method === 'GET') {
      const favSchools = favorites.filter((f) => f.userId === me.id).map((f) => schools.find((s) => s.id === f.schoolId)).filter(Boolean);
      return send(res, 200, favSchools.map(summary));
    }
  }
  m = path.match(/^\/api\/v1\/favorites\/(\d+)$/);
  if (m) {
    const me = currentUser(req);
    if (!me) return send(res, 401, { message: 'Authentication required' });
    const sid = parseInt(m[1], 10);
    if (method === 'POST') {
      if (!schools.find((s) => s.id === sid)) return send(res, 404, { message: `School ${sid} not found` });
      if (!favorites.some((f) => f.userId === me.id && f.schoolId === sid)) favorites.push({ userId: me.id, schoolId: sid });
      return send(res, 201);
    }
    if (method === 'DELETE') {
      favorites = favorites.filter((f) => !(f.userId === me.id && f.schoolId === sid));
      return send(res, 204);
    }
  }

  // Me
  if (path === '/api/v1/me' && method === 'GET') {
    const me = currentUser(req);
    if (!me) return send(res, 401, { message: 'Authentication required' });
    return send(res, 200, { id: me.id, firebaseUid: me.firebaseUid, email: me.email, displayName: me.displayName, role: me.role });
  }
  if (path === '/api/v1/me/reviews' && method === 'GET') {
    const me = currentUser(req);
    if (!me) return send(res, 401, { message: 'Authentication required' });
    const mine = reviews.filter((r) => r.userId === me.id).sort((a, b) => b.id - a.id);
    return send(res, 200, mine.map(reviewDto));
  }

  return send(res, 404, { message: `No route for ${method} ${path}` });
});

server.listen(PORT, () => {
  console.log(`School Finder dev server (Node stand-in) listening on http://localhost:${PORT}`);
  console.log('Try:  curl http://localhost:' + PORT + '/api/v1/schools');
});
