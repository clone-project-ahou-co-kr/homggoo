package com.hgc.homggoo.mappers.user;

import com.hgc.homggoo.entities.user.UserEntity;
import com.hgc.homggoo.vos.SearchVo;
import org.apache.catalina.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Property;

@Mapper
public interface UserMapper {
    int insert(@Param(value = "user") UserEntity user);

    int selectCountByEmail(@Param(value = "email") String email);

    UserEntity selectByProviderAndEmail(@Param(value = "providerType") String providerType,
                                        @Param(value = "email") String email);

    int update(@Param(value = "user") UserEntity user);

    UserEntity[] selectAll();

    UserEntity selectLocalUserEmail(@Param(value = "email") String email);

    UserEntity selectByEmailAndProviderType(@Param("email") String email,
                                            @Param("providerType") String providerType);

    UserEntity selectByEmail(@Param(value = "email") String email);

    UserEntity[] selectBySearch(@Param(value = "searchVo") SearchVo searchVo);

}
