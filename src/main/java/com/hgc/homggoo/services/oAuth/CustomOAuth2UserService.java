package com.hgc.homggoo.services.oAuth;

import com.hgc.homggoo.entities.user.UserEntity;
import com.hgc.homggoo.mappers.user.UserMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private HttpSession session;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String providerType = userRequest.getClientRegistration().getRegistrationId().toUpperCase();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String providerKey = null;
        String email = null;
        String nickname = null;
        byte[] profile = new byte[0];

        // ✅ Naver
        if ("NAVER".equals(providerType)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            providerKey = (String) response.get("id");
            email = (String) response.get("email");
            nickname = (String) response.get("nickname");
            String profileImageUrl = (String) response.get("profile_image");
            profile = downloadImageAsBytes(profileImageUrl);
        }

        // ✅ 필수 정보 보정
        if (email == null || email.isBlank()) {
            email = providerType.toLowerCase() + "_" + providerKey + "@oauth.com";
        }
        if (nickname == null || nickname.isBlank()) {
            nickname = "user_" + System.currentTimeMillis();
        }

        // ✅ 사용자 등록 여부 확인
        UserEntity dbUser = this.userMapper.selectByEmail(email);
        if (dbUser == null) {
            UserEntity newUser = new UserEntity();
            newUser.setEmail(email);
            newUser.setNickname(nickname);
            newUser.setPassword(providerType); // 의미 없음
            newUser.setProviderType(providerType);
            newUser.setProviderKey(providerKey);
            newUser.setCreatedAt(LocalDateTime.now());
            newUser.setModifiedAt(LocalDateTime.now());
            newUser.setAdmin(false);
            newUser.setDeleted(false);
            newUser.setProfile(profile);

            this.userMapper.insert(newUser);
            dbUser = newUser; // 방금 등록한 유저를 세션에 저장
        }

        // ✅ 세션 저장
        session.setAttribute("signedUser", dbUser);
        return new CustomOAuth2User(attributes);
    }

    // ✅ 이미지 URL을 byte[]로 다운로드
    private byte[] downloadImageAsBytes(String imageUrl) {
        try (InputStream in = new URL(imageUrl).openStream()) {
            return in.readAllBytes();
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}
