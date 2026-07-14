package com.onepass.practice.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WechatCodeSessionResponse(
        String openid,
        @JsonProperty("session_key") String sessionKey,
        String unionid,
        Integer errcode,
        String errmsg
) {
}
