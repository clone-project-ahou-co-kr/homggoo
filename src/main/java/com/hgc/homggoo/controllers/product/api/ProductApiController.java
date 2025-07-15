package com.hgc.homggoo.controllers.product.api;

import com.hgc.homggoo.entities.product.ProductEntity;
import com.hgc.homggoo.entities.product.ProductUserLikeEntity;
import com.hgc.homggoo.entities.user.UserEntity;
import com.hgc.homggoo.results.CommonResult;
import com.hgc.homggoo.services.product.ProductService;
import com.hgc.homggoo.vos.ProductVo;
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
        if (!image.isEmpty()) {
            product.setImage(image.getBytes());
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

    @RequestMapping(value = "/modifyProduct", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> postModifyProduct(@SessionAttribute(value = "signedUser", required = false) UserEntity signedUser,
                                                 @RequestParam(value = "_image", required = false) MultipartFile image,
                                                 ProductEntity product) throws IOException {
        Map<String, Object> response = new HashMap<>();

        if (image != null && !image.isEmpty()) {
            product.setImage(image.getBytes());
        } else {
            ProductVo already = productService.getById(product.getId());
            product.setImage(already.getImage());
        }

        CommonResult result = productService.updateProduct(product, signedUser);

        if (result == CommonResult.SUCCESS) {
            response.put("result", "success");
            response.put("id", product.getId());
        } else {
            response.put("result", "failure");
        }
        return response;
    }

    @RequestMapping(value = "/deleteProduct", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteProduct (@SessionAttribute(value = "signedUser", required = false) UserEntity signedUser,
                                 ProductEntity product) {
        CommonResult result = this.productService.deleteProduct(product, signedUser);
        JSONObject response = new JSONObject();
        response.put("result", result);
        return response.toString().toLowerCase();
    }

    @RequestMapping(value = "/productLike", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    public String patchProductUserLike (@SessionAttribute(value = "signedUser", required = false) UserEntity signedUser,
                                        @RequestParam(value = "id")int id) {
        Boolean result = this.productService.productUserLike(signedUser, id);
        JSONObject response = new JSONObject();
        if (result == null) {
            response.put("result", CommonResult.FAILURE.nameToLower());
        } else {
            response.put("result", result);
        }
        return response.toString();
    }
}
