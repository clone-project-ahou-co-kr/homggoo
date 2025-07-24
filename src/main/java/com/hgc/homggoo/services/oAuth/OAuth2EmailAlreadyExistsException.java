package com.hgc.homggoo.services.oAuth;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;


@Getter
public class OAuth2EmailAlreadyExistsException extends AuthenticationException {
    private final String existingProvider;
    private final String email;

    public OAuth2EmailAlreadyExistsException(String existingProvider, String email) {
        super("이미 " + existingProvider + "로 가입된 이메일입니다: " + email);
        this.existingProvider = existingProvider;
        this.email = email;
    }

}
