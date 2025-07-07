package com.hgc.homggoo.controllers.user.api;

import com.hgc.homggoo.entities.notice.NoticeEntity;
import com.hgc.homggoo.entities.user.EmailTokenEntity;
import com.hgc.homggoo.entities.user.UserEntity;
import com.hgc.homggoo.results.CommonResult;
import com.hgc.homggoo.results.ResultTuple;
import com.hgc.homggoo.results.Results;
import com.hgc.homggoo.services.notice.NoticeService;
import com.hgc.homggoo.services.user.EmailTokenService;
import com.hgc.homggoo.services.user.UserService;
import com.hgc.homggoo.vos.NoticeVo;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.ibatis.annotations.Param;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/user")
public class UserApiController {
    private final UserService userService;
    private final EmailTokenService emailTokenService;
    private final NoticeService noticeService;

    @Autowired
    public UserApiController(UserService userService, EmailTokenService emailTokenService, NoticeService noticeService) {
        this.userService = userService;
        this.emailTokenService = emailTokenService;
        this.noticeService = noticeService;
    }


    @RequestMapping(value = "/profile-image", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getProfileImage(@SessionAttribute("signedUser") UserEntity signedUser) {
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
        ResultTuple<EmailTokenEntity> result = this.userService.sendRegisterEmail(email, userAgent);
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
        JSONObject response = new JSONObject();
        response.put("result", result.getResult().nameToLower());
        response.put("data", result.getPayload());
        return response.toString();
    }

    @RequestMapping(value = "/modify", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    public String patchNotice(NoticeVo notice, @RequestParam(value = "index") int index, @RequestParam(value = "password") String password) {
        Results result = this.noticeService.modifyNotice(notice, password);
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

    @RequestMapping(value = "/notice", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteNotice(@RequestParam(value = "index")int index) {
        Results result = this.noticeService.deleteNotice(index);
        JSONObject response = new JSONObject();
        response.put("result", result.nameToLower());
        return response.toString();
    }
}
