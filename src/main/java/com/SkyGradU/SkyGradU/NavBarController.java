package com.SkyGradU.SkyGradU;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NavBarController {
    @GetMapping("")
    String Start(Model model){
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
    public String qnaPage() { return "qna"; }

    @GetMapping("/qna/write")
    public String qnaWritePage() { return "qna-write";}

    @GetMapping("/guide")
    public String guidePage() { return "guide"; }

    @GetMapping("/privacy")
    public String privacyPage() { return "privacy"; }

    @GetMapping("/faq")
    public String faqPage() { return "faq"; }

}
