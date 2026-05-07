package com.onepass.practice.vocabulary;

public record VocabularyBookView(
        String id,
        String name,
        String description,
        int totalWords
) {
}
