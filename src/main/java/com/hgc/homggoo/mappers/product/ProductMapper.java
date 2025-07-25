package com.hgc.homggoo.mappers.product;

import com.hgc.homggoo.entities.product.ProductEntity;
import com.hgc.homggoo.entities.user.UserEntity;
import com.hgc.homggoo.vos.ProductVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

@Mapper
public interface ProductMapper {
    int insert(@Param(value = "product")ProductEntity product);

    int insertProductOrder(@Param(value = "product")ProductEntity product,
                           @Param(value = "signedUser")UserEntity signedUser);

    int update(@Param(value = "product")ProductEntity product);

    int updateBuyProduct(@Param(value = "product")ProductEntity product);

    int delete(@Param(value = "product")ProductEntity product);

    UserEntity selectUserEmail(@Param(value = "signedUser")UserEntity signedUser);

    ProductVo selectById(@Param(value = "id") int id);

    ProductVo selectByIdAndCategory(@Param(value = "id") int id,
            @Param(value = "category") String category);

    List<ProductVo> selectByUserEmail(@Param(value = "userEmail") String userEmail);

    List<ProductVo> selectAll();

    List<ProductVo> selectByCategory(@Param("category") String category);

    List<ProductVo> selectLikedProductsByUserEmail(@Param("userEmail") String userEmail);

}
