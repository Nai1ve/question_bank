package com.onepass.practice.recite;

public record RecitePlanDayView(
        int dayNumber,
        String dayLabel,
        int totalCount,
        String status,
        boolean studyCompleted,
        Long latestRecordId,
        String latestMode,
        String lastAccuracy,
        Integer lastCorrectCount,
        Integer lastWrongCount,
        boolean cnToEnCompleted,
        Long cnToEnRecordId,
        String cnToEnAccuracy,
        boolean enToCnCompleted,
        Long enToCnRecordId,
        String enToCnAccuracy
) {
}
