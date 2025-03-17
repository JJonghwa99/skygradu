package com.SkyGradU.SkyGradU.User.member;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class forgetPWService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public forgetPWService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Map<String, Object> checkPortal(String userId, String password) {
        Map<String, Object> result = new HashMap<>();
        try {
            Connection.Response loginResponse = Jsoup.connect("https://www.sungkyul.ac.kr/portalLogin/skukr/loginProcess.do")
                    .data("userId", userId)
                    .data("password", password)
                    .method(Connection.Method.POST)
                    .execute();

            boolean success = loginResponse.body().contains("\"result\":\"SUCCESS\"");

            if (success) {
                Optional<Member> member = memberRepository.findByPortalID(userId);
                if (member.isPresent()) {
                    result.put("status", "exists");
                } else {
                    result.put("status", "no_member");
                }
            } else {
                result.put("status", "invalid_credentials");
            }
        } catch (Exception e) {
            result.put("status", "error");
        }
        return result;
    }
    public Map<String, Object> changePassword(String userId, String newPassword) {
        Map<String, Object> result = new HashMap<>();

        // 멤버 조회
        Optional<Member> memberOptional = memberRepository.findByPortalID(userId);
        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            member.setPassword1(passwordEncoder.encode(newPassword)); // 비밀번호 암호화 후 저장
            memberRepository.save(member);

            result.put("status", "success");
            result.put("message", "비밀번호가 성공적으로 변경되었습니다.");
        } else {
            result.put("status", "no_member");
            result.put("message", "사용자를 찾을 수 없습니다.");
        }

        return result;
    }


}

