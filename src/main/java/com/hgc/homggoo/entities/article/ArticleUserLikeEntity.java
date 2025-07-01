package com.hgc.homggoo.entities.article;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"articleId", "userEmail"})
@Builder
public class ArticleUserLikeEntity {
    private int articleId;
    private String userEmail;
    private LocalDateTime createdAt;
}
