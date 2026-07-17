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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GraduationCheckService {

    @Autowired private CompletedCourseRepository completedCourseRepository;
    @Autowired private GraduationRequirementsRepository graduationRequirementsRepository;
    @Autowired private GraduationProgressRepository graduationProgressRepository;
    @Autowired private MemberRepository memberRepository;

    @Transactional
    public GraduationCheckResult checkGraduationRequirements(String studentId) {
        // 1) 로그인한 사용자 & Member 조회
        CustomUser user = (CustomUser) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        String currentStudentId = user.studentID;
        Member member = memberRepository.findByStudentID(currentStudentId)
                .orElseThrow(() -> new RuntimeException(
                        "Member not found for studentId: " + currentStudentId));

        String department   = member.getMajor();
        int    enrollmentYear = Integer.parseInt(member.getEnrollYear());
        String electiveType = member.getElective();    // "심화" | "부전" | "복전"
        String minorDept    = member.getMinor();
        int    minorYear    = Integer.parseInt(member.getMinorYear());

        // 2) 기준 졸업요건 조회 (2020년 이전은 2020으로 고정)
        int baseYear = Math.max(enrollmentYear, 2020);
        GraduationRequirements req = graduationRequirementsRepository
                .findByDepartmentAndEnrollmentYear(department, baseYear)
                .orElseThrow(() -> new RuntimeException(
                        "Requirements not found for " + department + " year " + baseYear));

        // 3) 원본 elective_major / other_credits 보관
        int origElectiveMajor = req.getElectiveMajor();
        int origOtherCredits  = req.getOtherCredits();

        // 4) elective_major 기준 계산
        int electiveThreshold = origElectiveMajor;
        if ("부전".equals(electiveType)) {
            GraduationRequirements minorReq = graduationRequirementsRepository
                    .findByDepartmentAndEnrollmentYear(minorDept, minorYear)
                    .orElseThrow(() -> new RuntimeException(
                            "Minor requirements not found for " + minorDept + " year " + minorYear));
            electiveThreshold = minorReq.getElectiveMajor();
        } else if ("복전".equals(electiveType)) {
            GraduationRequirements minorReq = graduationRequirementsRepository
                    .findByDepartmentAndEnrollmentYear(minorDept, minorYear)
                    .orElseThrow(() -> new RuntimeException(
                            "Double-major requirements not found for " + minorDept + " year " + minorYear));
            electiveThreshold = minorReq.getMajorCreditsRequired()
                    + minorReq.getMajorElectiveCredits();
        }

        // 5) 메모리 상 other_credits 조정
        int delta = electiveThreshold - origElectiveMajor;
        int adjustedOtherCredits = origOtherCredits;
        if (delta > 0) {
            adjustedOtherCredits = Math.max(0, origOtherCredits - delta);
        }

        // 6) 이미 계산된 진행상황이 있으면 DTO로 반환
        if (!member.isUpdate()) {
            GraduationProgress stored = graduationProgressRepository
                    .findByStudentId(currentStudentId)
                    .orElseThrow(() -> new RuntimeException(
                            "Progress not found for studentId: " + currentStudentId));

            return new GraduationCheckResult(
                    department,
                    String.valueOf(enrollmentYear),
                    electiveType,
                    minorDept,
                    String.valueOf(minorYear),
                    req.getMajorCreditsRequired(),
                    req.getMajorElectiveCredits(),
                    req.getGeneralEducationRequired(),
                    req.getGeneralElectiveCredits(),
                    electiveThreshold,
                    adjustedOtherCredits,
                    req.getChapelRequired(),
                    req.getTotalCreditsRequired(),
                    stored.getMajorCreditsEarned(),
                    stored.getMajorElectiveEarned(),
                    stored.getGeneralEducationEarned(),
                    stored.getGeneralElectiveEarned(),
                    stored.getElectiveMajorEarned(),
                    stored.getOtherEarned(),
                    stored.getTotalCreditsEarned(),
                    stored.getChapelCompleted()
            );
        }

        // 7) 이수 과목 조회 및 학점 집계
        List<CompletedCourse> courses = completedCourseRepository
                .findByMemberId(currentStudentId);

        int earnedMajorRequired   = 0;
        int earnedMajorElective   = 0;
        int earnedGeneralRequired = 0;
        int earnedGeneralElective = 0;
        int earnedElectiveMajor   = 0;
        int earnedOther           = 0;
        int totalEarned           = 0;
        int chapelCompleted       = 0;

        for (CompletedCourse c : courses) {
            int cr = c.getCredits();
            totalEarned += cr;

            String name = c.getCourseName();
            if (name != null && name.contains("채플")) {
                chapelCompleted++;
            }

            String type = c.getCourseType();
            if (type == null) {
                earnedOther += cr;
            } else if (type.contains("전필")) {
                if ("컴퓨터공학과".equals(department)) {
                    if (name != null && name.contains("전공종합설계")) {
                        earnedMajorRequired += cr;
                    } else {
                        earnedMajorElective += cr;
                    }
                } else {
                    earnedMajorRequired += cr;
                }
            } else if (type.contains("전선")) {
                earnedMajorElective += cr;
            } else if (type.contains("교필")) {
                earnedGeneralRequired += cr;
            } else if (type.contains("교선")) {
                earnedGeneralElective += cr;
            } else if (type.contains("부전") || type.contains("복전")) {
                earnedElectiveMajor += cr;
            } else {
                earnedOther += cr;
            }
        }

        // 8) 초과 학점 순차 처리
        int reqMajReq  = req.getMajorCreditsRequired();
        int reqMajElec = req.getMajorElectiveCredits();
        int reqElecMaj = electiveThreshold;
        int reqGenReq  = req.getGeneralEducationRequired();
        int reqGenElec = req.getGeneralElectiveCredits();

        int overflow;
        // 전필 → 전선
        if (earnedMajorRequired > reqMajReq) {
            overflow = earnedMajorRequired - reqMajReq;
            earnedMajorRequired = reqMajReq;
            earnedMajorElective += overflow;
        }
        // 전선 → (심화는 선택전공, 부전/복전은 기타)
        if (earnedMajorElective > reqMajElec) {
            overflow = earnedMajorElective - reqMajElec;
            earnedMajorElective = reqMajElec;
            if ("심화".equals(electiveType)) {
                earnedElectiveMajor += overflow;
            } else {
                earnedOther += overflow;
            }
        }
        // 심화만 선택전공 → 기타
        if ("심화".equals(electiveType) && earnedElectiveMajor > reqElecMaj) {
            overflow = earnedElectiveMajor - reqElecMaj;
            earnedElectiveMajor = reqElecMaj;
            earnedOther += overflow;
        }
        // 교필 → 기타
        if (earnedGeneralRequired > reqGenReq) {
            overflow = earnedGeneralRequired - reqGenReq;
            earnedGeneralRequired = reqGenReq;
            earnedOther += overflow;
        }
        // 교선 → 기타
        if (earnedGeneralElective > reqGenElec) {
            overflow = earnedGeneralElective - reqGenElec;
            earnedGeneralElective = reqGenElec;
            earnedOther += overflow;
        }

        // 9) 계산된 진행상황 저장
        GraduationProgress prog = graduationProgressRepository
                .findByStudentId(currentStudentId)
                .orElse(new GraduationProgress());
        prog.setStudentId(currentStudentId);
        prog.setMajorCreditsEarned(   earnedMajorRequired);
        prog.setMajorElectiveEarned(   earnedMajorElective);
        prog.setGeneralEducationEarned(earnedGeneralRequired);
        prog.setGeneralElectiveEarned( earnedGeneralElective);
        prog.setElectiveMajorEarned(   earnedElectiveMajor);
        prog.setOtherEarned(           earnedOther);
        prog.setTotalCreditsEarned(    totalEarned);
        prog.setChapelCompleted(       chapelCompleted);
        graduationProgressRepository.save(prog);

        member.setUpdate(false);
        memberRepository.save(member);

        // 10) 결과 DTO 생성 및 반환
        return new GraduationCheckResult(
                department,
                String.valueOf(enrollmentYear),
                electiveType,
                minorDept,
                String.valueOf(minorYear),
                req.getMajorCreditsRequired(),
                req.getMajorElectiveCredits(),
                req.getGeneralEducationRequired(),
                req.getGeneralElectiveCredits(),
                electiveThreshold,
                adjustedOtherCredits,
                req.getChapelRequired(),
                req.getTotalCreditsRequired(),
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
