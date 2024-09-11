package com.SkyGradU.SkyGradU.User;

import com.SkyGradU.SkyGradU.User.member.CustomUser;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {
    @GetMapping("/login")
    public String login() {

        return "login.html";
    }

    @GetMapping("/mypage")
    public String myPage(Authentication auth, Model model) {
        CustomUser userDetails = (CustomUser) auth.getPrincipal();
        String userName = userDetails.userName;
        String studentID = userDetails.studentID;
        String major = userDetails.major;

        model.addAttribute("userName", userName);
        model.addAttribute("studentID", studentID);
        model.addAttribute("major", major);

        return "mypage.html";
    }
}
