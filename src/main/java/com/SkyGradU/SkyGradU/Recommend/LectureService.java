package com.SkyGradU.SkyGradU.Recommend;

import com.SkyGradU.SkyGradU.Lectures.AllLectures;
import com.SkyGradU.SkyGradU.Lectures.AllLecturesDTO;
import com.SkyGradU.SkyGradU.Lectures.AllLectureRepository;
import com.SkyGradU.SkyGradU.User.Courses.CompletedCourse;
import com.SkyGradU.SkyGradU.User.Courses.CompletedCourseRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LectureService {
    private final AllLectureRepository      lectureRepo;
    private final CompletedCourseRepository completedRepo;
    private final RestTemplate restTemplate;

    public LectureService(AllLectureRepository lectureRepo,
                          CompletedCourseRepository completedRepo,
                          RestTemplate restTemplate) {
        this.lectureRepo   = lectureRepo;
        this.completedRepo = completedRepo;
        this.restTemplate = restTemplate;
    }

    @Transactional(readOnly = true)
    public List<AllLecturesDTO> getPersonalizedRecommendations(String memberId, int limit) {
        // 1. Get recommendations from Python service
        List<String> recommendedCourseNames = getCollaborativeFilteringRecommendations(memberId, limit);

        // 2. If the recommendation service returns nothing, fall back to the old logic
        if (recommendedCourseNames == null || recommendedCourseNames.isEmpty()) {
            return getTopCultureElectivesWithCompletion(memberId, limit);
        }

        // 3. Get the lecture details from the database
        List<AllLectures> recommendedLectures = lectureRepo.findByCourseNameIn(recommendedCourseNames);

        // 4. Filter the recommendations
        // TODO: Get user's major information to filter for "일선"
        List<AllLectures> filteredLectures = recommendedLectures.stream()
                .filter(lec -> "교선".equals(lec.getCourseType())) // "교양선택" 필터링
                .toList();


        // 5. Get the set of completed courses for the user
        Set<String> doneNames = completedRepo.findByMemberId(memberId).stream()
                .map(CompletedCourse::getCourseName)
                .collect(Collectors.toSet());

        // 6. Convert to DTO and set the completed flag
        return filteredLectures.stream()
                .map(lec -> {
                    AllLecturesDTO dto = new AllLecturesDTO(lec);
                    dto.setCompleted(doneNames.contains(lec.getCourseName()));
                    return dto;
                })
                .toList();
    }

    private List<String> getCollaborativeFilteringRecommendations(String memberId, int limit) {
        URI uri = UriComponentsBuilder
                .fromUriString("http://127.0.0.1:5000/recommend")
                .queryParam("memberId", memberId)
                .queryParam("limit", limit)
                .build()
                .toUri();
        try {
            String[] recommendedCourses = restTemplate.getForObject(uri, String[].class);
            if (recommendedCourses != null) {
                return List.of(recommendedCourses);
            }
        } catch (Exception e) {
            // Log the exception
            System.err.println("Error calling recommendation service: " + e.getMessage());
        }
        return new ArrayList<>();
    }


    /**
     * @param memberId 현재 로그인된 사용자의 학번
     * @param limit    최대 가져올 강의 개수
     * @return count 내림차순으로 교선 강의 상위 limit개, completed 플래그까지 채움
     */
    @Transactional(readOnly = true)
    public List<AllLecturesDTO> getTopCultureElectivesWithCompletion(String memberId, int limit) {
        Pageable page = PageRequest.of(0, limit);

        // 1) 추천 순위(교선+count desc) 강의 조회
        List<AllLectures> lectures =
                lectureRepo.findByCourseTypeOrderByCountDesc("교선", page);

        // 2) 이수한 강의명 집합 조회
        Set<String> doneNames = completedRepo.findByMemberId(memberId).stream()
                .map(CompletedCourse::getCourseName)
                .collect(Collectors.toSet());

        // 3) DTO 변환 & completed 여부 세팅
        return lectures.stream()
                .map(lec -> {
                    AllLecturesDTO dto = new AllLecturesDTO(lec);
                    dto.setCompleted(doneNames.contains(lec.getCourseName()));
                    return dto;
                })
                .toList();
    }
}