package com.onepass.practice.auth;

import com.onepass.practice.common.ApiResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student/auth")
public class StudentAuthController {

    private static final Logger log = LoggerFactory.getLogger(StudentAuthController.class);

    private final StudentAuthService studentAuthService;

    public StudentAuthController(StudentAuthService studentAuthService) {
        this.studentAuthService = studentAuthService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody WxLoginRequest request) {
        log.info("Received student login request");
        return ApiResponse.ok(studentAuthService.loginWithWechatCode(request.code()));
    }
}
