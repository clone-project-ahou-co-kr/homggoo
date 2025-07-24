package com.hgc.homggoo.services.user;

import com.hgc.homggoo.entities.user.EmailTokenEntity;
import com.hgc.homggoo.entities.user.UserEntity;
import com.hgc.homggoo.mappers.user.EmailTokenMapper;
import com.hgc.homggoo.mappers.user.UserMapper;
import com.hgc.homggoo.regexes.UserRegex;
import com.hgc.homggoo.results.CommonResult;
import com.hgc.homggoo.results.ResultTuple;
import com.hgc.homggoo.results.Results;
import com.hgc.homggoo.utils.Bcrypt;
import com.hgc.homggoo.vos.SearchVo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.apache.catalina.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.client.RestClient;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final RestClient.Builder builder;

    private static EmailTokenEntity generateEmailToken(String email, String userAgent, int expMin) {
        String code = RandomStringUtils.randomNumeric(6);// 000000 - 999999 까지 준다. -> 반환타입은 String
        String salt = RandomStringUtils.randomAlphabetic(128);//a~z+ A~Z +0-9 -> 랜덤하게 128개를 반환한다.
        return UserService.generateEmailToken(email, userAgent, code, salt, expMin);
    }

    private static EmailTokenEntity generateEmailToken(String email, String userAgent, String code, String salt, int expMin) {
        EmailTokenEntity emailToken = new EmailTokenEntity();
        emailToken.setEmail(email);
        emailToken.setCode(code);
        emailToken.setSalt(salt);
        emailToken.setUserAgent(userAgent);
        emailToken.setUsed(false);
        emailToken.setCreateAt(LocalDateTime.now());
        emailToken.setExpiresAt(LocalDateTime.now().plusMinutes(expMin));
        //                          ⬆현재 시간에서        ⬆10분 추가(미래로)
        // > 현재 시간보다 3분 뒤
        return emailToken;
    }

    private final UserMapper userMapper;
    private final EmailTokenMapper emailTokenMapper;
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine springTemplateEngine;

    @Autowired
    public UserService(UserMapper userMapper, EmailTokenMapper emailTokenMapper, JavaMailSender javaMailSender, SpringTemplateEngine springTemplateEngine, RestClient.Builder builder) {
        this.userMapper = userMapper;
        this.emailTokenMapper = emailTokenMapper;
        this.javaMailSender = javaMailSender;
        this.springTemplateEngine = springTemplateEngine;
        this.builder = builder;
    }

    public Results isPasswordAvailable(String password) {
        if (!UserRegex.password.matches(password)) {
            return CommonResult.FAILURE;
        }
        return CommonResult.SUCCESS;
    }

    public Results isNicknameAvailable(String nickname) {
        if (!UserRegex.nickname.matches(nickname)) {
            return CommonResult.FAILURE;
        }
        return CommonResult.SUCCESS;
    }

    public Results isEmailAvailable(String email) {
        if (!UserRegex.email.matches(email)) {
            return CommonResult.FAILURE;
        }
        return CommonResult.SUCCESS;
    }


    //sendEmail=> provider 가 필요없다.
    public ResultTuple<EmailTokenEntity> sendEmail(String email, String userAgent) throws MessagingException {
        if (userAgent == null) {
            return ResultTuple.<EmailTokenEntity>builder()
                    .result(CommonResult.FAILURE).build();
        }
        int dbUser = this.userMapper.selectCountByEmail(email);
        if (dbUser > 0) {
            return ResultTuple.<EmailTokenEntity>builder()
                    .result(CommonResult.FAILURE)
                    .build();
        }
        if (this.userMapper.selectCountByEmail(email) > 0) {
            return ResultTuple.<EmailTokenEntity>builder()
                    .result(CommonResult.FAILURE_DUPLICATE).build();
        }

        EmailTokenEntity emailToken = UserService.generateEmailToken(email, userAgent, 3);
        if (this.emailTokenMapper.insert(emailToken) < 1) {
            return ResultTuple.<EmailTokenEntity>builder()
                    .result(CommonResult.FAILURE).build();
        }
        Context context = new Context();
        context.setVariable("code", emailToken.getCode());
        String mailText = this.springTemplateEngine.process("user/registerEmail", context);
        MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setFrom("hyeongyu98@gmail.com");
        mimeMessageHelper.setTo(emailToken.getEmail());
        mimeMessageHelper.setSubject("오늘의집 인증번호안내");
        mimeMessageHelper.setText(mailText, true);
        this.javaMailSender.send(mimeMessage);
        return ResultTuple.<EmailTokenEntity>builder()
                .result(CommonResult.SUCCESS)
                .payload(emailToken).build();
    }

    public ResultTuple<EmailTokenEntity> sendRetireEmail(String email, String userAgent) throws MessagingException {
        if (userAgent == null) {
            return ResultTuple.<EmailTokenEntity>builder()
                    .result(CommonResult.FAILURE).build();
        }
        UserEntity dbUser = this.userMapper.selectByEmail(email);
        if (dbUser == null) {
            return ResultTuple.<EmailTokenEntity>builder()
                    .result(CommonResult.FAILURE_ABSENT)
                    .build();
        }
        EmailTokenEntity emailToken = UserService.generateEmailToken(email, userAgent, 3);
        if (this.emailTokenMapper.insert(emailToken) < 1) {
            return ResultTuple.<EmailTokenEntity>builder()
                    .result(CommonResult.FAILURE_UNAUTHORIZED).build();
        }
        Context context = new Context();
        context.setVariable("code", emailToken.getCode());
        String mailText = this.springTemplateEngine.process("user/registerEmail", context);
        MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setFrom("hyeongyu98@gmail.com");
        mimeMessageHelper.setTo(emailToken.getEmail());
        mimeMessageHelper.setSubject("homggoo 인증번호 안내");
        mimeMessageHelper.setText(mailText, true);
        this.javaMailSender.send(mimeMessage);
        return ResultTuple.<EmailTokenEntity>builder()
                .result(CommonResult.SUCCESS)
                .payload(emailToken).build();
    }

    public ResultTuple<UserEntity> login(String email, String password) {
        if (email == null || password == null) {
            return ResultTuple.<UserEntity>builder()
                    .result(CommonResult.FAILURE)
                    .build();
        }
        UserEntity dbUser = this.userMapper.selectLocalUserEmail(email);
        if (dbUser == null) {
            return ResultTuple.<UserEntity>builder()
                    .result(CommonResult.FAILURE_SESSION_EXPIRED)
                    .build();
        }
        if (dbUser.isDeleted()) {
            System.out.println("isdeleted");
            return ResultTuple.<UserEntity>builder()
                    .result(CommonResult.FAILURE_ABSENT)
                    .build();
        }
        if (!Bcrypt.isMatch(password, dbUser.getPassword())) {
            return ResultTuple.<UserEntity>builder()
                    .result(CommonResult.FAILURE).build();
        }
        return ResultTuple.<UserEntity>builder()
                .result(CommonResult.SUCCESS)
                .payload(dbUser).build();
    }

    public Results register(UserEntity user) {
        if (user == null || user.getNickname() == null || user.getNickname().trim().isEmpty()) {
            return CommonResult.FAILURE;
        }
        UserEntity dbUser = this.userMapper.selectByProviderAndEmail(user.getProviderType(), user.getEmail());
        if (this.userMapper.selectCountByEmail(user.getEmail()) > 0) {
            return CommonResult.FAILURE_DUPLICATE;
        }
        user.setAdmin(false);
        user.setDeleted(false);
        user.setPassword(Bcrypt.encrypt(user.getPassword()));
        user.setProfile(new byte[1]); // 테스트용 기본값
        user.setCreatedAt(LocalDateTime.now());
        user.setModifiedAt(LocalDateTime.now());
        user.setProviderType("ORIGIN"); // ENUM과 일치하도록 대문자
        user.setProviderKey(null);
        return this.userMapper.insert(user) > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }


    public ResultTuple<UserEntity> adminLogin(String email, String password) {
        if (email == null || password == null) {
            return ResultTuple.<UserEntity>builder()
                    .result(CommonResult.FAILURE).build();
        }
        UserEntity dbUser = this.userMapper.selectLocalUserEmail(email);
        if (!dbUser.isAdmin()) {
            return ResultTuple.<UserEntity>builder()
                    .result(CommonResult.FAILURE).build();
        }
        if (!Bcrypt.isMatch(password, dbUser.getPassword())) {
            return ResultTuple.<UserEntity>builder()
                    .result(CommonResult.FAILURE_ABSENT).build();
        }
        return ResultTuple.<UserEntity>builder()
                .result(CommonResult.SUCCESS_ADMIN)
                .payload(dbUser).
                build();
    }

    public UserEntity[] getAll() {
        return this.userMapper.selectAll();
    }

    public Results retire(UserEntity signedUser, String email) {
        if (signedUser == null) {
            return CommonResult.FAILURE;
        }
        if (signedUser.isDeleted()) {
            return CommonResult.FAILURE_ABSENT;
        }
        if (email.isBlank()) {
            return CommonResult.FAILURE;
        }
        UserEntity dbUser = this.userMapper.selectByEmail(email);
        if (dbUser == null) {
            return CommonResult.FAILURE;
        }
        dbUser.setDeleted(true);
        return this.userMapper.update(dbUser) > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }

    public ResultTuple<UserEntity[]> adminSearch(UserEntity signedUser, SearchVo searchVo) {
        if (searchVo == null || signedUser == null) {
            return ResultTuple.<UserEntity[]>builder()
                    .result(CommonResult.FAILURE_ABSENT).build();
        }
        if (!signedUser.isAdmin()) {
            return ResultTuple.<UserEntity[]>builder()
                    .result(CommonResult.FAILURE).build();
        }
        UserEntity[] dbSearch = this.userMapper.selectBySearch(searchVo);
        System.out.println(searchVo.getBy());
        if (dbSearch == null) {
            return ResultTuple.<UserEntity[]>builder().result(CommonResult.FAILURE_ABSENT).build();
        }
        return ResultTuple.<UserEntity[]>builder().result(CommonResult.SUCCESS)
                .payload(dbSearch).build();
    }

}
