package com.SkyGradU.SkyGradU.Recommend;

import com.SkyGradU.SkyGradU.Lectures.AllLecturesDTO;
import com.SkyGradU.SkyGradU.User.member.CustomUser;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class RecommendController {
    private final LectureService lectureService;

    public RecommendController(LectureService lectureService) {
        this.lectureService = lectureService;
    }

    @GetMapping({"/recommend"})
    public String recommendPage(Authentication auth, Model model) {
        // Authentication에서 CustomUser 꺼내기
        CustomUser user = (CustomUser) auth.getPrincipal();

        // public 필드 studentID 직접 사용
        String memberId = user.studentID;

        // 추천 목록 + 이수 여부 가져오기
        List<AllLecturesDTO> lectures =
                lectureService.getTopCultureElectivesWithCompletion(memberId, 50);

        // 뷰에 바인딩
        model.addAttribute("lectures", lectures);
        model.addAttribute("student", user);

        return "recommend";  // templates/recommend.html
    }
}
