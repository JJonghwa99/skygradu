package com.SkyGradU.SkyGradU.User.Courses;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/excel")
@RequiredArgsConstructor
public class CourseController {

    private final ExcelService excelService;

    @PostMapping("/upload")
    public String uploadExcel(@RequestParam MultipartFile file, Authentication auth) {
        excelService.processExcelFile(file, auth);
        return "redirect:/mypage";
    }
}

