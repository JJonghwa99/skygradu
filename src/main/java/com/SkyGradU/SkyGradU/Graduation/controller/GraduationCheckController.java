package com.SkyGradU.SkyGradU.Graduation.controller;

import com.SkyGradU.SkyGradU.Graduation.dto.GraduationCheckResult;
import com.SkyGradU.SkyGradU.Graduation.service.GraduationCheckService;
import com.SkyGradU.SkyGradU.User.Courses.CompletedCourseRepository;
import com.SkyGradU.SkyGradU.User.Courses.CompletedCourse;
import com.SkyGradU.SkyGradU.User.member.CustomUser;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class GraduationCheckController {

    private final GraduationCheckService graduationCheckService;
    private final CompletedCourseRepository completedCourseRepository;

    public GraduationCheckController(GraduationCheckService graduationCheckService,
                                     CompletedCourseRepository completedCourseRepository) {
        this.graduationCheckService = graduationCheckService;
        this.completedCourseRepository = completedCourseRepository;
    }

    @GetMapping("/graduation-check")
    public String getGraduationCheck(Authentication auth, Model model) {
        CustomUser user = (CustomUser) auth.getPrincipal();
        String studentId = user.studentID; // CustomUser 클래스에 public 필드 또는 getter가 있어야 함

        GraduationCheckResult result = graduationCheckService.checkGraduationRequirements(studentId);
        model.addAttribute("graduationResult", result);
        model.addAttribute("student", user);

        List<String> completedNames = completedCourseRepository.findByMemberId(studentId)
                .stream()
                .map(CompletedCourse::getCourseName)
                .collect(Collectors.toList());
        model.addAttribute("completedNames", completedNames);

        return "result";  // templates/result.html
    }
}
