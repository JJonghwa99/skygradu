package com.SkyGradU.SkyGradU.Graduation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "graduation_progress")
public class GraduationProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id")
    private String studentId;  // 사용자 학번(또는 아이디)

    @Column(name = "major_credits_earned")
    private int majorCreditsEarned; // 전공필수 취득 학점

    @Column(name = "major_elective_earned")
    private int majorElectiveEarned; // 전공선택 취득 학점

    @Column(name = "general_education_earned")
    private int generalEducationEarned; // 교양필수 취득 학점

    @Column(name = "general_elective_earned")
    private int generalElectiveEarned; // 교양선택 취득 학점

    @Column(name = "elective_major_earned")
    private int electiveMajorEarned; // 선택 전공(심화,부전,복전) 취득 학점

    @Column(name = "other_earned")
    private int otherEarned; // 기타 학점

    @Column(name = "total_credits_earned")
    private int totalCreditsEarned; // 총 학점

    @Column(name = "chapel_completed")
    private int chapelCompleted; // 채플 이수 횟수

}
