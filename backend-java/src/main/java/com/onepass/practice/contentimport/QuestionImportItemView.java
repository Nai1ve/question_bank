package com.onepass.practice.contentimport;

import java.util.List;

public record QuestionImportItemView(
        int itemOrder,
        String sourceQuestionNo,
        String questionType,
        String normalizedQuestionType,
        String categoryPath,
        String status,
        List<String> errors,
        List<String> warnings,
        String stemPreview,
        String stem,
        List<QuestionImportOption> options,
        List<String> answerKeys,
        String analysis,
        String knowledgePoint,
        String difficulty,
        String targetQuestionId
) {
}
