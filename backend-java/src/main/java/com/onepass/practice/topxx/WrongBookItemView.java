package com.onepass.practice.topxx;

import java.util.List;

public record WrongBookItemView(
        String questionId,
        String stem,
        String categoryName,
        int wrongCount,
        int answeredCount,
        List<String> tags
) {
}
