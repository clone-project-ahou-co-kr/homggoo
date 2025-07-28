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

    private int likeCount;
    private int commentCount;
    private boolean isLiked;
    private String nickname;
    private String categoryDisplayText;
    private String imageUrl;
}
