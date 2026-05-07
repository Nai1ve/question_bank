package com.onepass.practice.recite;

import java.util.List;

public record ReciteStudyView(
        Long planId,
        String bookName,
        int dayNumber,
        String dayLabel,
        int totalCount,
        List<ReciteStudyItemView> items
) {
}
