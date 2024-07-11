package com.SkyGradU.SkyGradU.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
public class RegisterController {



    @GetMapping("/auth")
    public String authForm(Model model) {
        model.addAttribute("content", "auth-form"); // Thymeleaf에서 사용할 변수 설정
        return "auth";
    }

    @GetMapping("/register")
    public String register(@RequestParam("token") String token) {
        // Validate token and return the registration page
        if (validateToken(token)) {
            return "register";
        } else {
            return "redirect:/auth?error=invalid_token";
        }
    }

    private boolean validateToken(String token) {
        // Add your token validation logic here
        return token.endsWith("_token");
    }
}
