package com.onepass.practice.recite;

public record ReciteActivePlanView(
        Long planId,
        String bookId,
        String bookName,
        int dailyCount,
        int totalWords,
        int totalDays,
        int completedDays,
        String currentDayLabel,
        String status
) {
}
