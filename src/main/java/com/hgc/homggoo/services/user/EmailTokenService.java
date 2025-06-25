package com.hgc.homggoo.services.user;

import com.hgc.homggoo.entities.user.EmailTokenEntity;
import com.hgc.homggoo.mappers.user.EmailTokenMapper;
import com.hgc.homggoo.results.CommonResult;
import com.hgc.homggoo.results.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmailTokenService {
    private final EmailTokenMapper emailTokenMapper;

    @Autowired
    public EmailTokenService(EmailTokenMapper emailTokenMapper) {
        this.emailTokenMapper = emailTokenMapper;
    }

    public Results verifyEmailToken(EmailTokenEntity emailToken) {
        if (emailToken == null) {
            return CommonResult.FAILURE;
        }
        EmailTokenEntity dbEmailToken = this.emailTokenMapper.selectByEmailAndCodeAndSalt(emailToken.getEmail(), emailToken.getCode(), emailToken.getSalt());
        if (dbEmailToken == null && !emailToken.isUsed()) {
            return CommonResult.FAILURE;
        }
        if (dbEmailToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            return CommonResult.FAILURE;
        }
        if (!dbEmailToken.getUserAgent().equals(emailToken.getUserAgent())) {
            return CommonResult.FAILURE;
        }
        dbEmailToken.setUsed(true);
        return this.emailTokenMapper.update(dbEmailToken) > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }
}
