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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "school_image")
@Getter
@Setter
@NoArgsConstructor
public class SchoolImage {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @Column(nullable = false)
    private String url;

    @Column
    private String caption;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public SchoolImage(School school, String url, String caption) {
        this.school = school;
        this.url = url;
        this.caption = caption;
    }
}
