package com.SkyGradU.SkyGradU.Lectures;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AllLectureRepository extends JpaRepository<AllLectures, Long> {

    @Query("SELECT a FROM AllLectures a " +
            "WHERE a.lectureCode = :keyword " +
            "OR a.courseName LIKE %:keyword%")
    List<AllLectures> searchByCodeOrName(@Param("keyword") String keyword);

}
