package com.hgc.homggoo.services.notification;

import com.hgc.homggoo.entities.notification.NotificationEntity;
import com.hgc.homggoo.entities.user.UserEntity;
import com.hgc.homggoo.mappers.notification.NotificationMapper;
import com.hgc.homggoo.results.CommonResult;
import com.hgc.homggoo.results.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final NotificationMapper notificationMapper;

    @Autowired
    public NotificationService(NotificationMapper notificationMapper) {
        this.notificationMapper = notificationMapper;
    }

    public NotificationEntity[] getByEmail(String email) {
        if (email == null) {
            return null;
        }

        return this.notificationMapper.getByEmail(email);
    }
}
