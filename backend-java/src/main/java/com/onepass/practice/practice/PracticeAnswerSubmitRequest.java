package com.onepass.practice.practice;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record PracticeAnswerSubmitRequest(
        @NotBlank String questionId,
        List<String> selectedOptions
) {
}
