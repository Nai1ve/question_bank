package com.onepass.practice.student;

public record StudentProfileData(
        Long id,
        String displayName,
        String avatarUrl
) {
}
