package com.onepass.practice.contentimport;

import java.util.List;

public record ParsedImportQuestion(
        int itemOrder,
        String sourceQuestionNo,
        String rawQuestionType,
        String normalizedQuestionType,
        String categoryPath,
        String categoryId,
        List<String> tags,
        String stem,
        List<QuestionImportOption> options,
        List<String> answerKeys,
        String analysis,
        String knowledgePoint,
        String difficulty,
        String microCourseId,
        String source,
        String markdownBlock,
        boolean supported,
        List<String> errors,
        List<String> warnings
) {
}
