package com.SkyGradU.SkyGradU.Recommend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RecommendController {
    @GetMapping({"/recommend", "/recommend.html"})
    public String recommendPage() {
        return "recommend"; // recommend.html 템플릿을 반환
    }
}
