package com.hgc.homggoo.controllers;

import com.hgc.homggoo.entities.user.UserEntity;
import com.hgc.homggoo.services.oAuth.CustomOAuth2User;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.Arrays;

@Controller
@RequestMapping(value = "/")
public class HomeController {
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getIndex(@SessionAttribute(value = "signedUser", required = false) UserEntity signedUser,
                           @AuthenticationPrincipal CustomOAuth2User image,
                           Model model) {
        if (image != null) {
            model.addAttribute("image", image.getProfileImage());
        }
        model.addAttribute("signedUser", signedUser);
        return "/home/index";
    }
}
