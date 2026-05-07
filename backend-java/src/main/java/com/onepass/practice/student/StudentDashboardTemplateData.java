package com.onepass.practice.student;

public record StudentDashboardTemplateData(
        String currentQuestionBank,
        String currentRecitePlan,
        StudentSummaryTemplateView summaryTemplate
) {
}
