package com.hgc.homggoo.entities.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEntity {
    private int id;
    private String receiverEmail;
    private int articleId;
    private LocalDateTime createdAt;
    private boolean isDeleted;
}
