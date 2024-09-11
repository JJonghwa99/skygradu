package com.SkyGradU.SkyGradU;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StartpageController {
    @GetMapping("")
    String Start(Model model){
        return "Start.html";
    }

}
