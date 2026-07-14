package com.onepass.practice.contentimport;

import java.util.List;

public record VocabularyImportItemView(
        int itemOrder,
        String english,
        String chinese,
        String partOfSpeech,
        int sortOrder,
        String status,
        List<String> errors,
        List<String> warnings
) {
}
