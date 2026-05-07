package com.onepass.practice.practice;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record PracticeStartRequest(
        @NotBlank String entryType,
        String categoryId,
        @NotBlank String categoryName,
        @NotNull Integer questionCount,
        @NotBlank String feedbackMode,
        List<String> selectedTags
) {
}
