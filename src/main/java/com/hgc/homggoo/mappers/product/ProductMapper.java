package com.hgc.homggoo.mappers.product;

import com.hgc.homggoo.entities.product.ProductEntity;
import com.hgc.homggoo.entities.user.UserEntity;
import com.hgc.homggoo.vos.ProductVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {
    int insert(@Param(value = "product")ProductEntity product);

    int update(@Param(value = "product")ProductEntity product);

    int delete(@Param(value = "product")ProductEntity product);

    UserEntity selectUserEmail(@Param(value = "signedUser")String signedUser);

    ProductVo selectById(@Param(value = "id") int id);

    ProductVo selectByIdAndCategory(@Param(value = "id") int id,
            @Param(value = "category") String category);

    List<ProductVo> selectAll();

    List<ProductVo> selectByCategory(@Param("category") String category);

    int countByUserEmail(@Param("userEmail") String userEmail);
}
