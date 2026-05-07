package com.onepass.practice.auth;

import com.onepass.practice.common.AppException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class StudentIdentityService {

    private final JwtTokenProvider jwtTokenProvider;

    public StudentIdentityService(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public Long requireStudentId(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader)) {
            throw new AppException("Student authorization is required");
        }
        if (!authorizationHeader.startsWith("Bearer ")) {
            throw new AppException("Student authorization is invalid");
        }
        String token = authorizationHeader.substring("Bearer ".length()).trim();
        if (!StringUtils.hasText(token)) {
            throw new AppException("Student authorization is invalid");
        }
        return jwtTokenProvider.parseStudentId(token);
    }
}
