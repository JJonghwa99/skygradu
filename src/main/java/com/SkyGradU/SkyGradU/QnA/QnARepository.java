package com.SkyGradU.SkyGradU.QnA;

import com.SkyGradU.SkyGradU.User.Courses.CompletedCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QnARepository extends JpaRepository<QnA, Long> {
}
