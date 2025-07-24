package com.hgc.homggoo.controllers.user.api;

import com.hgc.homggoo.entities.images.ImageEntity;
import com.hgc.homggoo.entities.notice.NoticeEntity;
import com.hgc.homggoo.entities.user.EmailTokenEntity;
import com.hgc.homggoo.entities.user.UserEntity;
import com.hgc.homggoo.results.CommonResult;
import com.hgc.homggoo.results.ResultTuple;
import com.hgc.homggoo.results.Results;
import com.hgc.homggoo.services.article.ArticleService;
import com.hgc.homggoo.services.image.ImageService;
import com.hgc.homggoo.services.notice.NoticeService;
import com.hgc.homggoo.services.user.EmailTokenService;
import com.hgc.homggoo.services.user.UserService;
import com.hgc.homggoo.vos.ArticleVo;
import com.hgc.homggoo.vos.NoticeVo;
import com.hgc.homggoo.vos.SearchVo;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(value = "/api/user")
public class UserApiController {
    private final UserService userService;
    private final EmailTokenService emailTokenService;
    private final NoticeService noticeService;
    private final ArticleService articleService;
    private final ImageService imageService;

    @Autowired
    public UserApiController(UserService userService, EmailTokenService emailTokenService, NoticeService noticeService, ArticleService articleService, ImageService imageService) {
        this.userService = userService;
        this.emailTokenService = emailTokenService;
        this.noticeService = noticeService;
        this.articleService = articleService;
        this.imageService = imageService;
    }


    @RequestMapping(value = "/profile-image", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getProfileImage(@SessionAttribute("signedUser") UserEntity signedUser) {
        //byte[]로 이미지주는건 model에 담지 못하기 때문에 REsponseEntity로 사용하여서 api호출로 가지고 와야한다.
        //th:src="/api/user/profile-image
        byte[] profileImage = signedUser.getProfile();

        if (profileImage == null || profileImage.length == 0) {
            // 기본 이미지로 대체 가능
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(profileImage);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String postLogin(@RequestParam(value = "email") String email, @RequestParam(value = "password") String password, HttpSession session) {
        ResultTuple<UserEntity> result = this.userService.login(email, password);
        if (result.getResult() == CommonResult.SUCCESS) {
            session.setAttribute("signedUser", result.getPayload());
        }
        JSONObject response = new JSONObject();
        response.put("result", result.getResult().nameToLower());
        return response.toString();
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String postRegister(UserEntity user) {
        Results result = this.userService.register(user);
        JSONObject response = new JSONObject();
        response.put("result", result.nameToLower());
        return response.toString();
    }

    @RequestMapping(value = "/register-email", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String postRegisterEmail(@RequestParam(value = "email", required = false) String email, HttpServletRequest request) throws MessagingException {
        String userAgent = request.getHeader("User-Agent");
        ResultTuple<EmailTokenEntity> result = this.userService.sendEmail(email, userAgent);
        JSONObject response = new JSONObject();
        response.put("result", result.getResult().nameToLower());
        if (result.getResult() == CommonResult.SUCCESS) {
            response.put("salt", result.getPayload().getSalt());
            System.out.println(result.getPayload().getSalt());
        }
        return response.toString();
    }

    @RequestMapping(value = "/register-email", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchRegisterEmail(EmailTokenEntity emailToken, HttpServletRequest request) {
        emailToken.setUserAgent(request.getHeader("User-Agent"));
        Results result = this.emailTokenService.verifyEmailToken(emailToken);
        JSONObject response = new JSONObject();
        response.put("result", result.nameToLower());
        return response.toString();
    }

    //탈퇴 이메일 인증
    @RequestMapping(value="/edit-email",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public String postEditEmail(@RequestParam(value = "email",required = false)String email,HttpServletRequest request) throws MessagingException {
        String userAgent = request.getHeader("User-Agent");
        ResultTuple<EmailTokenEntity> result = this.userService.sendRetireEmail(email, userAgent);
        JSONObject response = new JSONObject();
        response.put("result", result.getResult().nameToLower());
        if (result.getResult() == CommonResult.SUCCESS) {
            response.put("salt", result.getPayload().getSalt());
            System.out.println(result.getPayload().getSalt());
        }
        return response.toString();
    }

    @RequestMapping(value = "/edit-email", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchEditEmail(EmailTokenEntity emailToken, HttpServletRequest request) {
        emailToken.setUserAgent(request.getHeader("User-Agent"));
        Results result = this.emailTokenService.verifyEmailToken(emailToken);
        JSONObject response = new JSONObject();
        response.put("result", result.nameToLower());
        return response.toString();
    }

    //    admin
    @RequestMapping(value = "/admin", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String postAdmin(@RequestParam(value = "email") String email, @RequestParam(value = "password") String password, HttpSession session) {
        ResultTuple<UserEntity> result = this.userService.adminLogin(email, password);
        if (result.getResult() == CommonResult.SUCCESS_ADMIN) {
            session.setAttribute("signedUser", result.getPayload());
            System.out.println(result.getPayload().isAdmin());
        }
        JSONObject response = new JSONObject();
        response.put("result", result.getResult().nameToLower());
        return response.toString();
    }

    @RequestMapping(value = "/notice", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String postNotice(@SessionAttribute(value = "signedUser", required = false) UserEntity signedUser, NoticeEntity notice) {
        ResultTuple<NoticeVo> result = this.noticeService.addNotice(signedUser, notice);
        JSONObject response = new JSONObject();
        response.put("result", result.getResult().nameToLower());
        return response.toString();
    }

    @RequestMapping(value = "/notice", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getNotice() {
        ResultTuple<NoticeVo[]> result = this.noticeService.getAll();
        UserEntity[] user = this.userService.getAll();
        ArticleVo[] articles = this.articleService.getAllIncludeDeleted();

        JSONObject response = new JSONObject();
        response.put("result", result.getResult().nameToLower());
        response.put("data", result.getPayload());
        response.put("user", user);
        response.put("articles", articles);
        return response.toString();
    }

    @RequestMapping(value = "/notice", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteNotice(@RequestParam(value = "index") int index) {
        Results result = this.noticeService.deleteNotice(index);
        JSONObject response = new JSONObject();
        response.put("result", result.nameToLower());
        return response.toString();
    }

    @RequestMapping(value = "/notice/image", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String postNoticeImage(@SessionAttribute(value = "signedUser", required = false) UserEntity signedUser, NoticeEntity notice, @RequestParam(value = "upload", required = false) MultipartFile multipartFile) throws IOException {
        System.out.println("image controller");
        ImageEntity image = ImageEntity.builder()
                .name(multipartFile.getOriginalFilename())
                .contentType(multipartFile.getContentType())
                .data(multipartFile.getBytes())
                .build();
        Results result = this.imageService.add(signedUser, notice, image);
        JSONObject response = new JSONObject();
        if (result == CommonResult.SUCCESS) {
            response.put("url", "/user/notice/image?index=" + image.getIndex());
        } else if (result == CommonResult.FAILURE_SESSION_EXPIRED) {
            JSONObject error = new JSONObject();
            error.put("message", "세션이 만료되었거나 게시글을 작성할 권하이 없습니다. 관리자에게 문의해 주세요.");
            response.put("error", error);
        } else {
            JSONObject error = new JSONObject();
            error.put("message", "알 수 없는 이유로 이미지를 업로드하지 못하였습니다. 잠시 후 다시 시도해 주세요.");
            response.put("error", error);
        }
        return response.toString();
        //image에 파일을 올리기만 해도 실행되는것이다.
    }

    @RequestMapping(value = "/modify", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    public String patchNotice(NoticeVo notice, @RequestParam(value = "index") int index, @RequestParam(value = "password") String password, @SessionAttribute(value = "signedUser", required = false) UserEntity signedUser) {
        Results result = this.noticeService.modifyNotice(notice, password, signedUser);
        JSONObject response = new JSONObject();
        response.put("result", result.nameToLower());
        return response.toString();
    }

    @RequestMapping(value = "/admin/notice/view", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultTuple<NoticeVo> getNoticeByIndex(@RequestParam(value = "index") int index) {
        ResultTuple<NoticeVo> result = this.noticeService.getByIndex(index);
        if (result.getResult() == CommonResult.SUCCESS) {
            return result;
        }
        // 실패 응답 구성
        return ResultTuple.<NoticeVo>builder()
                .result(result.getResult())
                .payload(null)
                .build();
    }

    @RequestMapping(value = "/notice-restore", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    public String patchNoticeRestore(@RequestParam(value = "index") int index) {
        Results results = this.noticeService.restoreNotice(index);
        JSONObject response = new JSONObject();
        response.put("result", results.nameToLower());
        return response.toString();
    }

    @RequestMapping(value = "/article-restore", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    public String patchArticleRestore(@SessionAttribute(value = "signedUser") UserEntity signedUser,@RequestParam(value = "index") int index) {
        Results results = this.articleService.restoreArticle(signedUser, index);
        JSONObject response = new JSONObject();
        response.put("result", results.nameToLower());
        return response.toString();
    }


    @RequestMapping(value = "/mypage/edit", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    public String patchMypageEdit(@RequestParam(value = "email") String email, @SessionAttribute(value = "signedUser", required = false) UserEntity signedUser, HttpSession session) {
        Results result = this.userService.retire(signedUser, email);
        session.removeAttribute("signedUser");
        JSONObject response = new JSONObject();
        response.put("result", result.nameToLower());
        return response.toString();
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET, produces = MediaType
            .APPLICATION_JSON_VALUE)
    public String getSearch(@SessionAttribute(value = "signedUser", required = false) UserEntity signedUser, SearchVo searchVo) {
        ResultTuple<UserEntity[]> result = this.userService.adminSearch(signedUser, searchVo);
        JSONObject response = new JSONObject();
        if (signedUser != null) {
            response.put("data", result.getPayload());
        }
        response.put("result", result.getResult().nameToLower());
        return response.toString();
    }

    @RequestMapping(value = "/article-delete", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteArticleDelete(@SessionAttribute(value = "signedUser") UserEntity signedUser, @RequestParam(value = "index") int index) {
        Results result = this.articleService.adminDelete(signedUser, index);
        JSONObject response = new JSONObject();
        response.put("result", result.nameToLower());
        return response.toString();
    }

}
