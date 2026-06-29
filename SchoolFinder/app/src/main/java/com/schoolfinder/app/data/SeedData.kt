package com.schoolfinder.app.data

/** Sample data used to populate the database on first launch. */
object SeedData {

    val schools: List<SchoolEntity> = listOf(
        SchoolEntity(
            name = "University of Yaoundé I",
            category = "University",
            location = "Yaoundé",
            address = "Ngoa-Ekelle, Yaoundé, Centre Region",
            latitude = 3.8649,
            longitude = 11.5021,
            tuition = 110,
            programs = listOf("Computer Science", "Medicine", "Law", "Mathematics", "Biology"),
            facilities = listOf("Library", "Research Labs", "Cafeteria", "Sports Complex", "Wi-Fi"),
            description = "One of the oldest and largest public universities in Cameroon, offering a wide range of science, arts and professional programs.",
            baseRating = 4.3,
            website = "https://www.uy1.uninet.cm",
            phone = "+237 222 234 496"
        ),
        SchoolEntity(
            name = "University of Buea",
            category = "University",
            location = "Buea",
            address = "Molyko, Buea, South-West Region",
            latitude = 4.1551,
            longitude = 9.2920,
            tuition = 95,
            programs = listOf("Software Engineering", "Nursing", "Education", "Economics", "Journalism"),
            facilities = listOf("Library", "ICT Center", "Hostels", "Health Center", "Auditorium"),
            description = "The reference Anglo-Saxon university in Cameroon, known for strong programs in technology, health sciences and the humanities.",
            baseRating = 4.5,
            website = "https://www.ubuea.cm",
            phone = "+237 233 322 134"
        ),
        SchoolEntity(
            name = "University of Douala",
            category = "University",
            location = "Douala",
            address = "Bassa, Douala, Littoral Region",
            latitude = 4.0470,
            longitude = 9.7679,
            tuition = 100,
            programs = listOf("Business Management", "Engineering", "Accounting", "Logistics", "Marketing"),
            facilities = listOf("Library", "Business Incubator", "Labs", "Cafeteria", "Parking"),
            description = "A leading public university in the economic capital, with strong management, commerce and engineering schools.",
            baseRating = 4.0,
            website = "https://www.univ-douala.cm",
            phone = "+237 233 401 754"
        ),
        SchoolEntity(
            name = "National Advanced School of Engineering (Polytechnique)",
            category = "Engineering School",
            location = "Yaoundé",
            address = "Melen, Yaoundé, Centre Region",
            latitude = 3.8612,
            longitude = 11.4894,
            tuition = 130,
            programs = listOf("Civil Engineering", "Electrical Engineering", "Computer Engineering", "Telecommunications"),
            facilities = listOf("Engineering Labs", "Workshops", "Library", "Wi-Fi", "Cafeteria"),
            description = "The premier engineering school of the University of Yaoundé I, training top-tier engineers through competitive entrance examinations.",
            baseRating = 4.7,
            website = "https://www.polytechnique.cm",
            phone = "+237 222 220 234"
        ),
        SchoolEntity(
            name = "Catholic University of Central Africa (UCAC)",
            category = "University",
            location = "Yaoundé",
            address = "Nkolbisson, Yaoundé, Centre Region",
            latitude = 3.8717,
            longitude = 11.4341,
            tuition = 1200,
            programs = listOf("Health Sciences", "Social Sciences", "Law", "Philosophy", "Management"),
            facilities = listOf("Modern Library", "Chapel", "Health Sciences Labs", "Hostels", "Wi-Fi"),
            description = "A respected private Catholic university serving the Central African region with a focus on ethics, health and the social sciences.",
            baseRating = 4.4,
            website = "https://www.ucac-icy.net",
            phone = "+237 222 311 211"
        ),
        SchoolEntity(
            name = "University of Dschang",
            category = "University",
            location = "Dschang",
            address = "Foto, Dschang, West Region",
            latitude = 5.4452,
            longitude = 10.0535,
            tuition = 90,
            programs = listOf("Agriculture", "Veterinary Medicine", "Sciences", "Economics", "Letters"),
            facilities = listOf("Botanical Garden", "Farms", "Library", "Hostels", "Sports Fields"),
            description = "Renowned for agronomy and environmental sciences, set in the scenic highlands of the West Region.",
            baseRating = 4.1,
            website = "https://www.univ-dschang.org",
            phone = "+237 233 451 381"
        ),
        SchoolEntity(
            name = "ICT University",
            category = "University",
            location = "Yaoundé",
            address = "Messassi, Yaoundé, Centre Region",
            latitude = 3.9156,
            longitude = 11.5419,
            tuition = 1500,
            programs = listOf("Information Technology", "Cybersecurity", "Data Science", "Software Engineering", "MBA"),
            facilities = listOf("Computer Labs", "High-speed Internet", "Library", "Cafeteria", "Online Platform"),
            description = "A private, technology-focused university offering American-style ICT degrees and flexible learning options.",
            baseRating = 4.2,
            website = "https://ictuniversity.edu.cm",
            phone = "+237 242 016 999"
        ),
        SchoolEntity(
            name = "University of Bamenda",
            category = "University",
            location = "Bamenda",
            address = "Bambili, Bamenda, North-West Region",
            latitude = 6.0190,
            longitude = 10.2490,
            tuition = 95,
            programs = listOf("Education", "Sciences", "Health Technology", "Arts", "Engineering"),
            facilities = listOf("Library", "Labs", "Hostels", "Health Center", "Sports Complex"),
            description = "A growing public university serving the North-West, with teacher-training, science and technology faculties.",
            baseRating = 3.9,
            website = "https://www.uniba.cm",
            phone = "+237 233 366 224"
        ),
        SchoolEntity(
            name = "Saint Monica University",
            category = "University",
            location = "Buea",
            address = "Bonduma, Buea, South-West Region",
            latitude = 4.1620,
            longitude = 9.2710,
            tuition = 1800,
            programs = listOf("Business Administration", "Nursing", "Computer Science", "Public Health", "Accounting"),
            facilities = listOf("Library", "Nursing Labs", "Computer Lab", "Hostels", "Wi-Fi"),
            description = "A private institution offering internationally oriented programs with small class sizes and professional focus.",
            baseRating = 4.0,
            website = "https://www.stmonicainstitution.com",
            phone = "+237 670 000 000"
        ),
        SchoolEntity(
            name = "Government Bilingual High School Yaoundé",
            category = "Secondary School",
            location = "Yaoundé",
            address = "Centre Administratif, Yaoundé, Centre Region",
            latitude = 3.8689,
            longitude = 11.5214,
            tuition = 60,
            programs = listOf("Sciences", "Arts", "Commercial", "Bilingual Studies"),
            facilities = listOf("Library", "Computer Room", "Science Labs", "Playground", "Cafeteria"),
            description = "A well-established public bilingual secondary school preparing students for the GCE and Baccalauréat examinations.",
            baseRating = 3.8,
            website = "https://www.minesec.gov.cm",
            phone = "+237 222 230 000"
        )
    )

    val sampleReviews: List<ReviewEntity> = listOf(
        ReviewEntity(schoolId = 1, userName = "Brenda M.", rating = 5, comment = "Great lecturers and a very active campus life."),
        ReviewEntity(schoolId = 1, userName = "Eric T.", rating = 4, comment = "Big university, sometimes crowded but lots of opportunities."),
        ReviewEntity(schoolId = 2, userName = "Achu P.", rating = 5, comment = "Excellent tech programs and a beautiful campus in Molyko."),
        ReviewEntity(schoolId = 4, userName = "Diane K.", rating = 5, comment = "Tough entrance exam but worth it. Top engineering training."),
        ReviewEntity(schoolId = 7, userName = "Samuel N.", rating = 4, comment = "Modern IT facilities and flexible schedules.")
    )

    val defaultAdmin = UserEntity(
        name = "System Administrator",
        email = "admin@schoolfinder.com",
        password = "admin123",
        role = "ADMIN"
    )
}
