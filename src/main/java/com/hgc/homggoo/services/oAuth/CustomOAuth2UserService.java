package com.hgc.homggoo.services.oAuth;

import com.hgc.homggoo.entities.user.UserEntity;
import com.hgc.homggoo.mappers.user.UserMapper;
import com.hgc.homggoo.utils.Bcrypt;
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
        String accessToken = userRequest.getAccessToken().getTokenValue();
        String providerType = userRequest.getClientRegistration().getRegistrationId().toUpperCase();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        System.out.println(attributes);
        System.out.println(accessToken);
        System.out.println(providerType);
        String providerKey = null;
        String email = null;
        String nickname = null;
        byte[] profile = new byte[0];
        String imageUrl = null;

        // ✅ Naver
        if ("NAVER".equals(providerType)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            providerKey = (String) response.get("id");
            email = (String) response.get("email");
            nickname = (String) response.get("nickname");
            String profileImageUrl = (String) response.get("profile_image");
            profile = downloadImageAsBytes(profileImageUrl);
            imageUrl = profileImageUrl;
        } else if ("KAKAO".equals(providerType)) {
            providerKey = String.valueOf(attributes.get("id"));

            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            if (kakaoAccount != null) {
                email = (String) kakaoAccount.get("email");

                Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");
                if (kakaoProfile != null) {
                    nickname = (String) kakaoProfile.get("nickname");
                    String profileImageUrl = (String) kakaoProfile.get("profile_image_url");
                    if (profileImageUrl != null) {
                        profile = downloadImageAsBytes(profileImageUrl);
                        imageUrl = profileImageUrl;
                    }
                }
            }
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
        if (dbUser != null && dbUser.isDeleted()) {
            // 탈퇴 계정 복구
            dbUser.setDeleted(false);
            dbUser.setModifiedAt(LocalDateTime.now());
            dbUser.setProviderKey(providerKey);
            dbUser.setProviderType(providerType);
            dbUser.setProfile(profile);
            dbUser.setImageUrl(imageUrl);
            this.userMapper.update(dbUser); // ✅ 업데이트로 복구
        } else if (dbUser == null) {
            // 신규 가입
            UserEntity newUser = new UserEntity();
            newUser.setEmail(email);
            newUser.setNickname(nickname);
            newUser.setPassword(Bcrypt.encrypt(providerKey));
            newUser.setProviderType(providerType);
            newUser.setProviderKey(providerKey);
            newUser.setCreatedAt(LocalDateTime.now());
            newUser.setModifiedAt(LocalDateTime.now());
            newUser.setAdmin(false);
            newUser.setDeleted(false);
            newUser.setProfile(profile);
            newUser.setImageUrl(imageUrl);
            this.userMapper.insert(newUser);
            dbUser = newUser;
        }
        UserEntity existingUser = this.userMapper.selectByEmail(email);
        if (existingUser != null && !existingUser.getProviderType().equals(providerType)) {
            // 이미 다른 플랫폼으로 가입된 이메일
            throw new OAuth2EmailAlreadyExistsException(existingUser.getProviderType(), email);
        }

        // ✅ 세션 저장
        // ✅ 세션 저장
        session.setAttribute("signedUser", dbUser);

// ✅ OAuth2User 반환 (providerType 포함)
        return new CustomOAuth2User(attributes, providerType,accessToken);

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
