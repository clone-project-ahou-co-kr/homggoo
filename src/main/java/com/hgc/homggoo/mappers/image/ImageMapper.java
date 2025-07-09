package com.hgc.homggoo.mappers.image;

import com.hgc.homggoo.entities.images.ImageEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ImageMapper {
    int insert(@Param(value = "image") ImageEntity image);

    ImageEntity selectByIndex(@Param(value = "index") int index);
}
