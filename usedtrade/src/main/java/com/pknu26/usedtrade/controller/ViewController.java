package com.pknu26.usedtrade.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // @Controller는 글자나 데이터를 반환하는게 아니라, "HTML 파일 이름"을 반환.
public class ViewController {

    /**
     * 1. 메인 홈 화면 (index.html) 리턴
     */
    @GetMapping("/")
    public String showIndexPage() {
        // "index"라고 리턴하면, 스프링이 templates 폴더에서 "index.html"을 찾아 화면에 띄워줌.
        return "index";
    }

    /**
     * 2. 로그인 화면 (login.html) 리턴
     */
    @GetMapping("/users/login")
    public String showLoginPage(Model model) {
        // 로그인 페이지에서 헤더 오른쪽에 '홈으로' 버튼만 보이게 하기 위한 값_SY
        model.addAttribute("authPage", true);
        return "users/login";
    }

    // UserController 기능 충돌로 인해 주석 처리
    /**
     * 3. 회원가입 화면 (join.html) 리턴
     */
    // @GetMapping("/join")
    // public String showJoinPage() {
    // return "join";
    // }

    // 게시글 상세화면
    @GetMapping("/posts/{postId}")
    public String showPostDetailPage() {
        return "post/detail";
    }

    @GetMapping("/my/posts")
    public String myPostsPage() {
        return "index";
    }

    @GetMapping("/wishlist")
    public String wishlistPage(){
        return "index";
    }
}