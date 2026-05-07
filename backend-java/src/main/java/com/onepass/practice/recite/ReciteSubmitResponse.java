package com.onepass.practice.recite;

public record ReciteSubmitResponse(
        Long recordId,
        int totalCount,
        int correctCount,
        int wrongCount,
        String accuracy
) {
}
