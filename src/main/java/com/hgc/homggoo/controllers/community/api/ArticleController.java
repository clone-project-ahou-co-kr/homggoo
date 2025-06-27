package com.hgc.homggoo.controllers.community.api;

import com.hgc.homggoo.entities.article.ArticleEntity;
import com.hgc.homggoo.mappers.article.ArticleMapper;
import com.hgc.homggoo.results.CommonResult;
import com.hgc.homggoo.results.ResultTuple;
import com.hgc.homggoo.services.article.ArticleService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/posts/new")
public class ArticleController {
    private final ArticleService articleService;

    @Autowired
    public ArticleController(ArticleMapper articleMapper, ArticleService articleService) {
        this.articleService = articleService;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String postIndex(ArticleEntity article) {
        ResultTuple<ArticleEntity> result = this.articleService.add(article);
        JSONObject response = new JSONObject();
        response.put("result", result.getResult().nameToLower());
        return response.toString();
    }
}
