package com.hgc.homggoo.controllers.product;

import com.hgc.homggoo.entities.product.ProductEntity;
import com.hgc.homggoo.entities.user.UserEntity;
import com.hgc.homggoo.services.product.ProductService;
import com.hgc.homggoo.vos.ProductVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequestMapping(value = "/product")
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getIndexProduct(@SessionAttribute(value = "signedUser", required = false) UserEntity signedUser,
                                  Model model) {
        List<ProductVo> products = this.productService.getAllProducts();

        model.addAttribute("products", products);
        return "product/index";
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getAll(@RequestParam(value = "category", required = false) String category,
                         @SessionAttribute(value = "signedUser", required = false) UserEntity signedUser,
                         Model model) {
        List<ProductVo> products;

        if (category == null || category.isBlank()) {
            products = this.productService.getAllProducts();
        } else {
            products = this.productService.getProductsByCategory(category);
        }
        model.addAttribute("products", products);
        model.addAttribute("category", category);
        return "product/allProduction";
    }

    @RequestMapping(value = "/production", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getProduction(@RequestParam(value = "category", required = false) String category,
                                @RequestParam(value = "id", required = false) int id,
                                @SessionAttribute(value = "signedUser", required = false) UserEntity signedUser,
                                Model model) {
        ProductVo productId = this.productService.getByIdAndCategory(id, category);

        if (signedUser != null) {
            boolean liked = this.productService.isLikedByUser(id, signedUser.getEmail());
            UserEntity signed = this.productService.getUserEmail(signedUser);
            List<ProductVo> products = this.productService.selectByUserEmail(signedUser.getEmail());

            if (productId != null) {
                this.productService.incrementView(productId);
            }
            model.addAttribute("productCount", products);
            model.addAttribute("user", signed);
            model.addAttribute("liked", liked);
        } else {
            model.addAttribute("signedUserEmail", null);
        }
        model.addAttribute("product", productId);
        int likeCount = this.productService.getLikeCount(id);
        model.addAttribute("likeCount", likeCount);
        return "product/production";
    }

    @RequestMapping(value = "/modifyProduct", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getProductionModify(@RequestParam(value = "id", required = false) int id,
                                      @SessionAttribute(value = "signedUser", required = false) UserEntity signedUser,
                                      Model model) {
        ProductVo productId = this.productService.getById(id);
        UserEntity signed = this.productService.getUserEmail(signedUser);
        model.addAttribute("user", signed);
        model.addAttribute("product", productId);
        return "product/modifyProduct";
    }

    @RequestMapping(value = "/image", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImage(@RequestParam(value = "id", required = false) int id) {
        ProductEntity product = this.productService.getById(id);
        if (product.getImage() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=")
                .contentLength(product.getImage().length)
                .body(product.getImage());
    }

    @RequestMapping(value = "/newProduct", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getNewProduct(@SessionAttribute(value = "signedUser")UserEntity signedUser,
                                Model model) {
        UserEntity signed = this.productService.getUserEmail(signedUser);
        model.addAttribute("user", signed);
        return "product/newProduct";
    }
}
