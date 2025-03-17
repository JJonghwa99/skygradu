package com.SkyGradU.SkyGradU.User.member;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/qna")
public class QnAController {

    @GetMapping("")
    public String qnaPage() {
        return "qna";  // ✅ qna.html을 반환해야 함
    }

    @GetMapping("/write")
    public String qnaWritePage() {
        return "qna-write";  // ✅ 질문 작성 페이지 반환
    }
}
