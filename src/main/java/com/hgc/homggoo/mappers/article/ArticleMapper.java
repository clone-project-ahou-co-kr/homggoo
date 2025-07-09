package com.hgc.homggoo.mappers.article;

import com.hgc.homggoo.vos.ArticleVo;
import com.hgc.homggoo.entities.article.ArticleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ArticleMapper {
     int insert(@Param(value = "article") ArticleEntity article);

     int update(@Param(value = "article") ArticleEntity article);

     ArticleVo[] selectByBoardId(@Param(value = "boardId") String boardId);

     ArticleVo[] selectByBoardIdAndCategoryId(@Param(value = "boardId") String boardId,
                                              @Param(value = "categoryId") String categoryId);

     ArticleVo selectById(@Param(value = "id") int id);

     ArticleVo[] selectAll();
}
