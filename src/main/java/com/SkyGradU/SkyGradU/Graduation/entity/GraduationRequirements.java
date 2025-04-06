package com.SkyGradU.SkyGradU.Graduation.entity;

import jakarta.persistence.*;

@Entity
public class GraduationRequirements {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 학과명
    private String department;

    // 입학연도
    @Column(name = "enrollment_year", columnDefinition = "integer USING enrollment_year::integer")
    private int enrollmentYear;

    // 총학점
    @Column(name = "total_credits_required")
    private int totalCreditsRequired;

    // 전공필수 학점
    @Column(name = "major_credits_required")
    private int majorCreditsRequired;

    // 전공선택 학점
    @Column(name = "major_elective_credits")
    private int majorElectiveCredits;

    // 교양필수 학점
    @Column(name = "general_education_required")
    private int generalEducationRequired;

    // 교양선택 학점 (이전 generalRequired -> generalElectiveCredits)
    @Column(name = "general_elective_credits")
    private int generalElectiveCredits;

    // 선택 전공(심화, 부전, 복전)
    private int electiveMajor;

    // 기타 학점
    @Column(name = "other_credits")
    private int otherCredits;

    // 채플 필수 이수 횟수
    @Column(name = "chapel_required")
    private int chapelRequired;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getEnrollmentYear() {
        return enrollmentYear;
    }

    public void setEnrollmentYear(int enrollmentYear) {
        this.enrollmentYear = enrollmentYear;
    }

    public int getTotalCreditsRequired() {
        return totalCreditsRequired;
    }

    public void setTotalCreditsRequired(int totalCreditsRequired) {
        this.totalCreditsRequired = totalCreditsRequired;
    }

    public int getMajorCreditsRequired() {
        return majorCreditsRequired;
    }

    public void setMajorCreditsRequired(int majorCreditsRequired) {
        this.majorCreditsRequired = majorCreditsRequired;
    }

    public int getMajorElectiveCredits() {
        return majorElectiveCredits;
    }

    public void setMajorElectiveCredits(int majorElectiveCredits) {
        this.majorElectiveCredits = majorElectiveCredits;
    }

    public int getGeneralEducationRequired() {
        return generalEducationRequired;
    }

    public void setGeneralEducationRequired(int generalEducationRequired) {
        this.generalEducationRequired = generalEducationRequired;
    }

    public int getGeneralElectiveCredits() {
        return generalElectiveCredits;
    }

    public void setGeneralElectiveCredits(int generalElectiveCredits) {
        this.generalElectiveCredits = generalElectiveCredits;
    }

    public int getElectiveMajor() {
        return electiveMajor;
    }

    public void setElectiveMajor(int electiveMajor) {
        this.electiveMajor = electiveMajor;
    }

    public int getOtherCredits() {
        return otherCredits;
    }

    public void setOtherCredits(int otherCredits) {
        this.otherCredits = otherCredits;
    }

    public int getChapelRequired() {
        return chapelRequired;
    }

    public void setChapelRequired(int chapelRequired) {
        this.chapelRequired = chapelRequired;
    }
}
