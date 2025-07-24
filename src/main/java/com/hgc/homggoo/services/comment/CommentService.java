package com.hgc.homggoo.services.comment;

import com.hgc.homggoo.entities.article.ArticleEntity;
import com.hgc.homggoo.entities.comment.CommentEntity;
import com.hgc.homggoo.entities.notification.NotificationEntity;
import com.hgc.homggoo.entities.user.UserEntity;
import com.hgc.homggoo.mappers.article.ArticleMapper;
import com.hgc.homggoo.mappers.comment.CommentMapper;
import com.hgc.homggoo.mappers.notification.NotificationMapper;
import com.hgc.homggoo.results.CommonResult;
import com.hgc.homggoo.results.ResultTuple;
import com.hgc.homggoo.results.Results;
import com.hgc.homggoo.vos.ArticleVo;
import com.hgc.homggoo.vos.CommentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommentService {
    private final ArticleMapper articleMapper;
    private final CommentMapper commentMapper;
    private final NotificationMapper notificationMapper;

    @Autowired
    public CommentService(CommentMapper commentMapper, ArticleMapper articleMapper, NotificationMapper notificationMapper) {
        this.commentMapper = commentMapper;
        this.articleMapper = articleMapper;
        this.notificationMapper = notificationMapper;
    }

    public Results insert(CommentEntity comment, UserEntity user) {
        if (user == null) {
            return CommonResult.FAILURE_SESSION_EXPIRED;
        }

        if (comment.isDeleted() || comment.getContent() == null) {
            return CommonResult.FAILURE;
        }

        comment.setUserEmail(user.getEmail());
        comment.setContent(comment.getContent());
        comment.setCommentId(comment.getCommentId());
        comment.setDeleted(false);
        comment.setCreatedAt(LocalDateTime.now());

        ArticleVo article = this.articleMapper.selectById(comment.getArticleId());

        if (article != null && !article.getUserEmail().equals(user.getEmail())) {
            // 자신 글에 단 댓글은 알림 제외
            NotificationEntity notification = NotificationEntity.builder()
                    .receiverEmail(article.getUserEmail())
                    .articleId(article.getId())
                    .createdAt(LocalDateTime.now())
                    .type("comment")
                    .build();
            this.notificationMapper.insert(notification);
        }

        return this.commentMapper.insert(comment) > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }

    public ResultTuple<CommentVo[]> getByArticleId(int articleId) {
        if (articleId < 1) {
            return ResultTuple.<CommentVo[]>builder().result(CommonResult.FAILURE).build();
        }
        ArticleEntity dbArticle = this.articleMapper.selectById(articleId);
        if (dbArticle == null || dbArticle.isDeleted()) {
            return ResultTuple.<CommentVo[]>builder().result(CommonResult.FAILURE_ABSENT).build();
        }
        return ResultTuple.<CommentVo[]>builder()
                .result(CommonResult.SUCCESS)
                .payload(this.commentMapper.selectByArticleId(articleId))
                .build();
    }


}
