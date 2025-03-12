package com.SkyGradU.SkyGradU.User.Courses;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompletedCourseRepository extends JpaRepository<CompletedCourse, Long> {

    List<CompletedCourse> findByMemberId(String memberId);
}

