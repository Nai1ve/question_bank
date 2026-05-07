package com.onepass.practice.practice;

import java.util.List;

public record PracticeQuestionView(
        String id,
        String type,
        List<String> tags,
        String stem,
        List<PracticeQuestionOptionView> options,
        List<String> userAnswer
) {
}
