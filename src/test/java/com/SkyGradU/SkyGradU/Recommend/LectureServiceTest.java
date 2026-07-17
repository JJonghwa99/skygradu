package com.SkyGradU.SkyGradU.Recommend;

import com.SkyGradU.SkyGradU.Lectures.AllLectureRepository;
import com.SkyGradU.SkyGradU.Lectures.AllLectures;
import com.SkyGradU.SkyGradU.Lectures.AllLecturesDTO;
import com.SkyGradU.SkyGradU.User.Courses.CompletedCourse;
import com.SkyGradU.SkyGradU.User.Courses.CompletedCourseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LectureServiceTest {

    @InjectMocks
    private LectureService lectureService;

    @Mock private AllLectureRepository lectureRepo;
    @Mock private CompletedCourseRepository completedRepo;
    @Mock private RestTemplate restTemplate;

    @Test
    @DisplayName("Flask 추천 서버가 정상 응답할 때 상세 강의 매핑 및 기이수 플래그 필터링 확인")
    void testGetPersonalizedRecommendationsSuccess() {
        // Given
        String memberId = "20191234";
        int limit = 10;
        String[] mockFlaskResponse = {"컴퓨터네트워크", "데이터베이스"};

        when(restTemplate.getForObject(any(URI.class), eq(String[].class)))
                .thenReturn(mockFlaskResponse);

        AllLectures lec1 = new AllLectures();
        lec1.setCourseName("컴퓨터네트워크");
        lec1.setCourseType("교선"); // "교선"만 통과되도록 비즈니스 로직에 구현되어 있음

        AllLectures lec2 = new AllLectures();
        lec2.setCourseName("데이터베이스");
        lec2.setCourseType("교선");

        List<AllLectures> mockLectures = Arrays.asList(lec1, lec2);
        when(lectureRepo.findByCourseNameIn(anyList())).thenReturn(mockLectures);

        // 사용자가 이미 '컴퓨터네트워크' 과목은 이수했다고 가정
        CompletedCourse completed = new CompletedCourse();
        completed.setCourseName("컴퓨터네트워크");
        completed.setCredits(3);
        when(completedRepo.findByMemberId(memberId)).thenReturn(Collections.singletonList(completed));

        // When
        List<AllLecturesDTO> results = lectureService.getPersonalizedRecommendations(memberId, limit);

        // Then
        assertNotNull(results);
        assertEquals(2, results.size());

        // 첫 번째 과목인 '컴퓨터네트워크'는 completed 플래그가 true여야 함
        AllLecturesDTO dto1 = results.stream()
                .filter(d -> "컴퓨터네트워크".equals(d.getCourseName()))
                .findFirst().orElseThrow();
        assertTrue(dto1.isCompleted());

        // 두 번째 과목인 '데이터베이스'는 completed 플래그가 false여야 함
        AllLecturesDTO dto2 = results.stream()
                .filter(d -> "데이터베이스".equals(d.getCourseName()))
                .findFirst().orElseThrow();
        assertFalse(dto2.isCompleted());
    }

    @Test
    @DisplayName("Flask 서버 오프라인/통신 에러 발생 시 인기 강의 목록을 조회하는 Fallback 메커니즘 검증")
    void testGetRecommendationsFallbackWhenFlaskFails() {
        // Given
        String memberId = "20191234";
        int limit = 10;

        // Flask API 통신 시 예외 발생 시뮬레이션
        when(restTemplate.getForObject(any(URI.class), eq(String[].class)))
                .thenThrow(new RestClientException("Connection Timeout"));

        // Fallback 로직에서 DB 인기강의 조회 응답 모의
        AllLectures popularLec = new AllLectures();
        popularLec.setCourseName("인기교양강좌");
        popularLec.setCourseType("교선");
        popularLec.setCount(100);

        when(lectureRepo.findByCourseTypeOrderByCountDesc(eq("교선"), any(Pageable.class)))
                .thenReturn(Collections.singletonList(popularLec));
        when(completedRepo.findByMemberId(memberId)).thenReturn(new ArrayList<>());

        // When
        List<AllLecturesDTO> results = lectureService.getPersonalizedRecommendations(memberId, limit);

        // Then
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("인기교양강좌", results.get(0).getCourseName());
        assertFalse(results.get(0).isCompleted());

        // Flask 에러 시 DB 이수 횟수 기준 조회 쿼리가 구동되었는지 검증
        verify(lectureRepo, times(1)).findByCourseTypeOrderByCountDesc(eq("교선"), any(Pageable.class));
        verify(lectureRepo, never()).findByCourseNameIn(anyList());
    }
}
