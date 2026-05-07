package com.onepass.practice.practice;

public record PracticeSessionView(
        String sessionId,
        String entryType,
        String categoryId,
        String categoryName,
        String feedbackMode,
        String status,
        Integer currentIndex,
        Integer currentSequence,
        Integer totalCount,
        boolean completed,
        String startedAt,
        String lastActiveAt,
        String completedAt,
        String expiredAt,
        PracticeQuestionView currentQuestion
) {
}
