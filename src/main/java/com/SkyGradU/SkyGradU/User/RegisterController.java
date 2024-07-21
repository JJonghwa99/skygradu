package com.SkyGradU.SkyGradU.User;

import com.SkyGradU.SkyGradU.User.member.Member;
import com.SkyGradU.SkyGradU.User.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
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
        return "register";
    }

    private final MemberRepository memberRepository;

    @PostMapping("/member")
    @ResponseBody
    public String addMember(@RequestParam String userName,
                            @RequestParam String studentID,
                            @RequestParam String major,
                            @RequestParam String userEmail,
                            @RequestParam String password1) {
        try {
            if (password1.length() < 8) {
                return "error2";
            }

            if (memberRepository.findByStudentID(studentID).isPresent()) {
                return "error1";
            }

            Member member = new Member();
            member.setStudentID(studentID);
            member.setUserName(userName);
            member.setMajor(major);
            member.setUserEmail(userEmail);
            var encoder = new BCryptPasswordEncoder();
            String encodedPassword = encoder.encode(password1);
            member.setPassword1(encodedPassword);
            memberRepository.save(member);

            return "success";

        } catch (Exception e) {
            return "error";
        }
    }
}
