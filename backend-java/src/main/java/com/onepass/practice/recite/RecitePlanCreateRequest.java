package com.onepass.practice.recite;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record RecitePlanCreateRequest(
        @NotBlank String bookId,
        @Min(1) Integer dailyCount
) {
}
