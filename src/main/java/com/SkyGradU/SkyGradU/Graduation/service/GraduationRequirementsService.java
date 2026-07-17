package com.SkyGradU.SkyGradU.Graduation.service;

import com.SkyGradU.SkyGradU.Graduation.entity.GraduationRequirements;
import com.SkyGradU.SkyGradU.Graduation.repository.GraduationRequirementsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GraduationRequirementsService {

    private final GraduationRequirementsRepository graduationRequirementsRepository;

    @Cacheable(value = "gradReqs", key = "#department + '_' + #enrollmentYear")
    public GraduationRequirements findByDepartmentAndEnrollmentYear(String department, int enrollmentYear) {
        // 캐시에 데이터가 있으면 즉시 반환, 없으면 DB 조회 후 캐시에 저장하고 반환
        return graduationRequirementsRepository
                .findByDepartmentAndEnrollmentYear(department, enrollmentYear)
                .orElseThrow(() -> new RuntimeException(
                        "Requirements not found for " + department + " year " + enrollmentYear));
    }
}
