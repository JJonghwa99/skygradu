package com.SkyGradU.SkyGradU.User.member;

import com.SkyGradU.SkyGradU.Graduation.repository.GraduationProgressRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private GraduationProgressRepository graduationProgressRepository;

    public boolean verifyPassword(String studentID, String currentPassword) {
        Member member = memberRepository.findByStudentID(studentID)
                .orElseThrow();
        return passwordEncoder.matches(currentPassword, member.getPassword1());
    }

    public boolean changePassword(String studentID, String currentPassword, String newPassword) {
        if (verifyPassword(studentID, currentPassword)) {
            Member member = memberRepository.findByStudentID(studentID)
                    .orElseThrow();
            member.updatePassword(passwordEncoder.encode(newPassword));
            memberRepository.save(member);
            return true;
        }
        return false;
    }

    @Transactional
    public void deleteMemberByStudentID(String studentID) {
        graduationProgressRepository.deleteByStudentId(studentID);
        memberRepository.deleteByStudentID(studentID);
    }
}