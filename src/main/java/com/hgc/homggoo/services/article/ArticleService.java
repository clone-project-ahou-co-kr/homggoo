package com.hgc.homggoo.services.article;

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
    private static final Logger log = LoggerFactory.getLogger(ArticleService.class);
    private final ArticleMapper articleMapper;
    private final ArticleUserLikeMapper articleUserLikeMapper;

    @Autowired
    public ArticleService(ArticleMapper articleMapper, ArticleUserLikeMapper articleUserLikeMapper) {
        this.articleMapper = articleMapper;
        this.articleUserLikeMapper = articleUserLikeMapper;
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

        return this.articleMapper.insert(article) > 0 ?
                ResultTuple.<ArticleEntity>builder().result(CommonResult.SUCCESS).payload(article).build() :
                ResultTuple.<ArticleEntity>builder().result(CommonResult.FAILURE).build();
    }

    public Boolean articleLike(UserEntity signedUser, int articleId) {
        if (signedUser == null || signedUser.isDeleted() || signedUser.isDormancy()) {

            return null;
        }
        // "dd"는 로그인한 사용자
        if (this.articleUserLikeMapper.selectByArticleIdAndUserEmail(articleId, "dd") == null) {
            // 음악 인덱스, 유저 이메일로 SELECT 해서 null 이면 좋아요 하지 않은 상태
            // > 좋아요 해주면 됨
            ArticleUserLikeEntity musicUserLike = ArticleUserLikeEntity.builder()
                    .articleId(articleId)
                    .userEmail("dd")
                    .createdAt(LocalDateTime.now())
                    .build();
            return this.articleUserLikeMapper.insert(musicUserLike) > 0 ? true : null;
            // true 를 반환하면 최종적으로 좋아요 한 상태라는 의미
        } else {
            // null이 아니면 좋아요한 상태
            // > 좋아요 취소
            return this.articleUserLikeMapper.delete(articleId, "dd") > 0 ? false : null;
            // false 를 반환하면 최종적으로 좋아요 하지 않은 상태라는 의미
        }
    }

    public ArticleVo read (/*UserEntity signedUser, */int id) {
        if (id < 1) {
            return null;
        }

        return this.articleMapper.selectById(id);
    }

    public Results incrementView (ArticleEntity article) {
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


}
