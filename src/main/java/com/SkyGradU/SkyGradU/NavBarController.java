package com.SkyGradU.SkyGradU;

import com.SkyGradU.SkyGradU.QnA.QnA;
import com.SkyGradU.SkyGradU.QnA.QnARepository;
import com.SkyGradU.SkyGradU.User.member.MemberRepository;
import com.SkyGradU.SkyGradU.VIsit.VisitService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class NavBarController {
    @Autowired
    private QnARepository qnaRepository;
    @Autowired
    private final VisitService visitService;
    @Autowired
    private final MemberRepository memberRepository;

    public NavBarController(VisitService visitService, MemberRepository memberRepository) {
        this.visitService = visitService;
        this.memberRepository = memberRepository;
    }

    @GetMapping("")
    public String start(Model model, HttpServletRequest request, HttpSession session) {

        // 세션 기준 방문 기록
        visitService.recordVisitOncePerSession(request, session);

        long todayVisits = visitService.getTodayVisitCount();
        long totalVisits = visitService.getTotalVisitCount();
        long userCount = memberRepository.count();

        model.addAttribute("todayVisits", todayVisits);
        model.addAttribute("totalVisits", totalVisits);
        model.addAttribute("userCount", userCount - 1); //관리자계정 제외

        return "Start.html";
    }

    @GetMapping("/login")
    public String login() { return "login.html";}

    @GetMapping("/auth")
    public String authPage() {
        return "auth";
    }

    @GetMapping("/success")
    public String successPage() {
        return "success";
    }

    @GetMapping("/qna")
    public String qnaPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<QnA> qnaPage = qnaRepository.findAll(pageable);

        // 페이지네이션 데이터 계산
        int totalPages = qnaPage.getTotalPages();
        int startPage = Math.max(0, page - 5);
        int endPage = Math.min(totalPages - 1, page + 5);

        // 모델에 데이터 추가
        model.addAttribute("qnaList", qnaPage.getContent());
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "qnaList";
    }


    @GetMapping("/qna/write")
    public String qnaWritePage() { return "qnaWrite";}

    @GetMapping("/guide")
    public String guidePage() { return "guide"; }

    @GetMapping("/privacy")
    public String privacyPage() { return "privacy"; }

    @GetMapping("/faq")
    public String faqPage() { return "faq"; }

}
