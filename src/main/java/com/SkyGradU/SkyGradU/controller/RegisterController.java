package com.SkyGradU.SkyGradU.controller;


import com.SkyGradU.SkyGradU.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Controller
public class RegisterController {

    @Autowired
    private AuthService authService;

    @GetMapping("/auth")
    public String authPage() {
        return "auth";
    }

    @PostMapping("/api/auth")
    @ResponseBody
    public Map<String, Object> authenticate(@RequestParam String userId, @RequestParam String password) {
        return authService.authenticate(userId, password);
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam Map<String, String> userDetails, Model model) {
        model.addAttribute("userDetails", userDetails);
        System.out.println(userDetails);
        return "register";
    }

}