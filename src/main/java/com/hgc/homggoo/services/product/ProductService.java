package com.hgc.homggoo.services.product;

import com.hgc.homggoo.entities.product.ProductEntity;
import com.hgc.homggoo.entities.user.UserEntity;
import com.hgc.homggoo.mappers.product.ProductMapper;
import com.hgc.homggoo.results.CommonResult;
import com.hgc.homggoo.services.user.UserService;
import com.hgc.homggoo.vos.ProductVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {
    private final ProductMapper productMapper;

    @Autowired
    public ProductService(UserService userService, ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    public ProductVo getById(int id) {
        if (id < 1) {
            return null;
        }
        return this.productMapper.selectById(id);
    }

    public CommonResult createProduct(ProductEntity product, UserEntity signedUser) throws IOException {
        UserEntity userEmail = this.productMapper.selectUserEmail(signedUser.getEmail());

        product.setUserEmail(userEmail.getEmail());
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return this.productMapper.insert(product) > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }

    public List<ProductVo> getAllProducts() {
        return this.productMapper.selectAll();
    }

    public CommonResult incrementView(ProductEntity product) {
        if (product == null) {
            System.out.println(1);
            return CommonResult.FAILURE;
        }
        if (product.getId() < 1){
            System.out.println(2);
            return CommonResult.FAILURE;
        }
        product.setViewCount(product.getViewCount() + 1);
        return this.productMapper.update(product) > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }
}
