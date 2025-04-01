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

    private String memberId;  // 학번 supabase에서 직접 연동했음// 설정함 배포할때 다시 설정하던가 해야할듯
    private String courseName;  // 강좌명
    private String courseType;  // 이수구분
    private int credits;  // 학점
    private boolean isCustom = false;  // 커스텀 기능
    private String semesterCompleted;  // 학기
    private String year;  // 학년보단 이수년도가 나을듯?
}
