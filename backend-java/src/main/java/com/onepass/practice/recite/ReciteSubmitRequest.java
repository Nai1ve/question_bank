package com.onepass.practice.recite;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record ReciteSubmitRequest(
        @NotBlank String mode,
        List<ReciteAnswerItemRequest> answers
) {
}
