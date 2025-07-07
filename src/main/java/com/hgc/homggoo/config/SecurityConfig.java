package com.hgc.homggoo.config;

import com.hgc.homggoo.services.oAuth.CustomOAuth2UserService;
import com.hgc.homggoo.services.oAuth.OAuth2LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration  // ✅ 이 클래스는 Spring의 설정 클래스로 사용됨 (Spring Security 설정을 담당)
public class SecurityConfig {

    @Autowired
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    // ✅ 로그인 성공 시 세션에 사용자 정보를 저장할 커스텀 핸들러
    // @Component로 등록되어야 자동 주입 가능함

    /**
     * ✅ SecurityFilterChain을 Spring Bean으로 등록
     * Spring Security에서 Http 보안 설정을 정의함
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // ✅ CSRF 보호 비활성화
                // - REST API나 외부 인증(OAuth 등)을 사용하는 경우 비활성화하는 경우가 많음
                // - 운영 환경에서는 토큰 방식(JWT 등)을 쓰거나, 프론트와 CSRF 토큰을 연동하는 게 안전함
                .csrf(csrf -> csrf.disable())

                // ✅ OAuth2 로그인 설정
                .oauth2Login(oauth -> oauth
                        // 사용자가 인증되지 않은 상태로 보호된 리소스에 접근할 경우 보여줄 커스텀 로그인 페이지 경로
                        .loginPage("/user/login")

                        // 로그인 성공 후 무조건 "/"(홈)으로 리다이렉트
                        // true → 사용자가 원래 접근하려던 URL이 있어도 무시하고 여기로 감
                        .defaultSuccessUrl("/", true)

                        // 사용자 정보 불러오는 엔드포인트 설정
                        .userInfoEndpoint(userInfo -> userInfo
                                // 사용자 정보를 처리할 커스텀 서비스 등록 (OAuth2UserService 구현체)
                                .userService(oAuth2UserService())
                        )

                        // ✅ 로그인 성공 후 실행할 커스텀 핸들러 등록
                        // 이 핸들러에서 세션에 사용자 정보(signedUser)를 저장함
                        .successHandler(oAuth2LoginSuccessHandler)
                )

                // ✅ 로그아웃 설정
                .logout(logout -> logout
                        // 로그아웃을 수행할 URL (GET 또는 POST 요청 허용)
                        .logoutUrl("/logout")

                        // 로그아웃 성공 후 이동할 페이지
                        .logoutSuccessUrl("/")

                        // 세션 무효화 → 서버에 저장된 세션 제거
                        .invalidateHttpSession(true)

                        // 브라우저의 JSESSIONID 쿠키도 함께 제거 (완전한 로그아웃 처리)
                        .deleteCookies("JSESSIONID")
                );

        // 최종적으로 보안 필터 체인을 반환 (이 설정이 Spring Security에 적용됨)
        return http.build();
    }

    /**
     * ✅ OAuth2 로그인 시 사용자 정보를 처리하는 커스텀 서비스 Bean 등록
     * CustomOAuth2UserService는 OAuth2UserService를 확장한 클래스임
     */
    @Bean
    public CustomOAuth2UserService oAuth2UserService() {
        return new CustomOAuth2UserService();
    }
}
