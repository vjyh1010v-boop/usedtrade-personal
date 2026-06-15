package com.pknu26.usedtrade.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.pknu26.usedtrade.service.UserService;
import com.pknu26.usedtrade.validation.UserJoinForm;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/join")
    public String joinForm(Model model) {
        model.addAttribute("userJoinForm", new UserJoinForm());
        // 회원가입 페이지에서 헤더 오른쪽에 '홈으로' 버튼만 보이게 하기 위한 값_SY
        model.addAttribute("authPage", true);
        return "users/join";
    }

    @PostMapping("/join")
    public String join(@Valid @ModelAttribute("userJoinForm") UserJoinForm form,
                        BindingResult bindingResult) {

        // 입력 검증 실패 시 회원가입 페이지로 다시 이동
        if (bindingResult.hasErrors()) {
            return "users/join";
        }

        try {
            userService.join(form);
        } catch (IllegalArgumentException e) {
            String errorMessage = e.getMessage() != null
                    ? e.getMessage()
                    : "회원가입 처리 중 오류가 발생했습니다.";
            bindingResult.reject("error", null, errorMessage);
            return "users/join";  // 에러메시지가 html에 출력
        }

        // 회원가입 후 로그인 페이지로 이동 (redirect)
        return "redirect:/login";
    }

}