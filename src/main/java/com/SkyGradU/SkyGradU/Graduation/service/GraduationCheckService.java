package com.SkyGradU.SkyGradU.Graduation.service;

import com.SkyGradU.SkyGradU.Graduation.dto.GraduationCheckResult;
import com.SkyGradU.SkyGradU.Graduation.entity.GraduationProgress;
import com.SkyGradU.SkyGradU.Graduation.entity.GraduationRequirements;
import com.SkyGradU.SkyGradU.Graduation.repository.GraduationProgressRepository;
import com.SkyGradU.SkyGradU.Graduation.repository.GraduationRequirementsRepository;
import com.SkyGradU.SkyGradU.User.Courses.CompletedCourse;
import com.SkyGradU.SkyGradU.User.Courses.CompletedCourseRepository;
import com.SkyGradU.SkyGradU.User.member.CustomUser;
import com.SkyGradU.SkyGradU.User.member.Member;
import com.SkyGradU.SkyGradU.User.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GraduationCheckService {

    @Autowired
    private CompletedCourseRepository completedCourseRepository;

    @Autowired
    private GraduationRequirementsRepository graduationRequirementsRepository;

    @Autowired
    private GraduationProgressRepository graduationProgressRepository;

    @Autowired
    private MemberRepository memberRepository;

    public GraduationCheckResult checkGraduationRequirements(String studentId) {
        // 1. 로그인한 사용자의 학번을 가져옴
        CustomUser user = (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentStudentId = user.studentID; // 또는 파라미터 studentId 사용

        // 2. 회원 정보 조회
        Optional<Member> optionalMember = memberRepository.findByStudentID(currentStudentId);
        if (!optionalMember.isPresent()) {
            throw new RuntimeException("Member not found for studentId: " + currentStudentId);
        }
        Member member = optionalMember.get();
        String department = member.getMajor();         // 학과명
        String enrollmentYear = member.getEnrollYear();  // 입학연도

        // 3. 졸업요건 조회 (학과별)
        GraduationRequirements requirements = graduationRequirementsRepository.findByDepartment(department)
                .orElseThrow(() -> new RuntimeException("Graduation requirements not found for department: " + department));

        // 4. 해당 사용자의 수강한 강좌 정보 조회
        List<CompletedCourse> courses = completedCourseRepository.findByMemberId(currentStudentId);

        // 5. 각 영역별 취득 학점 및 채플 이수 횟수 합산
        int earnedMajorRequired = 0;
        int earnedMajorElective = 0;
        int earnedGeneralRequired = 0;
        int earnedGeneralElective = 0;
        int earnedElectiveMajor = 0; // 선택 전공 취득 학점 (추후 계산 시 사용)
        int earnedOther = 0;
        int totalEarned = 0;
        int chapelCompleted = 0;

        for (CompletedCourse course : courses) {
            int credits = course.getCredits();
            totalEarned += credits;
            String courseName = course.getCourseName();
            if (courseName != null && courseName.contains("채플")) {
                chapelCompleted++;
            }
            String courseType = course.getCourseType();
            if (courseType != null) {
                if (courseType.contains("전필")) {
                    // 컴퓨터공학과인 경우: 강의명이 "전공종합설계"를 포함하면 전필, 그렇지 않으면 전선으로 계산
                    if ("컴퓨터공학과".equals(department)) {
                        if (courseName != null && courseName.contains("전공종합설계")) {
                            earnedMajorRequired += credits;
                        } else {
                            earnedMajorElective += credits;
                        }
                    } else {
                        earnedMajorRequired += credits;
                    }
                } else if (courseType.contains("전선")) {
                    earnedMajorElective += credits;
                } else if (courseType.contains("교필")) {
                    earnedGeneralRequired += credits;
                } else if (courseType.contains("교선")) {
                    earnedGeneralElective += credits;
                } else {
                    earnedOther += credits;
                }
            }
        }


        // 6. 전공 영역 초과 학점 처리
        if ("컴퓨터공학과".equals(department)) {
            int reqMajorRequired = requirements.getMajorCreditsRequired();
            int reqMajorElective = requirements.getMajorElectiveCredits();
            int reqElectiveMajor = requirements.getElectiveMajor();  // 선택 전공 필요 학점

            // 전필(전공필수) 초과분을 선택 전공에 추가
            if (earnedMajorRequired > reqMajorRequired) {
                int excess = earnedMajorRequired - reqMajorRequired;
                earnedMajorRequired = reqMajorRequired;
                earnedElectiveMajor += excess;
            }
            // 전선(전공선택) 초과분도 선택 전공에 추가
            if (earnedMajorElective > reqMajorElective) {
                int excess = earnedMajorElective - reqMajorElective;
                earnedMajorElective = reqMajorElective;
                earnedElectiveMajor += excess;
            }
            // 선택 전공 영역이 기준을 초과하면 남은 초과분은 기타에 반영
            if (earnedElectiveMajor > reqElectiveMajor) {
                int excess = earnedElectiveMajor - reqElectiveMajor;
                earnedElectiveMajor = reqElectiveMajor;
                earnedOther += excess;
            }
        } else {
            // 다른 학과의 경우, 초과분은 기타로 반영
            int reqMajorRequired = requirements.getMajorCreditsRequired();
            if (earnedMajorRequired > reqMajorRequired) {
                int excess = earnedMajorRequired - reqMajorRequired;
                earnedMajorRequired = reqMajorRequired;
                earnedOther += excess;
            }
            int reqMajorElective = requirements.getMajorElectiveCredits();
            if (earnedMajorElective > reqMajorElective) {
                int excess = earnedMajorElective - reqMajorElective;
                earnedMajorElective = reqMajorElective;
                earnedOther += excess;
            }
        }

        // 7. GraduationProgress 테이블에 저장 (업데이트 또는 새로 생성)
        GraduationProgress progress = graduationProgressRepository.findByStudentId(currentStudentId)
                .orElse(new GraduationProgress());
        progress.setStudentId(currentStudentId);
        progress.setMajorCreditsEarned(earnedMajorRequired);
        progress.setMajorElectiveEarned(earnedMajorElective);
        progress.setGeneralEducationEarned(earnedGeneralRequired);
        progress.setGeneralElectiveEarned(earnedGeneralElective);
        progress.setElectiveMajorEarned(earnedElectiveMajor);
        progress.setOtherEarned(earnedOther);
        progress.setTotalCreditsEarned(totalEarned);
        progress.setChapelCompleted(chapelCompleted);
        graduationProgressRepository.save(progress);

        // 8. 회원 update 플래그 변경 후 저장
        member.setUpdate(true);
        memberRepository.save(member);

        // 9. GraduationCheckResult 생성 (생성자 인자 순서 및 타입에 맞게)
        GraduationCheckResult result = new GraduationCheckResult(
                department,
                enrollmentYear,
                requirements.getMajorCreditsRequired(),      // 전공필수 기준 학점
                requirements.getMajorElectiveCredits(),        // 전공선택 기준 학점
                requirements.getGeneralEducationRequired(),    // 교양필수 기준 학점
                requirements.getGeneralElectiveCredits(),      // 교양선택 기준 학점
                requirements.getElectiveMajor(),               // 선택 전공 기준 학점
                requirements.getOtherCredits(),               // 기타 필요 학점
                requirements.getChapelRequired(),              // 채플 필수 이수 횟수
                requirements.getTotalCreditsRequired(),        // 총 필요 학점 (추가된 값)
                earnedMajorRequired,
                earnedMajorElective,
                earnedGeneralRequired,
                earnedGeneralElective,
                earnedElectiveMajor,
                earnedOther,
                totalEarned,
                chapelCompleted
        );

        return result;
    }
}
