package com.hgc.homggoo.mappers.user;

import com.hgc.homggoo.entities.user.EmailTokenEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EmailTokenMapper {
    int insert(@Param(value = "emailtoken") EmailTokenEntity emailToken);

    EmailTokenEntity selectByEmailAndCodeAndSalt(@Param(value = "email") String email, @Param(value = "code") String code, @Param(value = "salt") String salt);

    int update(@Param(value = "emailToken") EmailTokenEntity emailToken);
}
