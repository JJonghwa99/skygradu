package com.SkyGradU.SkyGradU.User.Courses;

import com.SkyGradU.SkyGradU.Exception.FileUploadException;
import com.SkyGradU.SkyGradU.User.member.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/excel")
@RequiredArgsConstructor
public class CourseController {

    private final ExcelService excelService;
    private final AutoExcelService autoExcelService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadExcel(@RequestParam MultipartFile file, Authentication auth) {
        try {
            String resultMessage = excelService.processExcelFile(file, auth);
            return ResponseEntity.ok(Map.of("message", resultMessage));
        } catch (FileUploadException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "error1");
            return ResponseEntity.status(500).body(errorResponse);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "업로드 실패", "error", e.getMessage()));
        }
    }
    @PostMapping("/auto")
    @ResponseBody
    public Map<String, Object> autoExcelUpdate(@RequestParam String userId, @RequestParam String password) {
        return autoExcelService.autoExcelUpdate(userId, password);
    }
}
