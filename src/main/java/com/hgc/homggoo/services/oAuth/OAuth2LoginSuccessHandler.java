package com.hgc.homggoo.services.oAuth;

import com.hgc.homggoo.entities.user.UserEntity;
import com.hgc.homggoo.mappers.user.UserMapper;
import com.hgc.homggoo.services.oAuth.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * ✅ OAuth2 로그인 성공 후 실행되는 핸들러 클래스
 * Spring Security에서 OAuth2 인증에 성공했을 때,
 * 세션에 사용자 정보를 저장하거나 커스텀 후처리를 할 수 있도록 도와준다.
 */
@Component  // Spring Bean으로 등록 → SecurityConfig에서 자동 주입 가능
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserMapper userMapper;  // ✅ DB에서 사용자 정보를 조회하기 위한 MyBatis 매퍼

    /**
     * ✅ 로그인 성공 시 자동으로 호출되는 메서드
     *
     * @param request       클라이언트의 요청 정보 (HttpServletRequest)
     * @param response      응답 객체 (HttpServletResponse)
     * @param authentication 인증된 사용자 정보가 담긴 Authentication 객체
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // ✅ 인증된 사용자 정보를 가져온다. 우리가 만든 CustomOAuth2User 타입으로 다운캐스팅
        // getPrincipal()은 인증된 사용자의 정보를 반환합니다.
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        // ✅ OAuth2로 로그인한 사용자의 이메일을 가져온다
        String email = oAuth2User.getEmail();

        // ✅ 이메일을 기반으로 실제 DB에 저장된 사용자 정보를 조회한다
        // 왜 다시 조회하냐면, OAuth2User에는 최소한의 정보(이름, 이메일 등)만 있기 때문
        UserEntity dbUser = userMapper.selectByEmail(email);

        // ✅ 사용자 정보가 존재한다면, 세션에 저장한다
        if (dbUser != null) {
            HttpSession session = request.getSession();  // 세션 객체 획득 (없으면 새로 생성)
            session.setAttribute("signedUser", dbUser);  // ✅ 세션에 사용자 정보 저장
            // 이 정보를 통해 이후 컨트롤러나 JSP/Thymeleaf 등에서 로그인한 사용자 정보를 활용할 수 있음
        }

        // ✅ 로그인 성공 후 "/" 경로로 리디렉션
        // 원하는 경우 마이페이지나 대시보드 경로로 바꿔도 됨 (예: "/dashboard")
        response.sendRedirect("/");
    }
}
