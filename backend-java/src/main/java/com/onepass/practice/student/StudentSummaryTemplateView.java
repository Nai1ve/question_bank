package com.onepass.practice.student;

import java.util.List;

public record StudentSummaryTemplateView(
        String title,
        String templateName,
        List<StudentSummaryBlockView> blocks
) {
}
