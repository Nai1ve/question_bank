package com.onepass.practice.practice;

import jakarta.validation.constraints.NotBlank;

public record PracticeActiveSessionQuery(
        @NotBlank String entryType,
        String categoryId
) {
}
