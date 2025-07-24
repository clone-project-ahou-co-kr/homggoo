package com.hgc.homggoo.services.oAuth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class OAuth2FailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        String message = "로그인 실패";

        Throwable cause = exception.getCause();
        if (cause instanceof OAuth2EmailAlreadyExistsException emailEx) {
            message = "이미 " + emailEx.getExistingProvider() + "로 가입된 이메일입니다.";
        } else if (exception.getMessage().contains("rate limit exceeded")) {
            message = "잠시 후 다시 시도해주세요 (로그인 요청이 너무 많습니다)";
        }

        // ✅ URL에 에러를 붙이지 않고, 세션에만 저장!
        request.getSession().setAttribute("OAUTH2_ERROR_MESSAGE", message);

        // ✅ 순수히 login 페이지로만 리다이렉트 (쿼리 없음!)
        response.sendRedirect("/user/login");
    }
}
