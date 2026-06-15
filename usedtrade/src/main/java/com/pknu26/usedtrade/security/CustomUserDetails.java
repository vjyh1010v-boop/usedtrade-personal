package com.pknu26.usedtrade.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.pknu26.usedtrade.dto.UserDTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails{

    private final UserDTO user;

    // 추가: 로그인한 사용자의 user_id
    public Long getUserId() {
        return user.getUserId();
    }

    // 화면에서 사용할 닉네임
    public String getNickname() {
        return user.getNickname();
    }

    // 로그인 ID
    @Override
    public String getUsername() {
        return user.getLoginId();
    }

    // 비밀번호
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // 권한
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole()));
    }

}