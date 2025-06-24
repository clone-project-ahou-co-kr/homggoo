package com.hgc.homggoo.mappers.user;

import com.hgc.homggoo.entities.user.EmailTokenEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EmailTokenMapper {
    int insert(@Param(value = "emailtoken") EmailTokenEntity emailToken);
}
