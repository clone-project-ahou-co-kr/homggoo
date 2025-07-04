package com.hgc.homggoo.controllers;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/product")
public class ProductController {
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getIndexProduct() {
        return "product/index";
    }

    @RequestMapping(value = "/category", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getCategory() {
        return "product/category";
    }

    @RequestMapping(value = "/production", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getproduction() {
        return "product/production";
    }
}
