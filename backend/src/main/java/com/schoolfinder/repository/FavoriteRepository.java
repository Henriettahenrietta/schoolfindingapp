package com.schoolfinder.repository;

import com.schoolfinder.domain.Favorite;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserIdOrderByCreatedAtDesc(Long userId);

    Favorite findByUserIdAndSchoolId(Long userId, Long schoolId);

    void deleteByUserIdAndSchoolId(Long userId, Long schoolId);

    boolean existsByUserIdAndSchoolId(Long userId, Long schoolId);
}
