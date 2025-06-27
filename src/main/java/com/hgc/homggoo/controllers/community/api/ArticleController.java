package com.hgc.homggoo.controllers.community.api;

import com.hgc.homggoo.entities.article.ArticleEntity;
import com.hgc.homggoo.entities.user.UserEntity;
import com.hgc.homggoo.mappers.article.ArticleMapper;
import com.hgc.homggoo.results.CommonResult;
import com.hgc.homggoo.results.ResultTuple;
import com.hgc.homggoo.services.article.ArticleService;
import com.hgc.homggoo.vos.ArticleVo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(value = "/like", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    public String patchLike(@SessionAttribute(value = "signedUser", required = false) UserEntity signedUser,
                            @RequestParam(value = "index", required = false) int index) {
        Boolean result = this.articleService.articleLike(signedUser, index);
        JSONObject response = new JSONObject();
        if (result == null) {
            response.put("result", CommonResult.FAILURE.nameToLower());
        } else {
            response.put("result", result);
        }
        return response.toString();
        // {result: failure} : 실패한것
        // {result: true} : 좋아하게 된 것
        // {result: false} : 좋아하지 않게 된 것
    }
}
