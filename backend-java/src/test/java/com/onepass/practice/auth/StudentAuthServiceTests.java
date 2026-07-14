package com.onepass.practice.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.onepass.practice.student.StudentProfileCatalog;
import com.onepass.practice.student.StudentProfileData;
import com.onepass.practice.student.persistence.StudentProfileDO;
import com.onepass.practice.student.persistence.StudentProfileMapper;
import com.onepass.practice.wechat.WechatCodeSession;
import com.onepass.practice.wechat.WechatMiniProgramClient;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class StudentAuthServiceTests {

    @Test
    void loginWithWechatCodeReturnsExistingStudent() {
        StudentProfileMapper mapper = mock(StudentProfileMapper.class);
        WechatMiniProgramClient wechatClient = mock(WechatMiniProgramClient.class);
        StudentProfileCatalog catalog = mock(StudentProfileCatalog.class);
        StudentAuthService service = realWechatService(mapper, wechatClient, catalog);
        StudentProfileDO profile = profile(2001L, "openid-1", "union-1");

        when(wechatClient.exchangeCode("wx-code")).thenReturn(new WechatCodeSession("openid-1", "session-key", "union-1"));
        when(mapper.selectByWechatOpenid("openid-1")).thenReturn(profile);

        LoginResponse response = service.loginWithWechatCode("wx-code");

        assertThat(response.user().id()).isEqualTo(2001L);
        assertThat(response.user().displayName()).isEqualTo("微信用户");
        assertThat(response.token()).isNotBlank();
        verify(mapper, never()).insert(any());
    }

    @Test
    void loginWithWechatCodeCreatesStudentForNewOpenid() {
        StudentProfileMapper mapper = mock(StudentProfileMapper.class);
        WechatMiniProgramClient wechatClient = mock(WechatMiniProgramClient.class);
        StudentProfileCatalog catalog = mock(StudentProfileCatalog.class);
        StudentAuthService service = realWechatService(mapper, wechatClient, catalog);

        when(wechatClient.exchangeCode("wx-code")).thenReturn(new WechatCodeSession("openid-2", "session-key", "union-2"));
        when(mapper.selectByWechatOpenid("openid-2")).thenReturn(null);
        doAnswer(invocation -> {
            StudentProfileDO inserted = invocation.getArgument(0);
            inserted.setId(2002L);
            return 1;
        }).when(mapper).insert(any(StudentProfileDO.class));

        LoginResponse response = service.loginWithWechatCode("wx-code");

        assertThat(response.user().id()).isEqualTo(2002L);
        assertThat(response.user().displayName()).isEqualTo("微信用户");
        assertThat(response.token()).isNotBlank();
        verify(mapper).insert(any(StudentProfileDO.class));
    }

    @Test
    void loginWithWechatCodeUsesMockStudentWhenMockEnabled() {
        StudentProfileMapper mapper = mock(StudentProfileMapper.class);
        WechatMiniProgramClient wechatClient = mock(WechatMiniProgramClient.class);
        StudentProfileCatalog catalog = mock(StudentProfileCatalog.class);
        StudentAuthService service = mockWechatService(mapper, wechatClient, catalog);

        when(catalog.requireById(1001L)).thenReturn(new StudentProfileData(1001L, "微信用户", ""));

        LoginResponse response = service.loginWithWechatCode("ignored");

        assertThat(response.user().id()).isEqualTo(1001L);
        assertThat(response.token()).isNotBlank();
        verify(wechatClient, never()).exchangeCode(any());
        verify(mapper, never()).selectByWechatOpenid(any());
    }

    private StudentAuthService realWechatService(
            StudentProfileMapper mapper,
            WechatMiniProgramClient wechatClient,
            StudentProfileCatalog catalog
    ) {
        return new StudentAuthService(jwtTokenProvider(), catalog, Optional.of(mapper), wechatClient, false);
    }

    private StudentAuthService mockWechatService(
            StudentProfileMapper mapper,
            WechatMiniProgramClient wechatClient,
            StudentProfileCatalog catalog
    ) {
        return new StudentAuthService(jwtTokenProvider(), catalog, Optional.of(mapper), wechatClient, true);
    }

    private JwtTokenProvider jwtTokenProvider() {
        JwtProperties properties = new JwtProperties();
        properties.setIssuer("test");
        properties.setSecret("test-jwt-secret-test-jwt-secret-1234");
        properties.setExpirationSeconds(3600);
        return new JwtTokenProvider(properties);
    }

    private StudentProfileDO profile(Long id, String openid, String unionid) {
        StudentProfileDO profile = new StudentProfileDO();
        profile.setId(id);
        profile.setWechatOpenid(openid);
        profile.setWechatUnionid(unionid);
        profile.setDisplayName("微信用户");
        profile.setAvatarUrl("");
        return profile;
    }
}
