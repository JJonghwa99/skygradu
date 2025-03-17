package com.SkyGradU.SkyGradU.User.member;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qna")
public class QnAApiController {

    @PostMapping("/write")
    public ResponseEntity<String> writeQuestion(@RequestBody QuestionRequest questionRequest) {
        // ✅ 질문 저장 로직 (DB 연동 필요)
        System.out.println("질문 제목: " + questionRequest.getTitle());
        System.out.println("익명 여부: " + questionRequest.isAnonymous());
        System.out.println("질문 내용: " + questionRequest.getContent());

        return ResponseEntity.ok("질문이 정상적으로 등록되었습니다.");
    }
}

// ✅ DTO 클래스
class QuestionRequest {
    private String title;
    private String content;
    private boolean anonymous;

    public String getTitle() { return title; }
    public String getContent() { return content; }
    public boolean isAnonymous() { return anonymous; }
}
