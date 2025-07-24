package com.hgc.homggoo.services.article;

import com.hgc.homggoo.entities.notification.NotificationEntity;
import com.hgc.homggoo.mappers.notification.NotificationMapper;
import com.hgc.homggoo.mappers.user.UserMapper;
import com.hgc.homggoo.results.Results;
import com.hgc.homggoo.vos.ArticleVo;
import com.hgc.homggoo.entities.article.ArticleEntity;
import com.hgc.homggoo.entities.article.ArticleUserLikeEntity;
import com.hgc.homggoo.entities.user.UserEntity;
import com.hgc.homggoo.mappers.article.ArticleMapper;
import com.hgc.homggoo.mappers.article.ArticleUserLikeMapper;
import com.hgc.homggoo.regexes.ArticleRegex;
import com.hgc.homggoo.results.CommonResult;
import com.hgc.homggoo.results.ResultTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.transform.Result;
import java.time.LocalDateTime;

@Service
public class ArticleService {
    private final ArticleMapper articleMapper;
    private final ArticleUserLikeMapper articleUserLikeMapper;
    private final NotificationMapper notificationMapper;
    private final UserMapper userMapper;

    @Autowired
    public ArticleService(ArticleMapper articleMapper, ArticleUserLikeMapper articleUserLikeMapper, NotificationMapper notificationMapper, UserMapper userMapper) {
        this.articleMapper = articleMapper;
        this.articleUserLikeMapper = articleUserLikeMapper;
        this.notificationMapper = notificationMapper;
        this.userMapper = userMapper;
    }

    public ResultTuple<ArticleEntity> add(ArticleEntity article,
                                          UserEntity user) {

        if (user == null) {
            return ResultTuple.<ArticleEntity>builder().
                    result(CommonResult.FAILURE_SESSION_EXPIRED).
                    build();
        }
        if (article.getTitle() == null ||
                article.getContent() == null ||
                article.getBoardId() == null ||
                article.getBoardId().isEmpty() ||
                article.getCategoryId() == null ||
                article.getCategoryId().isEmpty()) {
            return ResultTuple.<ArticleEntity>builder().result(CommonResult.FAILURE).build();
        }
        if (!ArticleRegex.title.matches(article.getTitle()) ||
                !ArticleRegex.content.matches(article.getContent())) {
            return ResultTuple.<ArticleEntity>builder().result(CommonResult.FAILURE).build();
        }

        article.setUserEmail(user.getEmail());
        article.setBoardId(article.getBoardId());
        article.setCategoryId(article.getCategoryId());
        article.setView(0);
        article.setCreatedAt(LocalDateTime.now());
        article.setModifiedAt(LocalDateTime.now());
        article.setDeleted(false);
        this.articleMapper.insert(article);

        int rowCount = this.articleMapper.insert(article);
        if (rowCount == 0) {
            return ResultTuple.<ArticleEntity>builder().result(CommonResult.FAILURE).build();
        }
        
        // 게시글 등록 알림
        NotificationEntity notification = NotificationEntity.builder()
                .receiverEmail(article.getUserEmail())
                .articleId(article.getId())
                .createdAt(LocalDateTime.now())
                .type("article")
                .build();
        this.notificationMapper.insert(notification);

        return ResultTuple.<ArticleEntity>builder().result(CommonResult.SUCCESS).payload(article).build();

    }

    public Boolean articleLike(UserEntity signedUser, int articleId) {
        if (signedUser == null || signedUser.isDeleted()) {

            return null;
        }

        if (this.articleUserLikeMapper.selectByArticleIdAndUserEmail(articleId, signedUser.getEmail()) == null) {

            ArticleUserLikeEntity articleUserLike = ArticleUserLikeEntity.builder()
                    .articleId(articleId)
                    .userEmail(signedUser.getEmail())
                    .createdAt(LocalDateTime.now())
                    .build();
            return this.articleUserLikeMapper.insert(articleUserLike) > 0 ? true : null;

        } else {

            return this.articleUserLikeMapper.delete(articleId, signedUser.getEmail()) > 0 ? false : null;
        }
    }

    public ArticleVo read(/*UserEntity signedUser, */int id) {
        if (id < 1) {
            return null;
        }

        return this.articleMapper.selectById(id);
    }

    public Results incrementView(ArticleEntity article) {
        if (article == null || article.getId() < 1) {
            return CommonResult.FAILURE;
        }
        article.setView(article.getView() + 1);
        return this.articleMapper.update(article) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    public ArticleVo[] getByBoardId(String boardId) {
        return this.articleMapper.selectByBoardId(boardId);
    }

    public ArticleVo[] getByBoardIdAndCategoryId(String boardId, String categoryId) {
        return this.articleMapper.selectByBoardIdAndCategoryId(boardId, categoryId);
    }

    public ArticleVo[] getAll() {
        return this.articleMapper.selectAll();
    }

    public ArticleVo[] getAllIncludeDeleted() {
        return this.articleMapper.selectAllIncludeDeleted();
    }

    public Results update(ArticleEntity article, int id, UserEntity user) {
        if (article == null || article.getId() < 1) {
            return CommonResult.FAILURE;
        }
        article.setModifiedAt(LocalDateTime.now());

        return this.articleMapper.modify(article) > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }

    public Results delete(int id) {
        if (id < 1) {
            return CommonResult.FAILURE;
        }

        ArticleEntity dbArticle = this.articleMapper.selectById(id);
        if (dbArticle == null) {
            return CommonResult.FAILURE_ABSENT;
        }
        if (dbArticle.isDeleted()) {
            return CommonResult.FAILURE_SESSION_EXPIRED;
        }
        dbArticle.setDeleted(true);

        return this.articleMapper.delete(dbArticle) > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }

    public Results adminDelete(UserEntity signedUser, int index) {
        if (index < 1) {
            return CommonResult.FAILURE;
        }
        if (!signedUser.isAdmin()) {
            return CommonResult.FAILURE_SESSION_EXPIRED;
        }
        UserEntity dbUser = this.userMapper.selectByEmail(signedUser.getEmail());
        ArticleEntity dbArticle = this.articleMapper.selectById(index);
        if (dbArticle.isDeleted()) {
            return CommonResult.FAILURE_ABSENT;
        }
        dbArticle.setDeleted(true);
        return this.articleMapper.delete(dbArticle) > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }

    public Results restoreArticle(UserEntity signedUser, int index) {
        if (index < 1 || signedUser == null) {
            return CommonResult.FAILURE;
        }
        UserEntity dbUser = this.userMapper.selectByEmail(signedUser.getEmail());
        if (!dbUser.isAdmin()) {
            return CommonResult.FAILURE_ADMIN;
        }
        ArticleEntity dbArticle = this.articleMapper.selectAdminById(index);
        if (dbArticle == null) {
            return CommonResult.FAILURE_ABSENT;
        }
        dbArticle.setModifiedAt(LocalDateTime.now());
        dbArticle.setDeleted(false);
        return this.articleMapper.update(dbArticle) > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }
}
