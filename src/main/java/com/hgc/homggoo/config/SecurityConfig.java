package com.hgc.homggoo.config;

import com.hgc.homggoo.services.oAuth.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                .oauth2Login(oauth -> oauth
                        .loginPage("/user/login") // 커스텀 로그인 페이지
                        .defaultSuccessUrl("/", true) // 로그인 성공 시 루트로 이동
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService())
                        )
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")// 로그아웃 후 루트로 이동
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );
        return http.build();
    }

    @Bean
    public CustomOAuth2UserService oAuth2UserService() {
        return new CustomOAuth2UserService();
    }
}
