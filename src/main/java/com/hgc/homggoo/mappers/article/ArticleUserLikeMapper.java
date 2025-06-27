package com.hgc.homggoo.mappers.article;

import com.hgc.homggoo.entities.article.ArticleUserLikeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ArticleUserLikeMapper {
    int delete(@Param(value = "articleId") int articleId,
               @Param(value = "userEmail") String userEmail);

    int insert (@Param(value = "articleUserLikes") ArticleUserLikeEntity articleUserLikes);

    ArticleUserLikeEntity selectByArticleidAndUserEmail(@Param(value = "articleId") int articleId,
                                                         @Param(value = "userEmail") String userEmail);
}
