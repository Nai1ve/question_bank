package com.onepass.practice.student;

import com.onepass.practice.auth.LoginUserView;

public record StudentDashboardResponse(
        LoginUserView user,
        String currentQuestionBank,
        String currentRecitePlan,
        StudentSummaryTemplateView summaryTemplate
) {
}
