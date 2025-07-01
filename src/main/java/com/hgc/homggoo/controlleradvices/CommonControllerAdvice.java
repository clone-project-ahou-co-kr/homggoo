package com.hgc.homggoo.controlleradvices;

import com.hgc.homggoo.entities.user.UserEntity;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CommonControllerAdvice {

    @ModelAttribute("signedUser")
    public UserEntity loginUser(HttpSession session) {
        return (UserEntity) session.getAttribute("signedUser");
    }
}
