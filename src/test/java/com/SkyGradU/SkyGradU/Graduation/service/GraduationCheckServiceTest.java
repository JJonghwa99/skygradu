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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GraduationCheckServiceTest {

    @InjectMocks
    private GraduationCheckService graduationCheckService;

    @Mock private CompletedCourseRepository completedCourseRepository;
    @Mock private GraduationRequirementsRepository graduationRequirementsRepository;
    @Mock private GraduationProgressRepository graduationProgressRepository;
    @Mock private MemberRepository memberRepository;

    private SecurityContext originalSecurityContext;

    @BeforeEach
    void setUp() {
        // Spring Security Context Mocking
        originalSecurityContext = SecurityContextHolder.getContext();
        
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        CustomUser customUser = new CustomUser("20191234", "password", Collections.emptyList());
        customUser.userName = "홍길동";
        customUser.studentID = "20191234";
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(customUser);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        // Security Context 복구
        SecurityContextHolder.setContext(originalSecurityContext);
    }

    @Test
    @DisplayName("회원 정보의 isUpdate가 false일 때 기존 계산된 결과(캐싱)를 바로 반환하는지 테스트")
    void testCheckRequirementsWhenUpdateIsFalse() {
        // Given
        String studentId = "20191234";
        Member member = new Member();
        member.setStudentID(studentId);
        member.setMajor("컴퓨터공학과");
        member.setEnrollYear("2019");
        member.setElective("심화");
        member.setUpdate(false); // 업데이트 필요 없음 (캐시 사용)

        GraduationRequirements req = new GraduationRequirements();
        req.setDepartment("컴퓨터공학과");
        req.setEnrollmentYear(2019);
        req.setMajorCreditsRequired(18);
        req.setMajorElectiveCredits(45);
        req.setGeneralEducationRequired(14);
        req.setGeneralElectiveCredits(20);
        req.setElectiveMajor(0);
        req.setOtherCredits(33);
        req.setChapelRequired(4);
        req.setTotalCreditsRequired(130);

        GraduationProgress progress = new GraduationProgress();
        progress.setStudentId(studentId);
        progress.setMajorCreditsEarned(18);
        progress.setMajorElectiveEarned(45);
        progress.setGeneralEducationEarned(14);
        progress.setGeneralElectiveEarned(20);
        progress.setElectiveMajorEarned(0);
        progress.setOtherEarned(33);
        progress.setTotalCreditsEarned(130);
        progress.setChapelCompleted(4);

        when(memberRepository.findByStudentID(studentId)).thenReturn(Optional.of(member));
        when(graduationRequirementsRepository.findByDepartmentAndEnrollmentYear("컴퓨터공학과", 2019))
                .thenReturn(Optional.of(req));
        when(graduationProgressRepository.findByStudentId(studentId)).thenReturn(Optional.of(progress));

        // When
        GraduationCheckResult result = graduationCheckService.checkGraduationRequirements(studentId);

        // Then
        assertNotNull(result);
        assertEquals(18, result.getEarnedMajorRequired());
        assertEquals(45, result.getEarnedMajorElective());
        assertEquals(130, result.getTotalEarned());
        
        // 이수과목 조회 및 신규 계산 로직은 실행되지 않아야 함
        verify(completedCourseRepository, never()).findByMemberId(anyString());
        verify(graduationProgressRepository, never()).save(any(GraduationProgress.class));
    }

    @Test
    @DisplayName("isUpdate가 true일 때 신규 학점 집계 및 초과학점 이월(Overflow) 로직 검증")
    void testCheckRequirementsWhenUpdateIsTrue() {
        // Given
        String studentId = "20191234";
        Member member = new Member();
        member.setStudentID(studentId);
        member.setMajor("컴퓨터공학과");
        member.setEnrollYear("2019");
        member.setElective("심화");
        member.setUpdate(true); // 새 연산 필요

        GraduationRequirements req = new GraduationRequirements();
        req.setDepartment("컴퓨터공학과");
        req.setEnrollmentYear(2019);
        req.setMajorCreditsRequired(18); // 전필 기준
        req.setMajorElectiveCredits(45); // 전선 기준
        req.setGeneralEducationRequired(14);
        req.setGeneralElectiveCredits(20);
        req.setElectiveMajor(0);
        req.setOtherCredits(33);
        req.setChapelRequired(4);
        req.setTotalCreditsRequired(130);

        // 취득한 과목 모의 데이터 구성 (전필 초과하여 전선으로 이월하는 시나리오)
        CompletedCourse course1 = new CompletedCourse();
        course1.setCourseName("자료구조(전필)");
        course1.setCourseType("전필");
        course1.setCredits(3);

        CompletedCourse course2 = new CompletedCourse();
        course2.setCourseName("알고리즘(전필)");
        course2.setCourseType("전필");
        course2.setCredits(3);

        CompletedCourse course4 = new CompletedCourse();
        course4.setCourseName("네트워크(전필)");
        course4.setCourseType("전필");
        course4.setCredits(15); // 총 전필 취득학점 = 3+3+15 = 21학점 (기준 18학점 초과 -> 초과 3학점은 전선으로 가야 함)

        CompletedCourse courseChapel = new CompletedCourse();
        courseChapel.setCourseName("채플1");
        courseChapel.setCourseType("교필");
        courseChapel.setCredits(0);

        List<CompletedCourse> mockCourses = Arrays.asList(course1, course2, course4, courseChapel);

        when(memberRepository.findByStudentID(studentId)).thenReturn(Optional.of(member));
        when(graduationRequirementsRepository.findByDepartmentAndEnrollmentYear("컴퓨터공학과", 2019))
                .thenReturn(Optional.of(req));
        when(completedCourseRepository.findByMemberId(studentId)).thenReturn(mockCourses);
        when(graduationProgressRepository.findByStudentId(studentId)).thenReturn(Optional.empty());

        // When
        GraduationCheckResult result = graduationCheckService.checkGraduationRequirements(studentId);

        // Then
        assertNotNull(result);
        // 전필은 기준값인 18학점까지만 취득한 것으로 제한되고
        assertEquals(18, result.getEarnedMajorRequired());
        // 초과한 3학점은 전공선택(MajorElective)으로 흘러 들어갔는지 확인
        assertEquals(3, result.getEarnedMajorElective());
        assertEquals(1, result.getChapelCompleted());
        
        // 연산 후 플래그가 false로 리셋되고 DB에 저장되었는지 확인
        assertFalse(member.isUpdate());
        verify(graduationProgressRepository, times(1)).save(any(GraduationProgress.class));
        verify(memberRepository, times(1)).save(member);
    }
}
