package com.SkyGradU.SkyGradU.User.member;

import com.SkyGradU.SkyGradU.Lectures.AllLecturesDTO;
import com.SkyGradU.SkyGradU.Lectures.AllLectureRepository;
import com.SkyGradU.SkyGradU.Lectures.AllLectures;
import com.SkyGradU.SkyGradU.SkyAuth;
import com.SkyGradU.SkyGradU.User.Courses.CompletedCourse;
import com.SkyGradU.SkyGradU.User.Courses.CompletedCourseRepository;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class UpdateUserController {

    private final UserService userService;
    @Autowired
    private SkyAuth skyAuth;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CompletedCourseRepository completedCourseRepository;
    @Autowired
    private AllLectureRepository allLectureRepository;

    @Autowired
    public UpdateUserController(UserService userService,forgetPWService forgetPWService) {
        this.userService = userService;
        this.forgetPWService = forgetPWService;
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
        Map<String, Object> response = new HashMap<>();
        try {
            // 로그인 및 프로필 데이터 가져오기
            JsonObject result = skyAuth.authenticate(userId, password);

            // 로그인 여부 확인
            boolean isAuth = result.get("is_auth").getAsBoolean();
            if (!isAuth) {
                response.put("status", "no_login");
                response.put("message", "로그인 실패");
                return ResponseEntity.ok(response);
            }

            // 프로필 데이터 추출
            JsonObject profile = result.getAsJsonObject("profile");
            String newMajor = profile.get("major").getAsString();

            // 사용자 정보 가져오기
            Member member = memberRepository.findByPortalID(userId).orElseThrow(() ->
                    new IllegalArgumentException("사용자를 찾을 수 없습니다.")
            );

            // 기존 학과 정보와 비교하여 업데이트 수행
            if (!newMajor.equals(member.getMajor())) {
                member.setMajor(newMajor);
                memberRepository.save(member); // 변경 사항 저장

                response.put("status", "success");
                response.put("message", "학과 정보가 성공적으로 업데이트되었습니다.");
                response.put("newMajor", newMajor);
            } else {
                response.put("status", "no_change");
                response.put("message", "학과 정보에 변경 사항이 없습니다.");
            }
        } catch (IllegalArgumentException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "오류 발생: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/lectures/search")
    public ResponseEntity<List<AllLecturesDTO>> searchLectures(
            @RequestParam String keyword) {

        List<AllLectures> results = allLectureRepository.searchByCodeOrName(keyword);

        return ResponseEntity.ok(results.stream()
                .map(AllLecturesDTO::new)
                .collect(Collectors.toList()));
    }

    @Transactional
    @PostMapping("/custom/save")
    public ResponseEntity<?> saveCourses(@RequestBody Map<String, List<AllLectures>> request, Authentication auth) {
        List<AllLectures> addedCourses = request.get("added");
        List<AllLectures> deletedCourses = request.get("deleted");

        // 추가할 과목 처리
        List<CompletedCourse> coursesToAdd = addedCourses.stream()
                .map(dto -> {
                    CompletedCourse course = new CompletedCourse();
                    course.setYear("커스텀");
                    course.setCourseName(dto.getCourseName());
                    course.setCourseType(dto.getCourseType());
                    course.setMemberId(auth.getName());
                    course.setCustom(true);
                    course.setSemesterCompleted(dto.getSemesterCompleted());
                    course.setCredits(dto.getCredits());
                    return course;
                }).collect(Collectors.toList());

        completedCourseRepository.saveAll(coursesToAdd);

        // 삭제할 과목 처리
        for (AllLectures dto : deletedCourses) {
            completedCourseRepository.deleteByMemberIdAndCourseName(auth.getName(), dto.getCourseName());
        }

        return ResponseEntity.ok().build();
    }
}
