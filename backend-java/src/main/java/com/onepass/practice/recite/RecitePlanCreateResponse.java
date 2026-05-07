package com.onepass.practice.recite;

public record RecitePlanCreateResponse(
        Long planId,
        String bookId,
        String bookName,
        int dailyCount,
        int totalDays
) {
}
