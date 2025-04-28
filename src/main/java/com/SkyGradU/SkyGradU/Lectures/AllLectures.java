package com.SkyGradU.SkyGradU.Lectures;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AllLectures {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lectureId;

    private String lectureCode;
    private String courseName;
    private String courseType;
    private int credits;
    private String semesterCompleted;
    private int count;

}
