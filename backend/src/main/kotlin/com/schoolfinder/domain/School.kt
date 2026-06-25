package com.schoolfinder.domain

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table(name = "school")
class School(
    @Column(nullable = false)
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var category: SchoolCategory,

    @Column(columnDefinition = "text")
    var description: String? = null,

    @Column(columnDefinition = "text")
    var history: String? = null,

    @Column
    var city: String? = null,

    @Column
    var region: String? = null,

    @Column
    var address: String? = null,

    @Column
    var latitude: Double? = null,

    @Column
    var longitude: Double? = null,

    @Column(name = "tuition_fee")
    var tuitionFee: BigDecimal? = null,

    @Column(nullable = false)
    var currency: String = "XAF",

    @Column
    var website: String? = null,

    @Column
    var phone: String? = null,

    @Column
    var email: String? = null,

    @Column(name = "cover_image_url")
    var coverImageUrl: String? = null,

    @Column(nullable = false)
    var approved: Boolean = true,

    @Column(name = "created_at", nullable = false)
    var createdAt: OffsetDateTime = OffsetDateTime.now(),

    @OneToMany(mappedBy = "school", cascade = [CascadeType.ALL], orphanRemoval = true)
    var programs: MutableList<Program> = mutableListOf(),

    @OneToMany(mappedBy = "school", cascade = [CascadeType.ALL], orphanRemoval = true)
    var images: MutableList<SchoolImage> = mutableListOf(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
)
