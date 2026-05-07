package com.onepass.practice.recite;

import java.util.List;

public record RecitePlanDaysResponse(
        ReciteActivePlanView plan,
        List<RecitePlanDayView> days
) {
}
