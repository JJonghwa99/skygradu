package com.SkyGradU.SkyGradU.User.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UpdateUserController {

    private final UserService userService;
    private final AuthService authService;

    @Autowired
    public UpdateUserController(UserService userService,forgetPWService forgetPWService,AuthService authService) {
        this.userService = userService;
        this.forgetPWService = forgetPWService;
        this.authService = authService;

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

    //로그인페이지 비밀번호 찾기
    private final forgetPWService forgetPWService;

    @PostMapping("/login/checkPortal")
    public Map<String, Object> changePW(@RequestParam String userId, @RequestParam String password, HttpSession session) {
        Map<String, Object> result = forgetPWService.checkPortal(userId, password);
        if ("exists".equals(result.get("status"))) {
            session.setAttribute("UserId", userId);
        }
        return result;
    }

    @PostMapping("/login/changePW")
    public Map<String, Object> changeLoginPW(@RequestBody Map<String, String> request, HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        String newPassword = request.get("newPassword");

        String userId = (String) session.getAttribute("UserId");
        if (userId == null || userId.isEmpty()) {
            result.put("status", "session_expired");
            result.put("message", "세션이 만료되었습니다. 다시 인증해주세요.");
            return result;
        }
        return forgetPWService.changePassword(userId, newPassword);
    }

    //마이페이지 정보 업데이트(전과하는 경우만 있으므로 학과만 가져오기)
    @PostMapping("/updateMajor")
    public ResponseEntity<Map<String, Object>> updateMajor(@RequestParam String userId, @RequestParam String password) {

        Map<String, Object> response = authService.getMajor(userId, password);
        return ResponseEntity.ok(response);
    }
}
