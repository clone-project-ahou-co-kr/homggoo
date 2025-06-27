package com.hgc.homggoo.services.article;

import com.hgc.homggoo.entities.article.ArticleEntity;
import com.hgc.homggoo.mappers.article.ArticleMapper;
import com.hgc.homggoo.regexes.ArticleRegex;
import com.hgc.homggoo.results.CommonResult;
import com.hgc.homggoo.results.ResultTuple;
import com.hgc.homggoo.results.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.transform.Result;
import java.time.LocalDateTime;

@Service
public class ArticleService {
    private final ArticleMapper articleMapper;

    @Autowired
    public ArticleService(ArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
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

}
