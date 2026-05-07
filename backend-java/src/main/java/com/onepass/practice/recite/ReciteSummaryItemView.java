package com.onepass.practice.recite;

public record ReciteSummaryItemView(
        Long wordId,
        String prompt,
        String english,
        String chinese,
        String partOfSpeech,
        String userAnswer,
        String standardAnswer,
        boolean correct
) {
}
