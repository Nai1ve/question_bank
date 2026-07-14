package com.onepass.practice.wechat;

import com.onepass.practice.common.AppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class WechatMiniProgramClient {

    private static final Logger log = LoggerFactory.getLogger(WechatMiniProgramClient.class);
    private static final String CODE_TO_SESSION_PATH = "/sns/jscode2session";
    private static final String PLACEHOLDER_VALUE = "replace-me";

    private final WechatMiniProgramProperties properties;
    private final RestClient restClient;

    public WechatMiniProgramClient(WechatMiniProgramProperties properties, RestClient.Builder restClientBuilder) {
        this.properties = properties;
        this.restClient = restClientBuilder
                .baseUrl("https://api.weixin.qq.com")
                .build();
    }

    public WechatCodeSession exchangeCode(String code) {
        String normalizedCode = normalize(code);
        if (!StringUtils.hasText(normalizedCode)) {
            throw new AppException("微信登录凭证不能为空");
        }

        String appId = normalize(properties.getAppId());
        String appSecret = normalize(properties.getAppSecret());
        if (isMissingConfig(appId) || isMissingConfig(appSecret)) {
            throw new AppException("微信登录配置未完成");
        }

        WechatCodeSessionResponse response;
        try {
            response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(CODE_TO_SESSION_PATH)
                            .queryParam("appid", appId)
                            .queryParam("secret", appSecret)
                            .queryParam("js_code", normalizedCode)
                            .queryParam("grant_type", "authorization_code")
                            .build())
                    .retrieve()
                    .body(WechatCodeSessionResponse.class);
        } catch (RestClientException exception) {
            log.warn("WeChat code2Session request failed: {}", exception.getMessage());
            throw new AppException("微信登录服务暂时不可用");
        }

        if (response == null) {
            throw new AppException("微信登录服务暂时不可用");
        }

        if (response.errcode() != null && response.errcode() != 0) {
            log.warn("WeChat code2Session rejected code, errcode={}, errmsg={}",
                    response.errcode(), safeWechatMessage(response.errmsg()));
            throw new AppException("微信登录失败，请重新登录");
        }

        if (!StringUtils.hasText(response.openid())) {
            log.warn("WeChat code2Session response missing openid");
            throw new AppException("微信登录失败，请重新登录");
        }

        return new WechatCodeSession(response.openid(), response.sessionKey(), response.unionid());
    }

    private boolean isMissingConfig(String value) {
        return !StringUtils.hasText(value) || PLACEHOLDER_VALUE.equals(value);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private String safeWechatMessage(String message) {
        return StringUtils.hasText(message) ? message : "";
    }
}
