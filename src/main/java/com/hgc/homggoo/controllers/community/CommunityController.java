package com.hgc.homggoo.controllers.community;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.swing.text.html.HTML;

@Controller
@RequestMapping(value = "community")
public class CommunityController {
    @RequestMapping(value = "interior", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getInterior() {
        return "/community/interior";
    }
}
