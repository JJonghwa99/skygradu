// src/main/java/com/SkyGradU/SkyGradU/graduation/repository/GraduationUpdateRepository.java
package com.SkyGradU.SkyGradU.Graduation.repository;

import com.SkyGradU.SkyGradU.Graduation.entity.GraduationUpdate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GraduationUpdateRepository extends JpaRepository<GraduationUpdate, Long> {
    Optional<GraduationUpdate> findByStudentId(String studentId);
}
