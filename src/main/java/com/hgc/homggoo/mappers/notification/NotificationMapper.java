package com.hgc.homggoo.mappers.notification;

import com.hgc.homggoo.entities.notification.NotificationEntity;
import com.hgc.homggoo.entities.user.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NotificationMapper {
    int insert(@Param(value = "notification") NotificationEntity notification);

    NotificationEntity[] getByEmail(@Param(value = "email") String email);
}
