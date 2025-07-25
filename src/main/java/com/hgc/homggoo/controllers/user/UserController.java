package com.hgc.homggoo.controllers.user;

import com.hgc.homggoo.entities.images.ImageEntity;
import com.hgc.homggoo.entities.product.ProductOrderEntity;
import com.hgc.homggoo.entities.user.UserEntity;
import com.hgc.homggoo.results.ResultTuple;
import com.hgc.homggoo.services.article.ArticleService;
import com.hgc.homggoo.services.image.ImageService;
import com.hgc.homggoo.services.notice.NoticeService;
import com.hgc.homggoo.services.oAuth.CustomOAuth2User;
import com.hgc.homggoo.services.product.ProductService;
import com.hgc.homggoo.services.user.UserService;
import com.hgc.homggoo.vos.ArticleVo;
import com.hgc.homggoo.vos.NoticeVo;
import com.hgc.homggoo.vos.ProductBuyVo;
import com.hgc.homggoo.vos.ProductVo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    private final NoticeService noticeService;
    private final UserService userService;
    private final ArticleService articleService;
    private final ImageService imageService;
    private final ProductService productService;

    public UserController(NoticeService noticeService, UserService userService, ArticleService articleService, ImageService imageService, ProductService productService) {
        this.noticeService = noticeService;
        this.userService = userService;
        this.articleService = articleService;
        this.imageService = imageService;
        this.productService = productService;
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
    public String getLogin(@AuthenticationPrincipal CustomOAuth2User user,
                           HttpSession session,
                           Model model) {
        // 이미 로그인된 경우 홈으로 리다이렉트
        if (user != null) {
            return "redirect:/";
        }

        // 세션에서 에러 메시지를 꺼내고, 모델에 넣고, 제거
        String errorMessage = (String) session.getAttribute("OAUTH2_ERROR_MESSAGE");
        if (errorMessage != null) {
            model.addAttribute("OAUTH2_ERROR_MESSAGE", errorMessage);
            session.removeAttribute("OAUTH2_ERROR_MESSAGE");
        }

        return "user/login"; // templates/user/login.html
    }


    @RequestMapping(value = "/logout", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String postLogout(@SessionAttribute(value = "signedUser", required = false) UserEntity signedUser, HttpSession session) {
//        session.setAttribute("signedUser", null);
        session.removeAttribute("signedUser");
        return "redirect:/";
    }

    @RequestMapping(value = "/admin/login", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getAdminLogin() {

        return "user/adminLogin";
    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getAdmin(Model model,@SessionAttribute(value = "signedUser", required = false) UserEntity signedUser) {
        if(signedUser==null || !signedUser.isAdmin()){
            return "redirect:/";
        }
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
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + UriUtils.encode(image.getName(), StandardCharsets.UTF_8) + "\"")
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

        List<ProductVo> products = this.productService.selectByUserEmail(signedUser.getEmail());
        List<ProductBuyVo> productOrders = this.productService.selectOrderProduct(signedUser);

        int productCount = products.size();
        int orderCount = productOrders.size();

        if (products.size() > 3) {
            products = products.subList(0, 3); // 앞 3개만
        } else {
            List<ProductVo> productSize = this.productService.selectByUserEmail(signedUser.getEmail());
            model.addAttribute("productSize", productSize);
        }

        if (productOrders.size() > 3) {
            productOrders = productOrders.subList(0, 3);
        } else {
            List<ProductBuyVo> productOrderSize = this.productService.selectOrderProduct(signedUser);
            model.addAttribute("productOrderSize", productOrderSize);
        }

        model.addAttribute("products", products);
        model.addAttribute("productOrders", productOrders);

        model.addAttribute("productCount", productCount);
        model.addAttribute("price", productOrders);
        model.addAttribute("orderCount", orderCount);

        model.addAttribute("signedUser", signedUser);
        return "user/mypage";
    }

    @RequestMapping(value = "/myproduct", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getMyProduct(@SessionAttribute(value = "signedUser", required = false) UserEntity signedUser,
                               Model model) {
        List<ProductVo> products = this.productService.selectByUserEmail(signedUser.getEmail());
        model.addAttribute("products", products);
        return "user/myProduct";
    }

    @RequestMapping(value = "/myproductlike", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getMyProductLike(@SessionAttribute(value = "signedUser", required = false) UserEntity signedUser,
                               Model model) {
        List<ProductVo> products = this.productService.getLikedProductsByUser(signedUser.getEmail());
        model.addAttribute("products", products);
        return "user/myProductLike";
    }

    @RequestMapping(value = "/mypage/edit", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getMypageEdit(@SessionAttribute(value = "signedUser", required = false) UserEntity signedUser, Model model) {
        if (signedUser == null) {
            return "redirect:/user/login";
        }
        model.addAttribute("signedUser", signedUser);
        return "user/edit";
    }


    //회원관리 search by nicknmae
    @RequestMapping(value = "/search", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getSearch() {
        return "user/admin";
    }

}
