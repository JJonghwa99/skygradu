package com.SkyGradU.SkyGradU.User.Courses;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/excel")
@RequiredArgsConstructor
public class CourseController {

    private final ExcelService excelService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadExcel(@RequestParam MultipartFile file, Authentication auth) {
        try {
            String resultMessage = excelService.processExcelFile(file, auth);
            return ResponseEntity.ok(Map.of("message", resultMessage));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "업로드 실패", "error", e.getMessage()));
        }
    }

}
