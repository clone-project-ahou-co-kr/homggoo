package com.hgc.homggoo.controllers.experts.api;

import com.hgc.homggoo.results.ResultTuple;
import com.hgc.homggoo.results.Results;
import com.hgc.homggoo.services.notice.NoticeService;
import com.hgc.homggoo.vos.NoticeVo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.print.attribute.standard.Media;

@RestController
@RequestMapping(value = "/api/expert")
public class ExpertApiController {
    @Autowired
    private final NoticeService noticeService;

    public ExpertApiController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @RequestMapping(value = "/notice", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getNotice() {
        ResultTuple<NoticeVo[]> results = this.noticeService.getAll();
        JSONObject response = new JSONObject();
        response.put("result", results.getResult());
        response.put("notices", results.getPayload());
        return response.toString();
    }

    @RequestMapping(value = "/notice", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String postNotice(@RequestParam(value = "index") int index) {
        Results results = this.noticeService.incrementView(index);
        JSONObject response = new JSONObject();
        response.put("result", results.nameToLower());
        return response.toString();
    }


}
