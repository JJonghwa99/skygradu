package com.SkyGradU.SkyGradU.User.member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String studentID) throws UsernameNotFoundException{
        var result = memberRepository.findByStudentID(studentID);
        if(result.isEmpty()){
            throw new UsernameNotFoundException("그런사람 또 없습니다.");
        }
        var user = result.get();
        List<GrantedAuthority> level =new ArrayList<>();

        /*if (user.getStudentID()===20201056){*/
            level.add(new SimpleGrantedAuthority("ROLE_USER")); //메모에 따라 어디에 들어갈 수 있음
      /*  }else{
            level.add(new SimpleGrantedAuthority("ROLE_ADMIN")); //메모에 따라 어디에 들어갈 수 있음

        }*/
        return new User(user.getStudentID(),user.getPassword1(),level);
    }
}