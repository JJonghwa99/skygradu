package com.SkyGradU.SkyGradU.Lectures;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AllLectureRepository extends JpaRepository<AllLectures, Long> {

    // 기존 검색 메서드
    @Query("SELECT a FROM AllLectures a " +
            "WHERE a.lectureCode = :keyword " +
            "   OR a.courseName LIKE %:keyword%")
    List<AllLectures> searchByCodeOrName(@Param("keyword") String keyword);

    // 교선(courseType='교선')을 count 내림차순, 페이징 처리
    List<AllLectures> findByCourseTypeOrderByCountDesc(String courseType, Pageable pageable);

    List<AllLectures> findByCourseNameIn(List<String> courseNames);

    // 또는 @Query로 직접
    // @Query("SELECT a FROM AllLectures a WHERE a.courseType = :type ORDER BY a.count DESC")
    // List<AllLectures> findTopByCourseType(@Param("type") String type, Pageable pageable);
    Optional<AllLectures> findByCourseNameAndCourseType(String courseName, String courseType);
}
