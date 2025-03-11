package com.SkyGradU.SkyGradU.User.Courses;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CompletedCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;

    private String memberId;  // 학번 - 아직 username이랑 연동되도록 안함
    private String courseName;  // 강좌명
    private String courseType;  // 이수구분
    private int credits;  // 학점
    private boolean isCustom = false;  // 커스텀 기능
    private String semesterCompleted;  // 학기
    private int year;  // 학년보단 이수년도가 나을듯?
}
