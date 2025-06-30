package com.hgc.homggoo.services.oAuth;

import com.hgc.homggoo.entities.user.UserEntity;
import com.hgc.homggoo.mappers.user.UserMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDateTime;
import java.util.Map;

public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private HttpSession session;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // "naver", "kakao"
        String providerType = registrationId.toUpperCase();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String providerKey = null;
        String email = null;
        String nickname = null;
        byte[] profile = new byte[]{0};

        if ("NAVER".equals(providerType)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            providerKey = (String) response.get("id");
            email = (String) response.get("email");
            nickname = (String) response.get("nickname");
        }
//        else if ("KAKAO".equals(providerType)) {
//            providerKey = String.valueOf(attributes.get("id"));
//            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
//            if (kakaoAccount != null) {
//                email = (String) kakaoAccount.get("email");
//                Map<String, Object> profileMap = (Map<String, Object>) kakaoAccount.get("profile");
//                if (profileMap != null) {
//                    nickname = (String) profileMap.get("nickname");
//                }
//            }
//        }

        // ✅ 필수 정보 보정
        if (email == null || email.isBlank()) {
            email = providerType.toLowerCase() + "_" + providerKey + "@oauth.com";
        }
        if (nickname == null || nickname.isBlank()) {
            nickname = "user_" + System.currentTimeMillis();
        }

        // ✅ 이메일 기준으로 사용자 존재 여부 확인
        UserEntity dbUser = this.userMapper.selectByEmail(email);
        if (dbUser == null) {
            UserEntity newUser = new UserEntity();
            newUser.setEmail(email);
            newUser.setNickname(nickname);
            newUser.setPassword(providerType); // OAuth 비밀번호는 의미 없음
            newUser.setProviderType(providerType);
            newUser.setProviderKey(providerKey);
            newUser.setCreatedAt(LocalDateTime.now());
            newUser.setModifiedAt(LocalDateTime.now());
            newUser.setAdmin(false);
            newUser.setDeleted(false);
            newUser.setProfile(profile);
            this.userMapper.insert(newUser);
        }
        session.setAttribute("signedUser", dbUser);

        return new CustomOAuth2User(attributes);
    }
}
