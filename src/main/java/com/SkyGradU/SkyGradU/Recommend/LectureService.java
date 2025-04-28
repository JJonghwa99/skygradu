package com.SkyGradU.SkyGradU.Recommend;

import com.SkyGradU.SkyGradU.Lectures.AllLectures;
import com.SkyGradU.SkyGradU.Lectures.AllLecturesDTO;
import com.SkyGradU.SkyGradU.Lectures.AllLectureRepository;
import com.SkyGradU.SkyGradU.User.Courses.CompletedCourse;
import com.SkyGradU.SkyGradU.User.Courses.CompletedCourseRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LectureService {
    private final AllLectureRepository      lectureRepo;
    private final CompletedCourseRepository completedRepo;

    public LectureService(AllLectureRepository lectureRepo,
                          CompletedCourseRepository completedRepo) {
        this.lectureRepo   = lectureRepo;
        this.completedRepo = completedRepo;
    }

    /**
     * @param memberId 현재 로그인된 사용자의 학번
     * @param limit    최대 가져올 강의 개수
     * @return count 내림차순으로 교선 강의 상위 limit개, completed 플래그까지 채움
     */
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
