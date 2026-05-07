package com.onepass.practice.practice;

import java.util.List;

public record PracticeQuestionDefinition(
        String id,
        String categoryId,
        List<String> categoryPathIds,
        String type,
        List<String> tags,
        String stem,
        List<PracticeQuestionOptionView> options,
        List<String> answer,
        String analysis
) {
}
