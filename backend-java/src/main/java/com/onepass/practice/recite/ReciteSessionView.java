package com.onepass.practice.recite;

import java.util.List;

public record ReciteSessionView(
        Long planId,
        String bookName,
        int dayNumber,
        String dayLabel,
        String mode,
        int totalCount,
        List<ReciteSessionItemView> items
) {
}
