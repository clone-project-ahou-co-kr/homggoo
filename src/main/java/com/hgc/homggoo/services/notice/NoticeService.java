package com.hgc.homggoo.services.notice;

import com.hgc.homggoo.entities.notice.NoticeEntity;
import com.hgc.homggoo.entities.user.UserEntity;
import com.hgc.homggoo.mappers.notice.NoticeMapper;
import com.hgc.homggoo.mappers.user.UserMapper;
import com.hgc.homggoo.results.CommonResult;
import com.hgc.homggoo.results.ResultTuple;
import com.hgc.homggoo.results.Results;
import com.hgc.homggoo.utils.Bcrypt;
import com.hgc.homggoo.vos.NoticeVo;
import com.hgc.homggoo.vos.SearchVo;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.xml.transform.Result;
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
        UserEntity dbUser = this.userMapper.selectLocalUserEmail(user.getEmail());
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

    public ResultTuple<NoticeVo[]> getAllExceptDelete() {
        NoticeVo[] dbNotice = this.noticeMapper.selectAllExceptDeleted();
        return ResultTuple.<NoticeVo[]>builder()
                .result(CommonResult.SUCCESS).payload(dbNotice).build();
    }

    public ResultTuple<NoticeVo> getByIndex(int index) {
        if (index < 0) {
            return ResultTuple.<NoticeVo>builder()
                    .result(CommonResult.FAILURE)
                    .build();
        }

        NoticeVo dbNotice = this.noticeMapper.selectByIndex(index);

        if (dbNotice == null) {
            return ResultTuple.<NoticeVo>builder()
                    .result(CommonResult.FAILURE_ABSENT)
                    .build();
        }

        // ✅ 삭제된 경우도 payload는 전달, result는 SUCCESS 유지
        return ResultTuple.<NoticeVo>builder()
                .result(CommonResult.SUCCESS)
                .payload(dbNotice)
                .build();
    }


    public Results incrementView(int index) {
        return this.noticeMapper.incrementView(index) > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }

    public Results restoreNotice(int index) {
        if (index < 1) {
            return CommonResult.FAILURE;
        }
        NoticeVo dbNotice = this.noticeMapper.selectByIndex(index);
        if (dbNotice == null) {
            return CommonResult.FAILURE_ABSENT;
        }
        dbNotice.setModifiedAt(LocalDateTime.now());
        dbNotice.setDeleted(false);
        return this.noticeMapper.update(dbNotice) > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }

    public Results modifyNotice(NoticeVo notice, String password, UserEntity signedUser) {
        if (notice == null || password == null || signedUser == null) {
            return CommonResult.FAILURE;
        }
        NoticeVo dbNotice = this.noticeMapper.selectByIndex(notice.getIndex());
        if (dbNotice == null) {
            return CommonResult.FAILURE_ABSENT;
        }
        UserEntity dbUser = this.userMapper.selectByEmail(signedUser.getEmail());
        if (dbUser == null) {
            return CommonResult.FAILURE;
        }
        if(!dbUser.isAdmin()){
            return CommonResult.FAILURE_SESSION_EXPIRED;
        }
        if (!Bcrypt.isMatch(password, signedUser.getPassword())) {
            return CommonResult.FAILURE_ADMIN;
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

    public ResultTuple<NoticeVo[]> searchNotice(SearchVo searchVo) {
        if (searchVo.getBy() == null || searchVo.getKeyword() == null) {
            return ResultTuple.<NoticeVo[]>builder()
                    .result(CommonResult.FAILURE).build();
        }

        NoticeVo[] dbNoticeVo = this.noticeMapper.selectBySearch(searchVo);
        if (dbNoticeVo == null||dbNoticeVo.length == 0) {
            return ResultTuple.<NoticeVo[]>builder()
                    .result(CommonResult.FAILURE_ABSENT).build();
        }
        return ResultTuple.<NoticeVo[]>builder()
                .result(CommonResult.SUCCESS)
                .payload(dbNoticeVo).build();
    }

}
