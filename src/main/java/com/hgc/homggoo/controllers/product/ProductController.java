package com.hgc.homggoo.controllers.product;

import com.hgc.homggoo.services.product.ProductService;
import com.hgc.homggoo.vos.ProductVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String getIndexProduct() {
        return "product/index";
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getAll(Model model) {
        List<ProductVo> products = this.productService.getAllProducts();
        model.addAttribute("products", products);
        return "product/allProduction";
    }

    @RequestMapping(value = "/production", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getProduction(@RequestParam(value = "id", required = false) Integer id,
                                Model model) {
        ProductVo productId = this.productService.getById(id);
        if (productId != null) {
            this.productService.incrementView(productId);
        }
        model.addAttribute("product", productId);
        return "product/production";
    }

    @RequestMapping(value = "/newProduct", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getNewProduct() {
        return "product/newProduct";
    }
}
