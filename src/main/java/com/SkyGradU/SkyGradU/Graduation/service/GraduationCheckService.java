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
        // 로그인한 사용자의 학번을 가져옴
        CustomUser user = (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentStudentId = user.studentID;

        // 회원 정보 조회
        Optional<Member> optionalMember = memberRepository.findByStudentID(currentStudentId);
        if (!optionalMember.isPresent()) {
            throw new RuntimeException("Member not found for studentId: " + currentStudentId);
        }
        Member member = optionalMember.get();
        String department = member.getMajor();
        int enrollmentYear = Integer.parseInt(member.getEnrollYear());

        // 졸업요건 조회 (입학년도까지 일치하는 데이터)
        GraduationRequirements requirements = graduationRequirementsRepository .findByDepartmentAndEnrollmentYear(department, enrollmentYear)
                .orElseThrow(() -> new RuntimeException("Graduation requirements not found for department: " + department));

        // member update 플래그가 false이면 저장된 GraduationProgress에서 데이터 반환
        if (!member.isUpdate()) {
            GraduationProgress progress = graduationProgressRepository.findByStudentId(currentStudentId)
                    .orElseThrow(() -> new RuntimeException("Graduation progress not found for studentId: " + currentStudentId));

            return new GraduationCheckResult(
                    department,
                    String.valueOf(enrollmentYear),
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

        // 해당 사용자의 수강한 강좌 정보 조회
        List<CompletedCourse> courses = completedCourseRepository.findByMemberId(currentStudentId);

        // 학점 합산
        int earnedMajorRequired = 0;
        int earnedMajorElective = 0;
        int earnedGeneralRequired = 0;
        int earnedGeneralElective = 0;
        int earnedElectiveMajor = 0;
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

        // 전공 초과 학점 처리
        if ("컴퓨터공학과".equals(department)) {
            int reqMajorRequired = requirements.getMajorCreditsRequired();
            int reqMajorElective = requirements.getMajorElectiveCredits();
            int reqElectiveMajor = requirements.getElectiveMajor();

            if (earnedMajorRequired > reqMajorRequired) {
                int excess = earnedMajorRequired - reqMajorRequired;
                earnedMajorRequired = reqMajorRequired;
                earnedElectiveMajor += excess;
            }
            if (earnedMajorElective > reqMajorElective) {
                int excess = earnedMajorElective - reqMajorElective;
                earnedMajorElective = reqMajorElective;
                earnedElectiveMajor += excess;
            }
            if (earnedElectiveMajor > reqElectiveMajor) {
                int excess = earnedElectiveMajor - reqElectiveMajor;
                earnedElectiveMajor = reqElectiveMajor;
                earnedOther += excess;
            }
        } else {
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

        // GraduationProgress에 학점 저장
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

        // update 플래그 false로 변경
        member.setUpdate(false);
        memberRepository.save(member);

        // GraduationCheckResult 반환
        GraduationCheckResult result = new GraduationCheckResult(
                department,
                String.valueOf(enrollmentYear),
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

        return result;
    }
}
