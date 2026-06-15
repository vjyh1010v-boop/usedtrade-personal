package com.pknu26.usedtrade.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    // Spring Security 커스터마이징 공간이라고 이해하면 됨.

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // 특정 경로에 대한 인가 작업
                .csrf(csrf -> csrf.disable()) // 개발 단계에서는 끔 (운영 시 고려)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/products",
                                "/posts/**",
                                "/community",
                                "/community/**",
                                "/users/join",
                                "/login",
                                "/uploads/**",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/posts", "/api/posts/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/community/posts", "/api/community/posts/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/posts/*/favorite").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/posts").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/posts/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/posts/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/posts/**").authenticated()

                        .requestMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated())

                .formLogin(form -> form
                        .loginPage("/users/login") // 우리가 만든 로그인 페이지(인증이 필요한 페이지에 비로그인 상태로 접근하면 여기로 리다이렉트)
                        .loginProcessingUrl("/users/login") // 로그인 POST 처리 URL (login.html 폼의 action과 일치해야 함)
                        .defaultSuccessUrl("/?login", true)
                        .failureUrl("/users/login?error=true")
                        .permitAll())

                .sessionManagement(session -> session
                        .maximumSessions(1) // 중복 로그인 방지
                        .expiredUrl("/users/login") // 세션 만료 시 이동 페이지
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/?logout") // 기존: "/"
                        .permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}