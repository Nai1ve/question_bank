package com.onepass.practice.practice;

import java.util.List;

public record PracticeSummaryView(
        String sessionId,
        String entryType,
        String categoryName,
        String feedbackMode,
        Integer totalCount,
        Integer correctCount,
        Integer wrongCount,
        String accuracy,
        List<PracticeQuestionResultView> questionResults
) {
}
