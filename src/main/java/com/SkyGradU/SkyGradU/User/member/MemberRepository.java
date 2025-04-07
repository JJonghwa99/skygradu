package com.SkyGradU.SkyGradU.User.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByStudentID(String studentID);
    Optional<Member> findByPortalID(String portalID);

    void deleteByStudentID(String studentID);

    long count(); //전체 가입자 수
}
