package com.onepass.practice.auth;

public record LoginResponse(String token, LoginUserView user) {
}

