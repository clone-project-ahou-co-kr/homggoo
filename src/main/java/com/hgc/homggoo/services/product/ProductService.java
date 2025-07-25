package com.hgc.homggoo.services.product;

import com.hgc.homggoo.entities.product.ProductEntity;
import com.hgc.homggoo.entities.product.ProductUserLikeEntity;
import com.hgc.homggoo.entities.user.UserEntity;
import com.hgc.homggoo.mappers.product.ProductMapper;
import com.hgc.homggoo.mappers.product.ProductUserLikeMapper;
import com.hgc.homggoo.results.CommonResult;
import com.hgc.homggoo.services.user.UserService;
import com.hgc.homggoo.vos.ProductBuyVo;
import com.hgc.homggoo.vos.ProductVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {
    private final ProductMapper productMapper;
    private final ProductUserLikeMapper productUserLikeMapper;

    @Autowired
    public ProductService(UserService userService, ProductMapper productMapper, ProductUserLikeMapper productUserLikeMapper) {
        this.productMapper = productMapper;
        this.productUserLikeMapper = productUserLikeMapper;
    }

    public ProductVo getById(int id) {
        if (id < 1) {
            return null;
        }
        return this.productMapper.selectById(id);
    }

    public List<ProductVo> selectByUserEmail(String email) {
        return this.productMapper.selectByUserEmail(email);
    }

    public List<ProductVo> getLikedProductsByUser(String userEmail) {
        return this.productMapper.selectLikedProductsByUserEmail(userEmail);
    }

    public List<ProductBuyVo> selectOrderProduct(UserEntity signedUser) {
        return this.productMapper.selectBuyProductsByUserEmail(signedUser.getEmail());
    }

    public int getIsNotSold(String categoryCode) {
        return this.productMapper.selectCountByIsNotSold(categoryCode);
    }

    public ProductVo getByIdAndCategory(int id, String category) {
        if (id < 1 || category == null) {
            return null;
        }
        return this.productMapper.selectByIdAndCategory(id, category);
    }

    public CommonResult createProduct(ProductEntity product, UserEntity signedUser) throws IOException {
        if (signedUser == null || signedUser.isDeleted()) {
            return CommonResult.FAILURE_ABSENT;
        }
        UserEntity userEmail = this.productMapper.selectUserEmail(signedUser);

        product.setUserEmail(userEmail.getEmail());
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return this.productMapper.insert(product) > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }

    public CommonResult updateProduct(ProductEntity product, UserEntity signedUser) {

        if (signedUser == null || signedUser.isDeleted()) {
            return CommonResult.FAILURE_ABSENT;
        }
        if (!signedUser.getEmail().equals(product.getUserEmail())) {
            return CommonResult.FAILURE_UNAUTHORIZED;
        }
        UserEntity userEmail = this.productMapper.selectUserEmail(signedUser);

        product.setUserEmail(userEmail.getEmail());
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return this.productMapper.update(product) > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }

    public CommonResult buyProduct(ProductEntity product, UserEntity signedUser) {
        if (signedUser == null || signedUser.isDeleted()) {
            return CommonResult.FAILURE_ABSENT;
        }

        product.setSold(true);
        product.setUpdatedAt(LocalDateTime.now());

        int updated = this.productMapper.updateBuyProduct(product);
        if (updated > 0) {
            this.productMapper.insertProductOrder(product, signedUser);
            return CommonResult.SUCCESS;
        }
        return CommonResult.FAILURE;
    }

    public CommonResult deleteProduct(ProductEntity product, UserEntity signedUser) {
        if (signedUser == null || signedUser.isDeleted()) {
            return CommonResult.FAILURE_ABSENT;
        }
        if (!signedUser.getEmail().equals(product.getUserEmail())) {
            return CommonResult.FAILURE_UNAUTHORIZED;
        }
        return this.productMapper.delete(product) > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }

    public List<ProductVo> getAllProducts() {
        return this.productMapper.selectAll();
    }

    public List<ProductVo> getProductsByCategory(String category) {
        return this.productMapper.selectByCategory(category);
    }

    public UserEntity getUserEmail(UserEntity signedUser) {
        return this.productMapper.selectUserEmail(signedUser);
    }

    public CommonResult incrementView(ProductEntity product) {
        if (product == null) {
            return CommonResult.FAILURE;
        }
        if (product.getId() < 1) {
            return CommonResult.FAILURE;
        }
        product.setViewCount(product.getViewCount() + 1);
        return this.productMapper.update(product) > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }

    public int getLikeCount(int productId) {
        return this.productUserLikeMapper.countByProductId(productId);
    }
    public boolean isLikedByUser(int productId, String userEmail) {
        return this.productUserLikeMapper.selectProductIndexAndUserEmail(productId, userEmail) != null;
    }

    public Boolean productUserLike(UserEntity signedUser, int id) {
        if (signedUser == null || signedUser.isDeleted()) {
            return null;
        }
        ProductEntity dbProduct = this.productMapper.selectById(id);
        if (dbProduct == null) {
            return null;
        }
        if (this.productUserLikeMapper.selectProductIndexAndUserEmail(id, signedUser.getEmail()) == null) {
            ProductUserLikeEntity productUserLike = ProductUserLikeEntity.builder()
                    .productId(id)
                    .UserEmail(signedUser.getEmail())
                    .createdAt(LocalDateTime.now())
                    .build();
            return this.productUserLikeMapper.insertProductLike(productUserLike) > 0 ? true : null;
        } else {
            return this.productUserLikeMapper.delete(id, signedUser.getEmail()) > 0 ? false : null;
        }
    }
}
