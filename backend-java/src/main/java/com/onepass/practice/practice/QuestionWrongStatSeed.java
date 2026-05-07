package com.onepass.practice.practice;

public record QuestionWrongStatSeed(
        long studentId,
        String questionId,
        int answeredCount,
        int wrongCount
) {
}
