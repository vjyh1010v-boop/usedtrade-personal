package com.pknu26.usedtrade.validation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserJoinForm {

    @NotBlank(message = "아이디는 필수입니다.")
    @Size(min = 4, max = 50, message = "아이디는 4~50자 사이여야 합니다.")
    private String loginId;

    @NotBlank(message = "이름은 필수입니다.")
    private String userName;

    @NotBlank(message = "패스워드는 필수입니다.")
    @Size(min = 6, max = 20, message = "패스워드는 6~20자 사이여야 합니다.")
    private String password;

    @NotBlank(message = "패스워드 확인은 필수입니다.")
    private String passwordConfirm;

    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;

    @NotBlank(message = "전화번호는 필수입니다.")
    private String phone;
}
