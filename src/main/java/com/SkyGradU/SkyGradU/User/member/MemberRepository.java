package com.SkyGradU.SkyGradU.User.member;

import com.SkyGradU.SkyGradU.User.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByStudentID(String studentID);
    Optional<Member> findByUserName(String username);
   /* Optional<Member> findByUserEmail(String userEmail);
    Optional<Member> findBymajor(String usermajor);*/

    void deleteByStudentID(String studentID);
}
