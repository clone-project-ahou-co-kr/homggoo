package com.hgc.homggoo.controllers.user;

import com.hgc.homggoo.entities.user.EmailTokenEntity;
import com.hgc.homggoo.entities.user.UserEntity;
import com.hgc.homggoo.results.CommonResult;
import com.hgc.homggoo.results.ResultTuple;
import com.hgc.homggoo.results.Results;
import com.hgc.homggoo.services.user.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getRegister() {
        return "/user/register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postRegister(UserEntity user) {
        Results result = this.userService.register(user);
        JSONObject response = new JSONObject();
        response.put("result", result.nameToLower());
        return response.toString();
    }

    @RequestMapping(value = "/register-email",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postRegisterEmail(@RequestParam(value="email",required = false)String email, HttpServletRequest request) throws MessagingException {
        String userAgent = request.getHeader("User-Agent");
        ResultTuple<EmailTokenEntity> result = this.userService.sendRegisterEmail(email, userAgent);
        JSONObject response = new JSONObject();
        response.put("result", result.getResult().toString());
        if (result.getResult() == CommonResult.SUCCESS) {
            response.put("salt", result.getPayload().getSalt());
        }
        return response.toString();
    }

}
