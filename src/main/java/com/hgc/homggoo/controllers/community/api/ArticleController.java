package com.hgc.homggoo.controllers.community.api;

import com.hgc.homggoo.entities.article.ArticleEntity;
import com.hgc.homggoo.entities.comment.CommentEntity;
import com.hgc.homggoo.entities.user.UserEntity;
import com.hgc.homggoo.mappers.article.ArticleMapper;
import com.hgc.homggoo.results.CommonResult;
import com.hgc.homggoo.results.ResultTuple;
import com.hgc.homggoo.results.Results;
import com.hgc.homggoo.services.article.ArticleService;
import com.hgc.homggoo.services.comment.CommentService;
import com.hgc.homggoo.vos.ArticleVo;
import com.hgc.homggoo.vos.CommentVo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/posts")
public class ArticleController {
    private final ArticleService articleService;
    private final CommentService commentService;

    @Autowired
    public ArticleController(ArticleMapper articleMapper, ArticleService articleService, CommentService commentService) {
        this.articleService = articleService;
        this.commentService = commentService;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteId(@PathVariable(value = "id", required = false) String id,
                           @SessionAttribute(value = "signedUser", required = false) UserEntity signedUser) {
        if (signedUser == null) {
            return "redirect:/";
        }

        Results result = this.articleService.delete(Integer.parseInt(id));
        JSONObject response = new JSONObject();
        response.put("result", result.nameToLower());

        return response.toString();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    public String patchId(@PathVariable(value = "id", required = false) String id,
                         ArticleEntity article,
                         @SessionAttribute(value = "signedUser", required = false) UserEntity signedUser) {
        if (signedUser == null) {
            return "redirect:/";
        }
        Results result = this.articleService.update(article, Integer.parseInt(id), signedUser);
        JSONObject response = new JSONObject();
        response.put("result", result.nameToLower());

        return response.toString();
    }

    @RequestMapping(value = "/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String postIndex(@RequestParam(value = "id") int id) {
        ArticleVo result = this.articleService.read(id);
        if (result != null) {
            this.articleService.incrementView(result);
        }
        JSONObject response = new JSONObject();
        response.put("result", CommonResult.SUCCESS.nameToLower());
        response.put("id", result.getId());
        response.put("boardId", result.getBoardId());
        response.put("categoryId", result.getCategoryId());
        response.put("userEmail", result.getUserEmail());
        response.put("title", result.getTitle());
        response.put("content", result.getContent());
        response.put("view", result.getView());
        response.put("createdAt", result.getCreatedAt());
        response.put("modifiedAt", result.getModifiedAt());
        response.put("isDeleted", result.isDeleted());
        response.put("likeCount", result.getLikeCount());
        response.put("nickname", result.getNickname());
        response.put("profile", result.getImageUrl());
        System.out.println(result.getImageUrl());

        return response.toString();
    }

    @RequestMapping(value = "/new", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String postNew(ArticleEntity article,
                          @SessionAttribute(value = "signedUser", required = false) UserEntity signedUser) {

        ResultTuple<ArticleEntity> result = this.articleService.add(article, signedUser);
        JSONObject response = new JSONObject();
        response.put("result", result.getResult().nameToLower());
        if (result.getPayload() != null) {
            response.put("id", result.getPayload().getId());
        }

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
    }

    @RequestMapping(value = "/comment", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public CommentVo[] getComment(@RequestParam(value = "id", required = false) int id,
                                  @SessionAttribute(value = "signedUser", required = false) UserEntity signedUser) {
        ResultTuple<CommentVo[]> result = this.commentService.getByArticleId(id);
        if (result.getResult() == CommonResult.SUCCESS) {
            CommentVo[] comments = result.getPayload();
            String signedUserEmail = signedUser == null ? null : signedUser.getEmail();
            for (CommentVo comment : comments) {
                if (comment.isDeleted()) {
                    comment.setContent(null);
                }
                comment.setMine(comment.getUserEmail().equals(signedUserEmail));
            }
            return comments;
        }
        return new CommentVo[0];
    }

    @RequestMapping(value = "/comment", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String postComment(@SessionAttribute(value = "signedUser", required = false) UserEntity signedUser,
                              CommentEntity comment) {
        Results result = this.commentService.insert(comment, signedUser);
        JSONObject response = new JSONObject();
        response.put("result", result.nameToLower());

        return response.toString();
    }

//    @RequestMapping(value = "/reply", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//    public String postReply(@SessionAttribute(value = "signedUser", required = false) UserEntity signedUser,
//                            CommentEntity comment) {
//
//    }
}
