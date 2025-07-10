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

    UserEntity selectUserEmail(@Param(value = "signedUser")String signedUser);

    ProductVo selectById(@Param(value = "id") int id);

    List<ProductVo> selectAll();

    int countByUserEmail(@Param("userEmail") String userEmail);
}
