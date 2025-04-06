package com.SkyGradU.SkyGradU.Graduation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GraduationCheckResult {
    private String department;               // 사용자의 학과명
    private String enrollmentYear;           // 입학연도
    private int requiredMajorRequired;       // 졸업요건: 전공필수 필요 학점
    private int requiredMajorElective;       // 졸업요건: 전공선택 필요 학점
    private int requiredGeneralRequired;     // 졸업요건: 교양필수 필요 학점
    private int requiredGeneralElective;     // 졸업요건: 교양선택 필요 학점
    private int requiredElectiveMajor;       // 졸업요건: 선택 전공 필요 학점
    private int requiredOther;               // 졸업요건: 기타 필요 학점
    private int requiredChapel;              // 졸업요건: 채플 요구 횟수
    private int requiredTotalCredits;        // 졸업요건: 총 필요 학점

    private int earnedMajorRequired;         // 취득한 전공필수 학점
    private int earnedMajorElective;         // 취득한 전공선택 학점
    private int earnedGeneralRequired;       // 취득한 교양필수 학점
    private int earnedGeneralElective;       // 취득한 교양선택 학점
    private int earnedElectiveMajor;         // 취득한 선택 전공 학점
    private int earnedOther;                 // 취득한 기타 학점 (초과분 포함)
    private int totalEarned;                 // 취득한 총 학점
    private int chapelCompleted;             // 채플 이수 횟수
}
