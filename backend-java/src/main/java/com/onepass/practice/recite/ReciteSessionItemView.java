package com.onepass.practice.recite;

public record ReciteSessionItemView(
        Long wordId,
        String prompt,
        String english,
        String chinese,
        String partOfSpeech
) {
}
