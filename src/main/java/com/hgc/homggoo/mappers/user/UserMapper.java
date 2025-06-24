package com.hgc.homggoo.mappers.user;

import com.hgc.homggoo.entities.user.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
     int insert(@Param(value = "user") UserEntity user);

     int selectCountByEmail(@Param(value = "email") String email);

}
