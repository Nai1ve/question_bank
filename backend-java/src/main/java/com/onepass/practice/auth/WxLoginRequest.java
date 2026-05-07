package com.onepass.practice.auth;

import jakarta.validation.constraints.NotBlank;

public record WxLoginRequest(@NotBlank String code) {
}

