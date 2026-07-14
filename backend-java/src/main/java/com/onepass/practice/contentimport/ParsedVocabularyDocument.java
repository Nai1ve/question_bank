package com.onepass.practice.contentimport;

import java.util.List;

public record ParsedVocabularyDocument(
        String bookId,
        String bookName,
        String description,
        List<ParsedVocabularyWord> words,
        List<String> errors,
        List<String> warnings
) {
}
