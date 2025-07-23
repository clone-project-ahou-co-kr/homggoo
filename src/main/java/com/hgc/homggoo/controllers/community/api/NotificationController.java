package com.hgc.homggoo.controllers.community.api;

import com.hgc.homggoo.entities.notification.NotificationEntity;
import com.hgc.homggoo.entities.user.UserEntity;
import com.hgc.homggoo.results.Results;
import com.hgc.homggoo.services.notification.NotificationService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String postIndex(@SessionAttribute(value = "signedUser") UserEntity signedUser) {
        if (signedUser == null) {
            return null;
        }
        NotificationEntity[] notification = this.notificationService.getByEmail(signedUser.getEmail());
        JSONArray response = new JSONArray();
        if (notification != null) {
            for (NotificationEntity result : notification) {
                JSONObject obj = new JSONObject();
                obj.put("articleId", result.getArticleId());
                obj.put("email", signedUser.getEmail());
                obj.put("createdAt", result.getCreatedAt());
                response.put(obj);
            }
        }

        return  response.toString();
    }
}
