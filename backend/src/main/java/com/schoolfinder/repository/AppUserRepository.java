package com.schoolfinder.repository;

import com.schoolfinder.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByFirebaseUid(String firebaseUid);
}
