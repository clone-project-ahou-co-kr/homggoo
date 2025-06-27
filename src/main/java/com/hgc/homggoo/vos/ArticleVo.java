package com.hgc.homggoo.vos;

import com.hgc.homggoo.entities.article.ArticleEntity;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ArticleVo extends ArticleEntity {
    private int likeCount;
    private boolean isLiked;
    private String nickname;
}
