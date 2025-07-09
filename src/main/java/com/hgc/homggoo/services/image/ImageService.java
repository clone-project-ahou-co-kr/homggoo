package com.hgc.homggoo.services.image;

import com.hgc.homggoo.entities.images.ImageEntity;
import com.hgc.homggoo.entities.notice.NoticeEntity;
import com.hgc.homggoo.entities.user.UserEntity;
import com.hgc.homggoo.mappers.image.ImageMapper;
import com.hgc.homggoo.results.CommonResult;
import com.hgc.homggoo.results.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ImageService {
    @Autowired
    private final ImageMapper imageMapper;

    public ImageService(ImageMapper imageMapper) {
        this.imageMapper = imageMapper;
    }

    public ImageEntity getByIndex(int index) {
        if (index < 1) {
            return null;
        }
        return this.imageMapper.selectByIndex(index);
    }

    public Results add(UserEntity signedUser, NoticeEntity notice, ImageEntity image) {
        System.out.println("image Service");

        if (signedUser == null) {
            return CommonResult.FAILURE_ABSENT;
        }
        if (image == null ||
                image.getName() == null ||
                image.getData() == null ||
                image.getData().length == 0 ||
                image.getContentType() == null) {
            return CommonResult.FAILURE;
        }
        System.out.println(image.getIndex());
        image.setNoticeIndex(null);
        image.setCreatedAt(LocalDateTime.now());

        return this.imageMapper.insert(image)>0?CommonResult.SUCCESS:CommonResult.FAILURE;
    }
}
