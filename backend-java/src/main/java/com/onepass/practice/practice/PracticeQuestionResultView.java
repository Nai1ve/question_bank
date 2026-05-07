package com.onepass.practice.practice;

import java.util.List;

public record PracticeQuestionResultView(
        String id,
        Integer sequence,
        String type,
        String stem,
        List<String> tags,
        List<PracticeQuestionOptionView> options,
        List<String> userAnswer,
        String userAnswerLabel,
        List<String> standardAnswer,
        String standardAnswerLabel,
        boolean correct,
        String analysis
) {
}
