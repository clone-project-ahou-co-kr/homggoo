package com.hgc.homggoo.entities.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleUserLikeEntity {
    private int articleId;
    private String userEmail;
    private LocalDateTime createdAt;
}
