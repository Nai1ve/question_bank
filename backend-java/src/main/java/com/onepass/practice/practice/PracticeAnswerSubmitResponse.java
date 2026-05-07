package com.onepass.practice.practice;

public record PracticeAnswerSubmitResponse(
        boolean correct,
        String standardAnswer,
        String userAnswer,
        String analysis
) {
}
