package com.SkyGradU.SkyGradU.User.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UpdateUserController {

    private final UserService userService;

    @Autowired
    public UpdateUserController(UserService userService) {
        this.userService = userService;
    }

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

    @DeleteMapping("/member/delete")
    public ResponseEntity<String> deleteAccount(@AuthenticationPrincipal UserDetails userDetails,
                                                HttpServletRequest request, HttpServletResponse response) {
        String studentID = userDetails.getUsername();
        userService.deleteMemberByStudentID(studentID);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        return ResponseEntity.ok("회원탈퇴가 완료되었습니다.");
    }
}
