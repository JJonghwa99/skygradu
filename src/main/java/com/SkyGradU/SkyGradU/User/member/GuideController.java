package com.SkyGradU.SkyGradU.User.member; // 올바른 패키지 경로 확인

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GuideController {

    @GetMapping("/guide")
    public String guidePage() {
        return "guide";  // templates/guide.html 반환
    }

    @GetMapping("/privacy")
    public String privacyPage() {
        return "privacy";  // templates/privacy.html 반환
    }
}