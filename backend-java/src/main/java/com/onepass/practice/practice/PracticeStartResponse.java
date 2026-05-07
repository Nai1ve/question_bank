package com.onepass.practice.practice;

public record PracticeStartResponse(
        boolean ok,
        String sessionId,
        Integer totalCount,
        String entryType,
        String categoryName,
        String feedbackMode,
        String message
) {
}

