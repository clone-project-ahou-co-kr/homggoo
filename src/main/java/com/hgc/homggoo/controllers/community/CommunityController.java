package com.hgc.homggoo.controllers.community;

import com.hgc.homggoo.entities.user.UserEntity;
import com.hgc.homggoo.services.article.ArticleService;
import com.hgc.homggoo.vos.ArticleVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

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
    public String getPostsNew() {
        return "/community/posts/new";
    }

    @RequestMapping(value = "/interior", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getHoney(@RequestParam(value = "boardId") String boardId,
                                Model model) {

        ArticleVo[] article = this.articleService.getByBoardId(boardId);
        model.addAttribute("articles", article);

        return "/community/interior";
    }

    @RequestMapping(value = "/posts", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getPosts(@SessionAttribute(value = "signedUser", required = false) UserEntity signedUser,
                           Model model,
                           HttpServletRequest request) {

        return "/community/posts";








































































    }


}
