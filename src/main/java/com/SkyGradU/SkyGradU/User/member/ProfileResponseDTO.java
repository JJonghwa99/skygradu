package com.SkyGradU.SkyGradU.User.member;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileResponseDTO {
    private boolean Auth;
    private String studentID;
    private String userName;
    private String major;
    private String portalID;
}
