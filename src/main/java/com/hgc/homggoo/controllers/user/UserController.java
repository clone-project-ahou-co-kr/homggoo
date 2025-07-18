package com.hgc.homggoo.controllers.user;

import com.hgc.homggoo.entities.images.ImageEntity;
import com.hgc.homggoo.entities.user.UserEntity;
import com.hgc.homggoo.results.CommonResult;
import com.hgc.homggoo.results.ResultTuple;
import com.hgc.homggoo.services.article.ArticleService;
import com.hgc.homggoo.services.image.ImageService;
import com.hgc.homggoo.services.notice.NoticeService;
import com.hgc.homggoo.services.oAuth.CustomOAuth2User;
import com.hgc.homggoo.services.user.UserService;
import com.hgc.homggoo.vos.ArticleVo;
import com.hgc.homggoo.vos.NoticeVo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    private final NoticeService noticeService;
    private final UserService userService;
    private final ArticleService articleService;
    private final ImageService imageService;

    public UserController(NoticeService noticeService, UserService userService, ArticleService articleService, ImageService imageService) {
        this.noticeService = noticeService;
        this.userService = userService;
        this.articleService = articleService;
        this.imageService = imageService;
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getRegister(@AuthenticationPrincipal CustomOAuth2User signedUser, HttpSession session) {
        if (signedUser != null) {
            // 세션 저장도 가능
            return "redirect:/";
        }
        return "user/register";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getLogin(@AuthenticationPrincipal CustomOAuth2User user) {
        // 이미 로그인된 경우 홈으로
        if (user != null) {
            System.out.println(user.getEmail());
            return "redirect:/";
        }
        return "user/login"; // 로그인 폼
    }
    @RequestMapping(value = "/logout", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String postLogout(@SessionAttribute(value = "signedUser", required = false) UserEntity signedUser, HttpSession session) {
        session.setAttribute("signedUser", null);
        return "redirect:/";
    }

    @RequestMapping(value = "/admin/login", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getAdminLogin() {
        return "user/adminLogin";
    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getAdmin(Model model) {
        ResultTuple<NoticeVo[]> result = this.noticeService.getAll();
        NoticeVo[] noticeList = result.getPayload();
        //todo 유저 회원수 가지고 오기 위해서 userService getall 추가.
        UserEntity[] userList = this.userService.getAll();
        ArticleVo[] articleList = this.articleService.getAll();

        // 모델에 각각 저장
        model.addAttribute("noticeList", noticeList);
        model.addAttribute("noticeCount", noticeList.length);

        model.addAttribute("articleList", articleList);
        model.addAttribute("articleCount", articleList.length);

        model.addAttribute("userList", userList);
        model.addAttribute("userCount", userList.length);

        return "user/admin";
    }

    @RequestMapping(value = "/notice/image", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImage(@RequestParam(value = "index", required = false) int index) {
        //responseentity는 응답을 돌려주기위한 상태 타입.
        ImageEntity image = this.imageService.getByIndex(index);
        if (image == null) {
            return ResponseEntity.notFound().build();
            //notFound()는 404를 날린다.
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + image.getName())
                .contentLength(image.getData().length)
                .contentType(MediaType.parseMediaType(image.getContentType())) //문자열이라서 mediatype으로 바꿔주어야한다.
                .body(image.getData());//ok는 상태코드 200;
    }

    @RequestMapping(value = "/admin/modify", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getAdminModify(@RequestParam(value = "index") int index, Model model) {
        ResultTuple<NoticeVo> result = this.noticeService.getByIndex(index);
        NoticeVo notice = result.getPayload();
        model.addAttribute("notice", notice);
        return "user/modify";
    }

    @RequestMapping(value = "/admin/notice", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getAdminNotice() {
        return "user/noticeWrite";
    }

    //마이페이지
    @RequestMapping(value = "/mypage", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE
    )
    public String getMypage(@SessionAttribute(value = "signedUser", required = false) UserEntity signedUser,
                            Model model) {
        if (signedUser == null) {
            return "redirect:/user/login"; // 또는 401 페이지로 리다이렉트
        }
        model.addAttribute("signedUser", signedUser);
        return "user/mypage";
    }

}
