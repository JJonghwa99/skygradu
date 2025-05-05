package com.SkyGradU.SkyGradU.User.member;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String studentID;

    private String userName;
    private String major;
    private String userEmail;
    private String password1;
    private String portalID;
    private String elective;
    private String minor;
    private String enrollYear;
    private String minorYear;
    private boolean update = true;

    public void updatePassword(String newPassword){
        this.password1 = newPassword;
    }
}
