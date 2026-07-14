package com.onepass.practice.auth;

import com.onepass.practice.common.AppException;
import com.onepass.practice.student.StudentProfileCatalog;
import com.onepass.practice.student.StudentProfileData;
import com.onepass.practice.student.persistence.StudentProfileDO;
import com.onepass.practice.student.persistence.StudentProfileMapper;
import com.onepass.practice.wechat.WechatCodeSession;
import com.onepass.practice.wechat.WechatMiniProgramClient;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class StudentAuthService {

    private static final Logger log = LoggerFactory.getLogger(StudentAuthService.class);
    private static final Long MOCK_STUDENT_ID = 1001L;
    private static final String DEFAULT_DISPLAY_NAME = "微信用户";

    private final JwtTokenProvider jwtTokenProvider;
    private final StudentProfileCatalog studentProfileCatalog;
    private final Optional<StudentProfileMapper> studentProfileMapper;
    private final WechatMiniProgramClient wechatMiniProgramClient;
    private final boolean mockEnabled;

    public StudentAuthService(
            JwtTokenProvider jwtTokenProvider,
            StudentProfileCatalog studentProfileCatalog,
            Optional<StudentProfileMapper> studentProfileMapper,
            WechatMiniProgramClient wechatMiniProgramClient,
            @Value("${app.mock.enabled:true}") boolean mockEnabled
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.studentProfileCatalog = studentProfileCatalog;
        this.studentProfileMapper = studentProfileMapper;
        this.wechatMiniProgramClient = wechatMiniProgramClient;
        this.mockEnabled = mockEnabled;
    }

    public LoginResponse loginWithWechatCode(String code) {
        if (mockEnabled) {
            return issueMockToken();
        }

        StudentProfileMapper mapper = studentProfileMapper
                .orElseThrow(() -> new IllegalStateException("StudentProfileMapper is not available"));
        WechatCodeSession session = wechatMiniProgramClient.exchangeCode(code);
        StudentProfileDO profile = mapper.selectByWechatOpenid(session.openid());

        if (profile == null) {
            profile = createStudentForWechatSession(mapper, session);
        } else {
            fillMissingUnionid(mapper, profile, session);
        }

        LoginResponse response = issueToken(profile.getId(), profile.getDisplayName(), profile.getAvatarUrl());
        log.info("Issued student token for studentId={}", profile.getId());
        return response;
    }

    private LoginResponse issueMockToken() {
        StudentProfileData profile = studentProfileCatalog.requireById(MOCK_STUDENT_ID);
        LoginResponse response = issueToken(profile.id(), profile.displayName(), profile.avatarUrl());
        log.info("Issued mock student token for studentId={}", MOCK_STUDENT_ID);
        return response;
    }

    private StudentProfileDO createStudentForWechatSession(StudentProfileMapper mapper, WechatCodeSession session) {
        StudentProfileDO profile = new StudentProfileDO();
        profile.setWechatOpenid(session.openid());
        profile.setWechatUnionid(blankToNull(session.unionid()));
        profile.setDisplayName(DEFAULT_DISPLAY_NAME);
        profile.setAvatarUrl("");

        try {
            mapper.insert(profile);
        } catch (DuplicateKeyException exception) {
            StudentProfileDO existing = mapper.selectByWechatOpenid(session.openid());
            if (existing != null) {
                return existing;
            }
            throw exception;
        }

        if (profile.getId() == null) {
            throw new AppException("学生账号创建失败");
        }
        return profile;
    }

    private void fillMissingUnionid(StudentProfileMapper mapper, StudentProfileDO profile, WechatCodeSession session) {
        String unionid = blankToNull(session.unionid());
        if (unionid == null || StringUtils.hasText(profile.getWechatUnionid())) {
            return;
        }
        mapper.updateWechatUnionid(profile.getId(), unionid);
        profile.setWechatUnionid(unionid);
    }

    private LoginResponse issueToken(Long studentId, String displayName, String avatarUrl) {
        String token = jwtTokenProvider.generateStudentToken(studentId);
        LoginUserView user = new LoginUserView(studentId, displayName, avatarUrl);
        return new LoginResponse(token, user);
    }

    private String blankToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
