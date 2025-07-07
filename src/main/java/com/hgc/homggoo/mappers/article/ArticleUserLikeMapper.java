package com.hgc.homggoo.mappers.article;

import com.hgc.homggoo.entities.article.ArticleUserLikeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ArticleUserLikeMapper {
    int delete(@Param(value = "articleId") int articleId,
               @Param(value = "userEmail") String userEmail);

    int insert (@Param(value = "articleUserLike") ArticleUserLikeEntity articleUserLike);

    ArticleUserLikeEntity selectByArticleIdAndUserEmail(@Param(value = "articleId") int articleId,
                                                         @Param(value = "userEmail") String userEmail);
}
