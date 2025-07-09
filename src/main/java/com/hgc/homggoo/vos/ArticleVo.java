package com.hgc.homggoo.vos;

import com.hgc.homggoo.entities.article.ArticleEntity;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ArticleVo extends ArticleEntity {
    public static String getRelativeTime(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(createdAt, now);

        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();

        if (minutes < 1) {
            return "방금 전";
        } else if (minutes < 60) {
            return minutes + "분 전";
        } else if (hours < 24) {
            return hours + "시간 전";
        } else {
            return days + "일 전";
        }
    }

    private int likeCount;
    private boolean isLiked;
    private String nickname;
    private String categoryDisplayText;
    private String imageUrl;
}
