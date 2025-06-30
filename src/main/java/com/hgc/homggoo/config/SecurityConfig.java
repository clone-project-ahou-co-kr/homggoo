package com.hgc.homggoo.config;

import com.hgc.homggoo.services.oAuth.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // SecurityFilterChain을 Bean으로 등록
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // ✅ CSRF 보호 비활성화 (개발/테스트용)
                //   - 기본적으로 POST 요청에 CSRF 토큰이 필요하지만, API 또는 OAuth2 로그인 중심일 경우 비활성화하는 경우 많음
                .csrf(csrf -> csrf.disable())

                // ✅ OAuth2 로그인 설정
                .oauth2Login(oauth -> oauth
                        // 사용자가 인증되지 않은 상태로 보호된 페이지에 접근 시 보여줄 커스텀 로그인 페이지 경로
                        .loginPage("/user/login")

                        // 로그인 성공 시 무조건 "/"(루트 페이지)로 이동
                        // true: 이전 요청 여부와 상관없이 항상 이 경로로 리다이렉트
                        .defaultSuccessUrl("/", true)

                        // 사용자 정보 엔드포인트 설정
                        .userInfoEndpoint(userInfo -> userInfo
                                // 로그인 이후 사용자 정보를 가져오는 커스텀 서비스 지정
                                .userService(oAuth2UserService())
                        )
                )

                // ✅ 로그아웃 설정
                .logout(logout -> logout
                        // 로그아웃을 수행할 URL 지정 (GET/POST 요청 가능)
                        .logoutUrl("/logout")

                        // 로그아웃 성공 후 리다이렉트할 경로 (여기선 홈)
                        .logoutSuccessUrl("/")

                        // 세션 무효화: 서버에 저장된 사용자 세션 제거
                        .invalidateHttpSession(true)

                        // JSESSIONID 쿠키 제거: 브라우저의 인증 쿠키 제거
                        .deleteCookies("JSESSIONID")
                );

        // 최종 필터 체인을 반환하여 보안 설정 적용
        return http.build();
    }

    // ✅ OAuth2 로그인 시 사용자 정보를 처리하는 커스텀 서비스 Bean 등록
    @Bean
    public CustomOAuth2UserService oAuth2UserService() {
        return new CustomOAuth2UserService();
    }
}
