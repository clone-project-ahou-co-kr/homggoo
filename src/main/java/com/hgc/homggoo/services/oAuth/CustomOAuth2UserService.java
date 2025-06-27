package com.hgc.homggoo.services.oAuth;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        //이 메서드는 사용자가 OAuth2 로그인을 할 때 호출되어, 사용자 정보를 불러오고 가공하여 반환합니다.
        OAuth2User oAuth2User = super.loadUser(userRequest);
        //oAuth2User는 네이버 API 응답 전체(JSON)를 포함하고 있습니다.
        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // "naver"
        Map<String, Object> attributes = oAuth2User.getAttributes();
        System.out.println(attributes);
        // 네이버의 경우 response 내부에 유저 정보가 있음
        if ("naver".equals(registrationId)) {
            attributes = (Map<String, Object>) attributes.get("response");
        }

        return new CustomOAuth2User(attributes);
    }
}
