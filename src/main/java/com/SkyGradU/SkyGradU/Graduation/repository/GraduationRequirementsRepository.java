// src/main/java/com/SkyGradU/SkyGradU/Graduation/repository/GraduationRequirementsRepository.java
package com.SkyGradU.SkyGradU.Graduation.repository;

import com.SkyGradU.SkyGradU.Graduation.entity.GraduationRequirements;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GraduationRequirementsRepository extends JpaRepository<GraduationRequirements, Long> {
    Optional<GraduationRequirements> findByDepartmentAndEnrollmentYear(String department, int enrollmentYear);
}
