package com.hgc.homggoo.services.article;

import com.hgc.homggoo.vos.ArticleVo;
import com.hgc.homggoo.entities.article.ArticleEntity;
import com.hgc.homggoo.entities.article.ArticleUserLikeEntity;
import com.hgc.homggoo.entities.user.UserEntity;
import com.hgc.homggoo.mappers.article.ArticleMapper;
import com.hgc.homggoo.mappers.article.ArticleUserLikeMapper;
import com.hgc.homggoo.regexes.ArticleRegex;
import com.hgc.homggoo.results.CommonResult;
import com.hgc.homggoo.results.ResultTuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ArticleService {
    private final ArticleMapper articleMapper;
    private final ArticleUserLikeMapper articleUserLikeMapper;

    @Autowired
    public ArticleService(ArticleMapper articleMapper, ArticleUserLikeMapper articleUserLikeMapper) {
        this.articleMapper = articleMapper;
        this.articleUserLikeMapper = articleUserLikeMapper;
    }

    public ResultTuple<ArticleEntity> add(ArticleEntity article) {

        if (article.getTitle() == null ||
            article.getContent() == null ||
            article.getBoardId() == null ||
            article.getBoardId().isEmpty()) {
            return ResultTuple.<ArticleEntity>builder().result(CommonResult.FAILURE).build();
        }
        if (!ArticleRegex.title.matches(article.getTitle()) ||
            !ArticleRegex.content.matches(article.getContent())) {
            return ResultTuple.<ArticleEntity>builder().result(CommonResult.FAILURE).build();
        }
        article.setUserEmail("dd");
        article.setView(0);
        article.setCreatedAt(LocalDateTime.now());
        article.setModifiedAt(LocalDateTime.now());
        article.setDeleted(false);

        return this.articleMapper.insert(article) > 0 ?
                ResultTuple.<ArticleEntity>builder().result(CommonResult.SUCCESS).build() :
                ResultTuple.<ArticleEntity>builder().result(CommonResult.FAILURE).build();
    }

    public Boolean articleLike(UserEntity signedUser, int articleId) {
        if (signedUser == null || signedUser.isDeleted() || signedUser.isDormancy()) {

            return null;
        }
        // "dd"는 로그인한 사용자
        if (this.articleUserLikeMapper.selectByArticleidAndUserEmail(articleId, "dd") == null) {
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

    public ArticleVo[] getByBoardId(String boardId) {
        return this.articleMapper.selectByBoardId(boardId);
    }
}
