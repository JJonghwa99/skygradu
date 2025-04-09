package com.SkyGradU.SkyGradU.User.Courses;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompletedCourseRepository extends JpaRepository<CompletedCourse, Long> {
    List<CompletedCourse> findByMemberId(String memberId);

    Optional<CompletedCourse> findFirstByMemberIdAndCourseNameAndYear(String memberId, String courseName, String year);
}


