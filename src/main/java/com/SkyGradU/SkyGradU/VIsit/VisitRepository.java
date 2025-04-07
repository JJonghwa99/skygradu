package com.SkyGradU.SkyGradU.VIsit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    long countByVisitDate(LocalDate visitDate); // 오늘 방문자 수
    long count(); // 누적 방문자 수
}
