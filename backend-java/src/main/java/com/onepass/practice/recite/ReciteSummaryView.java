package com.onepass.practice.recite;

import java.util.List;

public record ReciteSummaryView(
        Long recordId,
        String bookName,
        String dayLabel,
        String mode,
        int totalCount,
        int correctCount,
        int wrongCount,
        String accuracy,
        List<ReciteSummaryItemView> items
) {
}
