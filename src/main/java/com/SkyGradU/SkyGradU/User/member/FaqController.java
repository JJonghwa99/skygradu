package com.SkyGradU.SkyGradU.User.member;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/faq")
public class FaqController {

    @GetMapping("")
    public String faqPage() {
        return "faq";  // ✅ faq.html을 반환
    }
}
