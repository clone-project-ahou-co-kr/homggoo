package com.hgc.homggoo.mappers.user;

import com.hgc.homggoo.entities.user.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
     int insert(@Param(value = "user") UserEntity user);

     int selectCountByEmail(@Param(value = "email") String email);

     UserEntity selectByEmail(@Param(value = "email") String email);

     UserEntity selectByProviderAndEmail(@Param(value = "providerKey") String providerKey,@Param(value = "providerType") String providerType,@Param(value="email")String email);

     int update(@Param(value = "user") UserEntity user);
}
