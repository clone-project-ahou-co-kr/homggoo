package com.hgc.homggoo.controllers.user;

import com.hgc.homggoo.entities.notice.NoticeEntity;
import com.hgc.homggoo.entities.user.UserEntity;
import com.hgc.homggoo.results.ResultTuple;
import com.hgc.homggoo.services.notice.NoticeService;
import com.hgc.homggoo.services.oAuth.CustomOAuth2User;
import com.hgc.homggoo.services.user.UserService;
import com.hgc.homggoo.vos.NoticeVo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    private final NoticeService noticeService;
    private final UserService userService;

    public UserController(NoticeService noticeService, UserService userService) {
        this.noticeService = noticeService;
        this.userService = userService;
    }

    private List<NoticeVo> filterByBoardId(List<NoticeVo> list, String boardId) {
        return list.stream()
                .filter(vo -> boardId.equals(vo.getBoardId()))
                .toList();
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

    @RequestMapping(value = "/admin/login", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getAdminLogin() {
        return "user/adminLogin";
    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getAdmin(Model model) {
        ResultTuple<NoticeVo[]> result = this.noticeService.getAll();
        List<NoticeVo> all = Arrays.asList(result.getPayload());
        //todo 유저 회원수 가지고 오기 위해서 userService getall 추가.
        UserEntity[] userList = this.userService.getAll();


        // 게시판 종류별로 분리
        List<NoticeVo> noticeList = filterByBoardId(all, "notice");
        List<NoticeVo> boardList = filterByBoardId(all, "board");

        // 모델에 각각 저장
        model.addAttribute("noticeList", noticeList);
        model.addAttribute("noticeCount", noticeList.size());

        model.addAttribute("boardList", boardList);
        model.addAttribute("boardCount", boardList.size());

        model.addAttribute("userList", userList);
        model.addAttribute("userCount", userList.length);

        return "user/admin";
    }

    @RequestMapping(value = "/admin/modify", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getAdminModify(@RequestParam(value = "index")int index,Model model) {
        ResultTuple<NoticeVo> result = this.noticeService.getByIndex(index);
        NoticeVo notice = result.getPayload();
        model.addAttribute("notice", notice);
        return "user/modify";
    }

    @RequestMapping(value = "/admin/notice", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getAdminNotice() {

        return "user/notice";
    }

}
