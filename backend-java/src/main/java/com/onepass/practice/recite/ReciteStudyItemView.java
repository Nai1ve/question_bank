package com.onepass.practice.recite;

public record ReciteStudyItemView(
        Long wordId,
        String english,
        String chinese,
        String partOfSpeech
) {
}
