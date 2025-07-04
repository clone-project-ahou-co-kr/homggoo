package com.hgc.homggoo.controllers.community;

import com.hgc.homggoo.entities.user.UserEntity;
import com.hgc.homggoo.services.article.ArticleService;
import com.hgc.homggoo.vos.ArticleVo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Controller
@RequestMapping(value = "community")
public class CommunityController {
    private final ArticleService articleService;

    @Autowired
    public CommunityController(ArticleService articleService) {
        this.articleService = articleService;
    }

/*    @RequestMapping(value = "interior", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getInterior() {
        return "/community/interior";
    }*/

    @RequestMapping(value = "/posts/new", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getPostsNew(@SessionAttribute(value = "signedUser", required = false) UserEntity signedUser) {
        if (signedUser == null) {
            System.out.println("세션에러");
            return "redirect:/user/login";
        }

        return "/community/posts/new";
    }

    @RequestMapping(value = "/{boardId}", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getInterior(@PathVariable String boardId,
                              @RequestParam(value = "categoryId", required = false) String categoryId,
                              Model model) {

/*        if (categoryId == null || categoryId.isEmpty()) {
            ArticleVo[] article = this.articleService.getByBoardId(boardId);
            model.addAttribute("articles", article);
            return  "/community/community";
        }
        ArticleVo[] article = this.articleService.getByBoardIdAndCategoryId(boardId, categoryId);
        for (ArticleVo articleVo : article) {
            model.addAttribute("articles", articleVo);
        }*/

        ArticleVo[] article;

        if (categoryId == null || categoryId.isEmpty()) {
            article = this.articleService.getByBoardId(boardId);
        } else {
            article = this.articleService.getByBoardIdAndCategoryId(boardId, categoryId);
        }

        for (ArticleVo vo : article) {
            if (vo.getBoardId() == null) {
                vo.setBoardId(boardId); // boardId 주입
            }
             System.out.println(vo.getBoardId());
        }

        model.addAttribute("articles", article);
        System.out.println(Arrays.toString(article));
        return "community/community";
    }

    @RequestMapping(value = "/posts", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getPosts(@SessionAttribute(value = "signedUser", required = false) UserEntity signedUser,
                           Model model,
                           HttpServletRequest request) {

        return "/community/posts";
    }


}
