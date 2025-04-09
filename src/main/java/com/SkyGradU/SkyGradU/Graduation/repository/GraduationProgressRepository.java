// src/main/java/com/SkyGradU/SkyGradU/Graduation/repository/GraduationProgressRepository.java
package com.SkyGradU.SkyGradU.Graduation.repository;

import com.SkyGradU.SkyGradU.Graduation.entity.GraduationProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GraduationProgressRepository extends JpaRepository<GraduationProgress, Long> {
    Optional<GraduationProgress> findByStudentId(String studentId);

    void deleteByStudentId(String studentId);
}
