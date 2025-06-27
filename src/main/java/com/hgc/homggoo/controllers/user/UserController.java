package com.hgc.homggoo.controllers.user;

import com.hgc.homggoo.services.oAuth.CustomOAuth2User;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/user")
public class UserController {

    @RequestMapping(value = "/register", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getRegister(@AuthenticationPrincipal CustomOAuth2User signedUser, HttpSession session) {
        if (signedUser != null) {
            // 세션 저장도 가능
            return "redirect:/";
        }
        return "user/register";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getLogin(@AuthenticationPrincipal CustomOAuth2User user) {
        // 이미 로그인된 경우 홈으로
        if (user != null) {
            System.out.println(user.getEmail());
            return "redirect:/";
        }
        return "user/login"; // 로그인 폼
    }
}
