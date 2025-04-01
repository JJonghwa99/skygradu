package com.SkyGradU.SkyGradU.User.Courses;

import com.SkyGradU.SkyGradU.User.member.CustomUser;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExcelService {


    private final CompletedCourseRepository completedCourseRepository;
    private static final Logger log = LoggerFactory.getLogger(ExcelService.class);

    public String processExcelFile(MultipartFile file, Authentication auth) {
        if (auth == null || auth.getPrincipal() == null) {
            log.warn("로그인 정보가 없습니다.");
            return null;
        }

        CustomUser userDetails = (CustomUser) auth.getPrincipal();
        String loggedInStudentID = userDetails.studentID;

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            Row secondRow = sheet.getRow(1);
            String studentIdFromExcel = getCellValueAsString(secondRow.getCell(9));

            if (!loggedInStudentID.equals(studentIdFromExcel)) {
                log.warn("본인의 파일이 맞는지 확인해 주세요.");
                throw new FileUploadException("error1");
            }

            log.info("엑셀 파일 검증 완료. 데이터 처리 시작...");

            List<CompletedCourse> deleteList = completedCourseRepository.findByMemberId(loggedInStudentID);

            List<CompletedCourse> filteredList = deleteList.stream()
                    .filter(course -> !course.isCustom())
                    .collect(Collectors.toList());

            completedCourseRepository.deleteAll(filteredList);

            List<CompletedCourse> existingCourses = completedCourseRepository.findByMemberId((loggedInStudentID));
            Map<String, CompletedCourse> existingCourseMap = existingCourses.stream()
                    .collect(Collectors.toMap(CompletedCourse::getCourseName, course -> course, (existing, duplicate) -> existing));


            List<CompletedCourse> courseList = new ArrayList<>();
            int duplicateCount = 0; // 중복된 데이터 개수
            for (int i = 3; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                if (row == null || isRowEmpty(row)) {
                    log.info("비어있는 행을 건너뜁니다. (행 번호: {})", i);
                    continue;
                }

                String gradeStatus = getCellValueAsString(row.getCell(20)); // 등급 열

                if ("F".equalsIgnoreCase(gradeStatus) || "NP".equalsIgnoreCase(gradeStatus)) {
                    log.info("등급이 '{}'인 강좌를 건너뜁니다.", gradeStatus);
                    continue;
                }


                String currentCourseType = getCellValueAsString(row.getCell(6)); // 영역
                String courseName = getCellValueAsString(row.getCell(8)); // 강좌명
                if ("".equalsIgnoreCase(currentCourseType) || "".equalsIgnoreCase(courseName)) {
                    log.info("비어있는 행을 건너뜁니다.");
                    continue;
                }
                if ("이수구분".equalsIgnoreCase(currentCourseType) || "강좌명".equalsIgnoreCase(courseName)) {
                    log.info("필요없는 행을 건너뜁니다.");
                    continue;
                }

                int credits;
                String year;
                try {
                    credits = parseIntegerOrDefault(getCellValueAsString(row.getCell(18)), 0); // 학점 열
                    year = getCellValueAsString(row.getCell(23));   // 년도 열
                } catch (NumberFormatException e) {
                    log.warn("숫자 변환 실패: 강좌명={}, 학점={}, 년도={}", courseName, row.getCell(18), row.getCell(23));
                    continue; // 숫자 변환 실패 시 건너뜁니다.
                }

                String semesterCompleted = getCellValueAsString(row.getCell(25)); // 학기 열


                CompletedCourse course = new CompletedCourse();

                if (existingCourseMap.containsKey(courseName)) {
                    CompletedCourse existingCourse = existingCourseMap.get(courseName);

                    if (existingCourse.isCustom()) { //커스텀강좌가 중복
                        completedCourseRepository.delete(existingCourse);
                        log.info("커스텀 강좌'{}'의 수강내역이 발견되어 데이터를 업데이트 합니다.", courseName);
                        course.setMemberId(loggedInStudentID);
                        course.setCourseType(currentCourseType);
                        course.setCourseName(courseName);
                        course.setCredits(credits);
                        course.setYear(year);
                        course.setSemesterCompleted(semesterCompleted.isEmpty() ? "알 수 없음" : semesterCompleted); // 학기가 비어있으면 기본값 설정
                        course.setCustom(false);

                        courseList.add(course);

                    }

                    duplicateCount++;
                    log.info("중복된 강좌 '{}'을(를) 제외합니다.", courseName);
                    continue; // 중복된 데이터는 추가하지 않음
                }


                course.setMemberId(loggedInStudentID);
                course.setCourseType(currentCourseType);
                course.setCourseName(courseName);
                course.setCredits(credits);
                course.setYear(year);
                course.setSemesterCompleted(semesterCompleted.isEmpty() ? "알 수 없음" : semesterCompleted); // 학기가 비어있으면 기본값 설정
                course.setCustom(false);

                courseList.add(course);
            }
            completedCourseRepository.saveAll(courseList);


            log.info("{}개의 강좌를 성공적으로 저장했습니다. (중복 제거: {}개)", courseList.size(), duplicateCount);

            if (duplicateCount == 0) {
                return "";
            } else {
                return duplicateCount + "개의 강좌의 커스텀 여부를 업데이트 했어요😁";
            }

        } catch (IOException e) {
            throw new RuntimeException("엑셀 파일 처리 중 오류 발생", e);
        }
    }


    public String processExcelFile2(MultipartFile file) {

        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("파일명이 올바르지 않습니다.");
        }


        String loggedInStudentID = originalFilename.replaceFirst("[.][^.]+$", "");

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            Row secondRow = sheet.getRow(1);

            List<CompletedCourse> deleteList = completedCourseRepository.findByMemberId(loggedInStudentID);

            List<CompletedCourse> filteredList = deleteList.stream()
                    .filter(course -> !course.isCustom())
                    .collect(Collectors.toList());

            completedCourseRepository.deleteAll(filteredList);

            List<CompletedCourse> existingCourses = completedCourseRepository.findByMemberId((loggedInStudentID));
            Map<String, CompletedCourse> existingCourseMap = existingCourses.stream()
                    .collect(Collectors.toMap(CompletedCourse::getCourseName, course -> course, (existing, duplicate) -> existing));

            List<CompletedCourse> courseList = new ArrayList<>();
            int duplicateCount = 0; // 중복된 데이터 개수
            for (int i = 3; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                if (row == null || isRowEmpty(row)) {
                    log.info("비어있는 행을 건너뜁니다. (행 번호: {})", i);
                    continue;
                }

                String gradeStatus = getCellValueAsString(row.getCell(20)); // 등급 열

                if ("F".equalsIgnoreCase(gradeStatus) || "NP".equalsIgnoreCase(gradeStatus)) {
                    log.info("등급이 '{}'인 강좌를 건너뜁니다.", gradeStatus);
                    continue;
                }


                String currentCourseType = getCellValueAsString(row.getCell(6)); // 영역
                String courseName = getCellValueAsString(row.getCell(8)); // 강좌명
                if ("".equalsIgnoreCase(currentCourseType) || "".equalsIgnoreCase(courseName)) {
                    log.info("비어있는 행을 건너뜁니다.");
                    continue;
                }
                if ("이수구분".equalsIgnoreCase(currentCourseType) || "강좌명".equalsIgnoreCase(courseName)) {
                    log.info("필요없는 행을 건너뜁니다.");
                    continue;
                }

                int credits;
                String year;
                try {
                    credits = parseIntegerOrDefault(getCellValueAsString(row.getCell(18)), 0); // 학점 열
                    year = getCellValueAsString(row.getCell(23));   // 년도 열
                } catch (NumberFormatException e) {
                    log.warn("숫자 변환 실패: 강좌명={}, 학점={}, 년도={}", courseName, row.getCell(18), row.getCell(23));
                    continue; // 숫자 변환 실패 시 건너뜁니다.
                }

                String semesterCompleted = getCellValueAsString(row.getCell(25)); // 학기 열

                CompletedCourse course = new CompletedCourse();

                if (existingCourseMap.containsKey(courseName)) {
                    CompletedCourse existingCourse = existingCourseMap.get(courseName);

                    if (existingCourse.isCustom()) { //커스텀강좌가 중복
                        completedCourseRepository.delete(existingCourse);
                        log.info("커스텀 강좌'{}'의 수강내역이 발견되어 데이터를 업데이트 합니다.", courseName);
                        course.setMemberId(loggedInStudentID);
                        course.setCourseType(currentCourseType);
                        course.setCourseName(courseName);
                        course.setCredits(credits);
                        course.setYear(year);
                        course.setSemesterCompleted(semesterCompleted.isEmpty() ? "알 수 없음" : semesterCompleted); // 학기가 비어있으면 기본값 설정
                        course.setCustom(false);

                        courseList.add(course);

                    }

                    duplicateCount++;
                    log.info("중복된 강좌 '{}'을(를) 제외합니다.", courseName);
                    continue; // 중복된 데이터는 추가하지 않음
                }


                course.setMemberId(loggedInStudentID);
                course.setCourseType(currentCourseType);
                course.setCourseName(courseName);
                course.setCredits(credits);
                course.setYear(year);
                course.setSemesterCompleted(semesterCompleted.isEmpty() ? "알 수 없음" : semesterCompleted); // 학기가 비어있으면 기본값 설정
                course.setCustom(false);

                courseList.add(course);
            }

            completedCourseRepository.saveAll(courseList);


            log.info("{}개의 강좌를 성공적으로 저장했습니다. (중복 제거: {}개)", courseList.size(), duplicateCount);

            if (duplicateCount == 0) {
                return "";
            } else {
                return duplicateCount + "개의 강좌의 커스텀 여부를 업데이트 했어요😁";
            }

        } catch (IOException e) {
            throw new RuntimeException("엑셀 파일 처리 중 오류 발생", e);
        }
    }


    private boolean isRowEmpty(Row row) {
        if (row == null) return true; // 행 자체가 null이면 비어있음으로 간주
        for (int cellNum = 0; cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell != null && !getCellValueAsString(cell).isEmpty()) {
                return false; // 하나라도 값이 있으면 비어있지 않음
            }
        }
        return true; // 모든 셀이 비어있으면 true 반환
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        return switch(cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long)cell.getNumericCellValue()).trim();
            default -> "";
        };
    }

    private int parseIntegerOrDefault(String value, int defaultValue) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue; // 변환 실패 시 기본값 반환
        }
    }

}





