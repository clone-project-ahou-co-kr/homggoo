package com.hgc.homggoo.mappers.comment;

import com.hgc.homggoo.entities.comment.CommentEntity;
import com.hgc.homggoo.vos.CommentVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommentMapper {
    int insert(@Param(value = "comment") CommentEntity comment);

    CommentEntity selectById(@Param(value = "id") int id);

    CommentVo[] selectByArticleId(@Param(value = "articleId") int articleId);

    int update(@Param(value = "comment") CommentEntity comment);
}
