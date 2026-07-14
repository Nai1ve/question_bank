package com.onepass.practice.contentimport;

import java.util.List;

public record ParsedVocabularyWord(
        int itemOrder,
        String english,
        String chinese,
        String partOfSpeech,
        int sortOrder,
        List<String> errors,
        List<String> warnings
) {
}
