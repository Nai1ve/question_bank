package com.onepass.practice.auth;

import com.onepass.practice.student.StudentProfileCatalog;
import com.onepass.practice.student.StudentProfileData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StudentAuthService {

    private static final Logger log = LoggerFactory.getLogger(StudentAuthService.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final StudentProfileCatalog studentProfileCatalog;

    public StudentAuthService(JwtTokenProvider jwtTokenProvider, StudentProfileCatalog studentProfileCatalog) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.studentProfileCatalog = studentProfileCatalog;
    }

    public LoginResponse loginWithWechatCode(String code) {
        Long studentId = 1001L;
        String token = jwtTokenProvider.generateStudentToken(studentId);
        StudentProfileData profile = studentProfileCatalog.requireById(studentId);
        LoginUserView user = new LoginUserView(
                profile.id(),
                profile.displayName(),
                profile.avatarUrl()
        );
        log.info("Issued student token for studentId={}", studentId);
        return new LoginResponse(token, user);
    }
}
