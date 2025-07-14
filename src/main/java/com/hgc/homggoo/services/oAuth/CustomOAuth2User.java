package com.hgc.homggoo.services.oAuth;

import com.hgc.homggoo.entities.article.ArticleEntity;
import com.hgc.homggoo.mappers.user.UserMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {
    private final Map<String, Object> attributes;


    public CustomOAuth2User(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getName() {
        Object name = attributes.get("name");
        if (name == null || name.toString().isBlank()) {
            name = attributes.get("nickname");
        }
        if (name == null || name.toString().isBlank()) {
            name = attributes.get("email");
        }
        if (name == null || name.toString().isBlank()) {
            name = attributes.get("id");
        }
        return name != null ? name.toString() : java.util.UUID.randomUUID().toString();
    }

    public String getEmail() {
        Object valueObj = attributes.get("email");
        return valueObj != null ? valueObj.toString() : null;
    }

    public String getGender() {
        Object gender = attributes.get("gender");
        return gender != null ? gender.toString() : null;
    }

    public String getBirthday() {
        return (String) attributes.get("birthday");
    }

    public String getBirthyear() {
        return (String) attributes.get("birthyear");
    }

    public String getProfileImage() {
        Object image = attributes.get("profile_image");

        return image != null ? image.toString() : null;
    }
}
