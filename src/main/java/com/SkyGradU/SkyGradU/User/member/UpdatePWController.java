package com.SkyGradU.SkyGradU.User.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UpdatePWController {

    @Autowired
    private UserService userService;

    @PostMapping("/checkPW")
    public Map<String, Object> verifyCurrentPassword(@RequestBody Map<String, String> request) {
        String currentPassword = request.get("currentPassword");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Map<String, Object> response = new HashMap<>();

        if (userService.verifyPassword(username, currentPassword)) {
            response.put("success", true);
        } else {
            response.put("success", false);
        }

        return response;
    }

    @PostMapping("/changePW")
    public Map<String, Object> changePassword(@RequestBody Map<String, String> request) {
        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Map<String, Object> response = new HashMap<>();

        if (userService.changePassword(username, currentPassword, newPassword)) {
            response.put("success", true);
        } else {
            response.put("success", false);
        }

        return response;
    }
}
