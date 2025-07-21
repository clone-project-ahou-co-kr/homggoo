package com.hgc.homggoo.mappers.notice;

import com.hgc.homggoo.entities.notice.NoticeEntity;
import com.hgc.homggoo.entities.user.UserEntity;
import com.hgc.homggoo.vos.NoticeVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NoticeMapper {
    int insert(@Param(value = "notice") NoticeEntity notice);

    NoticeVo[] selectAll();

    NoticeVo selectByIndex(@Param(value = "index") int index);

    int update(@Param(value = "notice") NoticeEntity notice);

    int incrementView(@Param(value = "index") int index);


}
