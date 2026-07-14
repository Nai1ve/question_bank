package com.onepass.practice.contentimport;

import com.onepass.practice.common.AppException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AdminImportTokenService {

    private final ContentImportProperties properties;

    public AdminImportTokenService(ContentImportProperties properties) {
        this.properties = properties;
    }

    public void requireValidToken(String token) {
        if (!StringUtils.hasText(token) || !properties.getAdminToken().equals(token)) {
            throw new AppException("Invalid admin import token");
        }
    }
}
