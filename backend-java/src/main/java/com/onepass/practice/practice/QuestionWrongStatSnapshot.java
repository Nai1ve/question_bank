package com.onepass.practice.practice;

public record QuestionWrongStatSnapshot(
        String questionId,
        int answeredCount,
        int wrongCount
) {
}
