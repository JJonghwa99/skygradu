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
        // 로그인한 사용자의 학번 획득
        CustomUser user = (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentStudentId = user.studentID;

        // 회원 정보 조회
        Member member = memberRepository.findByStudentID(currentStudentId)
                .orElseThrow(() -> new RuntimeException("Member not found for studentId: " + currentStudentId));
        String department      = member.getMajor();
        int    enrollmentYear  = Integer.parseInt(member.getEnrollYear());
        String electiveType    = member.getElective();      // "심화", "부전", "복전"
        String minorDepartment = member.getMinor();         // 부전/복전 대상 학과
        String minorYear       = member.getMinorYear();     // 부전/복전 시작 연도

        // 졸업요건 조회(공통)
        GraduationRequirements requirements = graduationRequirementsRepository
                .findByDepartmentAndEnrollmentYear(department, enrollmentYear < 2020 ? 2020 : enrollmentYear)
                .orElseThrow(() -> new RuntimeException(
                        "Requirements not found for " + department + " year " + (enrollmentYear < 2020 ? 2020 : enrollmentYear)));

        // electiveType에 따라 electiveMajor만 수정
        if ("부전".equals(electiveType)) {
            // 부전은 minorDept+minorYear에서 electiveMajor만 덮어쓰기
            GraduationRequirements minorReq = graduationRequirementsRepository
                    .findByDepartmentAndEnrollmentYear(minorDepartment, Integer.parseInt(minorYear))
                    .orElseThrow(() -> new RuntimeException(
                            "Minor requirements not found for " + minorDepartment + " year " + minorYear));
            requirements.setElectiveMajor(minorReq.getElectiveMajor());

        } else if ("복전".equals(electiveType)) {
            // 복전은 같은 minorReq에서 majorRequired+majorElective 합으로 덮어쓰기
            GraduationRequirements minorReq = graduationRequirementsRepository
                    .findByDepartmentAndEnrollmentYear(minorDepartment, Integer.parseInt(minorYear))
                    .orElseThrow(() -> new RuntimeException(
                            "Double-major requirements not found for " + minorDepartment + " year " + minorYear));
            int combined = minorReq.getMajorCreditsRequired()
                    + minorReq.getMajorElectiveCredits();
            requirements.setElectiveMajor(combined);
        }


        // 이미 계산된 진행상황 있으면 반환
        if (!member.isUpdate()) {
            GraduationProgress progress = graduationProgressRepository.findByStudentId(currentStudentId)
                    .orElseThrow(() -> new RuntimeException(
                            "Progress not found for studentId: " + currentStudentId));
            return new GraduationCheckResult(
                    department,
                    String.valueOf(enrollmentYear),
                    electiveType,
                    minorDepartment,
                    minorYear,
                    requirements.getMajorCreditsRequired(),
                    requirements.getMajorElectiveCredits(),
                    requirements.getGeneralEducationRequired(),
                    requirements.getGeneralElectiveCredits(),
                    requirements.getElectiveMajor(),
                    requirements.getOtherCredits(),
                    requirements.getChapelRequired(),
                    requirements.getTotalCreditsRequired(),
                    progress.getMajorCreditsEarned(),
                    progress.getMajorElectiveEarned(),
                    progress.getGeneralEducationEarned(),
                    progress.getGeneralElectiveEarned(),
                    progress.getElectiveMajorEarned(),
                    progress.getOtherEarned(),
                    progress.getTotalCreditsEarned(),
                    progress.getChapelCompleted()
            );
        }

        // 이수 과목 조회 및 학점 집계
        List<CompletedCourse> courses = completedCourseRepository.findByMemberId(currentStudentId);
        int earnedMajorRequired    = 0;
        int earnedMajorElective    = 0;
        int earnedGeneralRequired  = 0;
        int earnedGeneralElective  = 0;
        int earnedElectiveMajor    = 0;
        int earnedOther            = 0;
        int totalEarned            = 0;
        int chapelCompleted        = 0;

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
                    if ("컴퓨터공학과".equals(department) && courseName != null
                            && courseName.contains("전공종합설계")) {
                        earnedMajorRequired += credits;
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

        // 전공 초과 학점 처리
        int reqMajorRequired = requirements.getMajorCreditsRequired();
        int reqMajorElective = requirements.getMajorElectiveCredits();
        int reqElectiveMajor = requirements.getElectiveMajor();

        if ("컴퓨터공학과".equals(department)) {
            if (earnedMajorRequired > reqMajorRequired) {
                earnedElectiveMajor += earnedMajorRequired - reqMajorRequired;
                earnedMajorRequired = reqMajorRequired;
            }
            if (earnedMajorElective > reqMajorElective) {
                earnedElectiveMajor += earnedMajorElective - reqMajorElective;
                earnedMajorElective = reqMajorElective;
            }
            if (earnedElectiveMajor > reqElectiveMajor) {
                earnedOther += earnedElectiveMajor - reqElectiveMajor;
                earnedElectiveMajor = reqElectiveMajor;
            }
        } else {
            if (earnedMajorRequired > reqMajorRequired) {
                earnedOther += earnedMajorRequired - reqMajorRequired;
                earnedMajorRequired = reqMajorRequired;
            }
            if (earnedMajorElective > reqMajorElective) {
                earnedOther += earnedMajorElective - reqMajorElective;
                earnedMajorElective = reqMajorElective;
            }
        }

        // 진행상황 저장 및 플래그 업데이트
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

        member.setUpdate(false);
        memberRepository.save(member);

        // 결과 DTO 생성 및 반환
        return new GraduationCheckResult(
                department,
                String.valueOf(enrollmentYear),
                electiveType,
                minorDepartment,
                minorYear,
                requirements.getMajorCreditsRequired(),
                requirements.getMajorElectiveCredits(),
                requirements.getGeneralEducationRequired(),
                requirements.getGeneralElectiveCredits(),
                requirements.getElectiveMajor(),
                requirements.getOtherCredits(),
                requirements.getChapelRequired(),
                requirements.getTotalCreditsRequired(),
                earnedMajorRequired,
                earnedMajorElective,
                earnedGeneralRequired,
                earnedGeneralElective,
                earnedElectiveMajor,
                earnedOther,
                totalEarned,
                chapelCompleted
        );
    }
}
