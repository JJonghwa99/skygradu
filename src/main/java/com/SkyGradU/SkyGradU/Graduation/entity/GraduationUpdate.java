// src/main/java/com/SkyGradU/SkyGradU/graduation/entity/GraduationUpdate.java
package com.SkyGradU.SkyGradU.Graduation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "graduation_update")
@Getter
@Setter
@NoArgsConstructor
public class GraduationUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false, unique = true)
    private String studentId;

    @Column(name = "major_required", nullable = false)
    private int majorRequired;

    @Column(name = "major_elective", nullable = false)
    private int majorElective;

    @Column(name = "general_required", nullable = false)
    private int generalRequired;

    @Column(name = "general_elective", nullable = false)
    private int generalElective;

    @Column(name = "chapel_attendance_count", nullable = false)
    private int chapelAttendanceCount;
}
