package com.schoolfinder.repository;

import com.schoolfinder.domain.SchoolImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolImageRepository extends JpaRepository<SchoolImage, Long> {
}
