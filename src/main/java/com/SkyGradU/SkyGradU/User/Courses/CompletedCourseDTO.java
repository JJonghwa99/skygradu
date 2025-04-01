package com.SkyGradU.SkyGradU.User.Courses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CompletedCourseDTO {
    private String year;
    private String semesterCompleted;
    private String courseName;
    private String courseType;
    private int credits;
    private boolean isCustom;
}
