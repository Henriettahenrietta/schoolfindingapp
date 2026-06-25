package com.schoolfinder.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "program")
class Program(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    var school: School,

    @Column(nullable = false)
    var name: String,

    /** The School / faculty this programme belongs to, e.g. "School of Business & Finance". */
    @Column
    var faculty: String? = null,

    @Column
    var level: String? = null,

    @Column(name = "duration_months")
    var durationMonths: Int? = null,

    @Column(name = "tuition_fee")
    var tuitionFee: BigDecimal? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
)
