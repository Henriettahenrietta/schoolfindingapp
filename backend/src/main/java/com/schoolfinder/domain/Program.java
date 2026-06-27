package com.schoolfinder.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "program")
@Getter
@Setter
@NoArgsConstructor
public class Program {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @Column(nullable = false)
    private String name;

    /** The School / faculty this programme belongs to, e.g. "School of Business & Finance". */
    @Column
    private String faculty;

    @Column
    private String level;

    @Column(name = "duration_months")
    private Integer durationMonths;

    @Column(name = "tuition_fee")
    private BigDecimal tuitionFee;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
