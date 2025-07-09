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
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/posts")
public class ProductApiController {
    private final ProductService productService;

    @Autowired
    public ProductApiController(ProductService productService) {
        this.productService = productService;
    }

    @RequestMapping(value = "/newProduct", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> postNewProduct(@RequestParam(value = "_image", required = false) MultipartFile image,
                                              ProductEntity product,
                                              @SessionAttribute(value = "signedUser", required = false) UserEntity signedUser) throws IOException {
        Map<String, Object> response = new HashMap<>();
        // 이미지 바이트를 엔티티에 설정
        if (!image.isEmpty()) {
            product.setImage(image.getBytes());
            System.out.println(image.getOriginalFilename());
        }
        CommonResult result = productService.createProduct(product, signedUser);

        if (result == CommonResult.SUCCESS) {
            response.put("result", "success");
            response.put("id", product.getId()); // 만약 DB에서 생성된 ID 반환하도록 되어 있다면
        } else {
            response.put("result", "failure");
        }

        return response;
    }

}
