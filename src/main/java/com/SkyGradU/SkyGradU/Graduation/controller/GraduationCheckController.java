package com.SkyGradU.SkyGradU.Graduation.controller;

import com.SkyGradU.SkyGradU.Graduation.dto.GraduationCheckResult;
import com.SkyGradU.SkyGradU.Graduation.service.GraduationCheckService;
import com.SkyGradU.SkyGradU.User.member.CustomUser;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GraduationCheckController {

    private final GraduationCheckService graduationCheckService;

    public GraduationCheckController(GraduationCheckService graduationCheckService) {
        this.graduationCheckService = graduationCheckService;
    }

    @GetMapping("/graduation-check")
    public String getGraduationCheck(Authentication auth, Model model) {
        CustomUser user = (CustomUser) auth.getPrincipal();
        String studentId = user.studentID; // CustomUser 클래스에 public 필드 또는 getter가 있어야 함

        GraduationCheckResult result = graduationCheckService.checkGraduationRequirements(studentId);
        model.addAttribute("graduationResult", result);
        model.addAttribute("student", user);

        return "result";  // templates/result.html
    }
}
