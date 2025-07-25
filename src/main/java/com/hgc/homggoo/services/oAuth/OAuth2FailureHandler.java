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

        // ✅ 예외 본체 검사로 변경
        if (exception instanceof OAuth2EmailAlreadyExistsException emailEx) {
            message = "이미 " + emailEx.getExistingProvider() + "로 가입된 이메일입니다.";
        } else if (exception.getMessage() != null && exception.getMessage().contains("rate limit exceeded")) {
            message = "요청이 너무 많습니다. 잠시 후 다시 시도해주세요.";
        } else if (exception.getMessage() != null) {
            message = "로그인 실패: " + exception.getMessage();
        }

        request.getSession().setAttribute("OAUTH2_ERROR_MESSAGE", message);
        response.sendRedirect("/user/login");
    }

}
