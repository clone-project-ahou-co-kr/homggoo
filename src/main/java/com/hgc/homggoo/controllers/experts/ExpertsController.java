package com.hgc.homggoo.controllers.experts;

import com.hgc.homggoo.results.ResultTuple;
import com.hgc.homggoo.services.notice.NoticeService;
import com.hgc.homggoo.vos.NoticeVo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/experts")
public class ExpertsController {
    @Autowired
    private final NoticeService noticeService;

    public ExpertsController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getIndex(){
        return "experts/expert";
    }


    @RequestMapping(value = "notice/specific", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getNoticeSpec(@RequestParam(value = "index") int index, Model model) {
        ResultTuple<NoticeVo> notice = this.noticeService.getByIndex(index);
        model.addAttribute("notice",notice.getPayload());
        return "user/noticeSpecific";
    }
}
