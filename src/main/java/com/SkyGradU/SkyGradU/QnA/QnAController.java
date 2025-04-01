package com.SkyGradU.SkyGradU.QnA;

import com.SkyGradU.SkyGradU.QnA.QnA;
import com.SkyGradU.SkyGradU.QnA.QnARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.RedirectView;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/qna")
public class QnAController {

    @Autowired
    private QnARepository qnaRepository;

    // 질문 등록
    @PostMapping("/request")
    public String createQuestion(@RequestBody QnaRequestDto request, @AuthenticationPrincipal User user) {
        String username = user.getUsername();

        QnA qna = new QnA();
        qna.setQTitle(request.getTitle());
        qna.setQWriter(username);
        qna.setAnonymity(request.getSecret() != null ? request.getSecret() : false);
        qna.setQContent(request.getContent());
        qna.setQDate(LocalDateTime.now());
        qnaRepository.save(qna);

        return "redirect:/qna";
    }

    @GetMapping("/detail/{id}")
    public String getQnADetail(@PathVariable Long id, Model model, @AuthenticationPrincipal User user) {
        // 질문 데이터 조회
        QnA qna = qnaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid question ID: " + id));

        // 현재 로그인된 사용자 정보
        String username = user.getUsername();
        boolean isAdmin = user.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        boolean isWriter = username.equals(qna.getQWriter());

        // 모델에 데이터 추가
        model.addAttribute("qna", qna);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isWriter", isWriter);

        return "qnadetail";
    }

    @PostMapping("/answer/{id}")
    public String postAnswer(@PathVariable Long id, @RequestParam String answerContent) {
        // 질문 데이터 조회 및 답변 등록
        QnA qna = qnaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid question ID: " + id));

        qna.setAnswered(true);
        qna.setAContent(answerContent);
        qna.setADate(LocalDateTime.now());

        qnaRepository.save(qna);

        return "redirect:/qna/detail/" + id;
    }

    @PostMapping("/delete/{id}")
    public String deleteQuestion(@PathVariable Long id) {
        qnaRepository.deleteById(id);
        return "redirect:/qna";
    }


}