package com.SkyGradU.SkyGradU.User.member;

import com.SkyGradU.SkyGradU.SkyAuth;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Controller
@RequiredArgsConstructor
public class RegisterController {

    @Autowired
    private SkyAuth skyAuth;


    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostMapping("/api/auth")
    @ResponseBody
    public ResponseEntity<ProfileResponseDTO> authenticate(@RequestParam String userId, @RequestParam String password) {
        JsonObject result = skyAuth.authenticate(userId, password);

        ProfileResponseDTO response = new ProfileResponseDTO();
        response.setAuth(result.get("is_auth").getAsBoolean());
        JsonObject profile = result.getAsJsonObject("profile");
        response.setStudentID(profile.get("studentID").getAsString());
        response.setUserName(profile.get("userName").getAsString());
        response.setMajor(profile.get("major").getAsString());
        response.setPortalID(profile.get("portalID").getAsString());

        return ResponseEntity.ok(response);
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
                            @RequestParam String password1,
                            @RequestParam String portalID) {
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
            member.setPortalID(portalID);
            String encodedPassword = passwordEncoder.encode(password1);
            member.setPassword1(encodedPassword);
            memberRepository.save(member);

            System.out.println(member);

            return "success";

        } catch (Exception e) {
            return "error";
        }
    }
}
