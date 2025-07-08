package com.hgc.homggoo.services.notice;

import com.hgc.homggoo.entities.notice.NoticeEntity;
import com.hgc.homggoo.entities.user.UserEntity;
import com.hgc.homggoo.mappers.notice.NoticeMapper;
import com.hgc.homggoo.mappers.user.UserMapper;
import com.hgc.homggoo.results.CommonResult;
import com.hgc.homggoo.results.ResultTuple;
import com.hgc.homggoo.results.Results;
import com.hgc.homggoo.vos.NoticeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NoticeService {
    private final NoticeMapper noticeMapper;
    private final UserMapper userMapper;

    @Autowired
    public NoticeService(NoticeMapper noticeMapper, UserMapper userMapper) {
        this.noticeMapper = noticeMapper;
        this.userMapper = userMapper;
    }

    public ResultTuple<NoticeVo> addNotice(UserEntity user, NoticeEntity notice) {
        if (user == null || notice == null) {
            return ResultTuple.<NoticeVo>builder()
                    .result(CommonResult.FAILURE_SESSION_EXPIRED).build();
        }
        UserEntity dbUser = this.userMapper.selectByEmail(user.getEmail());
        if (!dbUser.isAdmin()) {
            return ResultTuple.<NoticeVo>builder()
                    .result(CommonResult.FAILURE).build();
        }
        notice.setView(0);
        notice.setUserEmail(dbUser.getEmail());
        notice.setCreatedAt(LocalDateTime.now());
        notice.setModifiedAt(LocalDateTime.now());
        return this.noticeMapper.insert(notice) > 0 ? ResultTuple.<NoticeVo>builder().result(CommonResult.SUCCESS).build() : ResultTuple.<NoticeVo>builder().result(CommonResult.FAILURE).build();
    }

    public ResultTuple<NoticeVo[]> getAll() {
        NoticeVo[] dbNotice = this.noticeMapper.selectAll();
        return ResultTuple.<NoticeVo[]>builder()
                .result(CommonResult.SUCCESS).payload(dbNotice).build();
    }

    public ResultTuple<NoticeVo> getByIndex(int index) {
        if (index < 0) {
            return ResultTuple.<NoticeVo>builder().result(CommonResult.FAILURE).build();
        }
        NoticeVo dbNotice = this.noticeMapper.selectByIndex(index);
        if (dbNotice == null || dbNotice.isDeleted()) {
            return ResultTuple.<NoticeVo>builder()
                    .result(CommonResult.FAILURE_ABSENT).build();
        }
        return ResultTuple.<NoticeVo>builder().result(CommonResult.SUCCESS)
                .payload(dbNotice).build();

    }

    public Results incrementView(int index) {
        return this.noticeMapper.incrementView(index)>0?CommonResult.SUCCESS:CommonResult.FAILURE;
    }

    public Results modifyNotice(NoticeVo notice, String password) {
        if (notice == null || password == null) {
            return CommonResult.FAILURE;
        }
        NoticeVo dbNotice = this.noticeMapper.selectByIndex(notice.getIndex());
        if (dbNotice == null) {
            return CommonResult.FAILURE_ABSENT;
        }
        dbNotice.setTitle(notice.getTitle());
        dbNotice.setNickname(notice.getNickname());
        dbNotice.setUserEmail(notice.getUserEmail());
        dbNotice.setContent(notice.getContent());
        dbNotice.setModifiedAt(LocalDateTime.now());
        dbNotice.setDeleted(false);
        return this.noticeMapper.update(dbNotice) > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }

    public Results deleteNotice(int index) {
        if (index < 1) {
            return CommonResult.FAILURE;
        }
        NoticeVo dbNotice = this.noticeMapper.selectByIndex(index);
        if (dbNotice == null) {
            return CommonResult.FAILURE_ABSENT;
        }
        dbNotice.setDeleted(true);
        return this.noticeMapper.update(dbNotice) > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }

}
