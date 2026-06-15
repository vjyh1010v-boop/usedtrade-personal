package com.pknu26.usedtrade.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pknu26.usedtrade.dto.UserDTO;
import com.pknu26.usedtrade.mapper.UserMapper;
import com.pknu26.usedtrade.validation.UserJoinForm;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public void join(UserJoinForm form) {

        // 비밀번호 확인
        if (!form.getPassword().equals(form.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 아이디 중복 체크
        UserDTO exist = this.userMapper.findByLoginId(form.getLoginId());
        if (exist != null) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        // UserJoinForm -> UserDTO 변환
        UserDTO user = new UserDTO();
        user.setLoginId(form.getLoginId());
        user.setUserName(form.getUserName());
        user.setPassword(form.getPassword());
        user.setNickname(form.getNickname());
        user.setPhone(form.getPhone());

        // 기본 권한 설정
        user.setRole("USER");

        user.setPassword(passwordEncoder.encode(form.getPassword()));

        userMapper.insertUser(user);
    }

}