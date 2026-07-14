package com.onepass.practice.wechat;

public record WechatCodeSession(String openid, String sessionKey, String unionid) {
}
