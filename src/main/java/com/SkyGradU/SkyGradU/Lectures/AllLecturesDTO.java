package com.SkyGradU.SkyGradU.Lectures;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AllLecturesDTO {
    private String lectureCode;
    private String courseName;
    private String courseType;
    private int credits;
    private String semesterCompleted;

    public AllLecturesDTO(AllLectures entity) {
        this.lectureCode = entity.getLectureCode();
        this.courseName = entity.getCourseName();
        this.courseType = entity.getCourseType();
        this.credits = entity.getCredits();
        this.semesterCompleted = entity.getSemesterCompleted();
    }
}
