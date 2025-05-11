package com.SkyGradU.SkyGradU.User.member;

import com.SkyGradU.SkyGradU.User.Courses.CompletedCourseDTO;
import com.SkyGradU.SkyGradU.User.Courses.CompletedCourseRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class MyPageController {
    public MyPageController(CompletedCourseRepository completedCourseRepository,
                            MemberRepository memberRepository) {
        this.completedCourseRepository = completedCourseRepository;
        this.memberRepository = memberRepository;
    }
    private final CompletedCourseRepository completedCourseRepository;
    private final MemberRepository memberRepository;



    @GetMapping("/mypage")
    public String myPage(Authentication auth, Model model) {
        CustomUser userDetails = (CustomUser) auth.getPrincipal();

        String userName = userDetails.userName;
        String studentID = userDetails.studentID;
        String major = userDetails.major;
        String portalID = userDetails.portalID;

        // studentID와 일치하는 데이터 조회
        List<CompletedCourseDTO> completedCourses = completedCourseRepository.findByMemberId(studentID)
                .stream()
                .map(course -> new CompletedCourseDTO(
                        course.getYear(),
                        course.getSemesterCompleted(),
                        course.getCourseName(),
                        course.getCourseType(),
                        course.getCredits(),
                        course.isCustom()
                ))
                .sorted(Comparator.comparing(CompletedCourseDTO::isCustom).reversed()) //  커스텀된 과목을 먼저 배치
                .collect(Collectors.toList());


        model.addAttribute("userName",      userName);
        model.addAttribute("studentID",     studentID);
        model.addAttribute("major",         major);
        model.addAttribute("portalID",      portalID);
        model.addAttribute("courses",       completedCourses);

        Member member = memberRepository.findByStudentID(studentID)
                .orElseThrow(() -> new RuntimeException("Member not found: " + studentID));
        model.addAttribute("elective",    member.getElective());
        model.addAttribute("minorDept",   member.getMinor());
        model.addAttribute("minorYear",   member.getMinorYear());



        return "mypage.html";
    }
    @PostMapping("/mypage/updateMinor")
    @ResponseBody
    public Map<String, String> updateMinorInfo(
            @AuthenticationPrincipal CustomUser userDetails,
            @RequestParam int year,
            @RequestParam String electiveType,
            @RequestParam String minorDept
    ) {
        String studentID = userDetails.studentID;
        Member member = memberRepository.findByStudentID(studentID)
                .orElseThrow(() -> new RuntimeException("Member not found: " + studentID));

        member.setElective(electiveType);
        member.setMinor(minorDept);
        member.setMinorYear(String.valueOf(year));
        member.setUpdate(true);
        memberRepository.save(member);
        return Collections.singletonMap("status", "success");
    }


    @GetMapping("/custom")
    public String customPage(Authentication auth, Model model) {
        CustomUser userDetails = (CustomUser) auth.getPrincipal();
        String studentID = userDetails.studentID;

        // studentID와 일치하는 데이터 조회
        List<CompletedCourseDTO> completedCourses = completedCourseRepository.findByMemberId(studentID)
                .stream()
                .map(course -> new CompletedCourseDTO(
                        course.getYear(),
                        course.getSemesterCompleted(),
                        course.getCourseName(),
                        course.getCourseType(),
                        course.getCredits(),
                        course.isCustom()
                ))
                .sorted(Comparator.comparing(CompletedCourseDTO::isCustom).reversed()) //  커스텀된 과목을 먼저 배치
                .collect(Collectors.toList());


        model.addAttribute("studentID", studentID);
        model.addAttribute("courses", completedCourses);

        return "custom.html";
    }
}
