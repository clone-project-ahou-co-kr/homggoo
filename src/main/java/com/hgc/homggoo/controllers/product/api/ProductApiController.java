package com.hgc.homggoo.controllers.product.api;

import com.hgc.homggoo.entities.product.ProductEntity;
import com.hgc.homggoo.entities.user.UserEntity;
import com.hgc.homggoo.results.CommonResult;
import com.hgc.homggoo.services.product.ProductService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(value = "/api/posts")
public class ProductApiController {
    private final ProductService productService;

    @Autowired
    public ProductApiController(ProductService productService) {
        this.productService = productService;
    }

    @RequestMapping(value = "/newProduct", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String postNewProduct(ProductEntity product,
                                 @RequestParam(value = "image",required = false) MultipartFile image,
                                 @SessionAttribute(value = "signedUser", required = false) UserEntity signedUser) throws IOException {

        CommonResult result = this.productService.createProduct(product, signedUser);
        JSONObject response = new JSONObject();
        response.put("result", result);
        if (result == CommonResult.SUCCESS) {
            response.put("id", product.getId());
        }
        return response.toString().toLowerCase();
    }

}
