package com.hgc.homggoo.mappers.product;

import com.hgc.homggoo.entities.product.ProductUserLikeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

@Mapper
public interface ProductUserLikeMapper {
    int insertProductLike(@Param(value = "productLike") ProductUserLikeEntity productLike);

    int delete(@Param(value = "id")int id,
               @Param(value = "userEmail")String userEmail);

    int countByProductId(@Param(value = "id") int id);

    ProductUserLikeEntity selectProductIndexAndUserEmail(@Param(value = "id")int id,
                                                         @Param(value = "userEmail")String userEmail);
}
