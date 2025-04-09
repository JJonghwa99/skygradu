package com.SkyGradU.SkyGradU.Graduation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "graduation_progress")
public class GraduationProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 기존 studentId 컬럼 (Member 엔티티의 primary key와 연결됨)
    @Column(name = "student_id")
    private String studentId;

    // Member 엔티티와 연관 관계 설정 (읽기 전용으로 지정)
    // Member 엔티티의 'studentID' 필드와 매핑됩니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", referencedColumnName = "studentID", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private com.SkyGradU.SkyGradU.User.member.Member member;

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
