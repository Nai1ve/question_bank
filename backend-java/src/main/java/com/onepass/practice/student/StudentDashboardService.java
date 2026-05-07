package com.onepass.practice.student;

import com.onepass.practice.auth.LoginUserView;
import com.onepass.practice.recite.ReciteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StudentDashboardService {

    private static final Logger log = LoggerFactory.getLogger(StudentDashboardService.class);
    private final StudentDashboardTemplateCatalog studentDashboardTemplateCatalog;
    private final StudentProfileCatalog studentProfileCatalog;
    private final ReciteService reciteService;

    public StudentDashboardService(
            StudentDashboardTemplateCatalog studentDashboardTemplateCatalog,
            StudentProfileCatalog studentProfileCatalog,
            ReciteService reciteService
    ) {
        this.studentDashboardTemplateCatalog = studentDashboardTemplateCatalog;
        this.studentProfileCatalog = studentProfileCatalog;
        this.reciteService = reciteService;
    }

    public StudentDashboardResponse getDashboard(Long studentId) {
        log.info("Loaded student dashboard studentId={}", studentId);
        StudentDashboardTemplateData templateData = studentDashboardTemplateCatalog.getTemplateData();
        StudentProfileData profile = studentProfileCatalog.requireById(studentId);

        return new StudentDashboardResponse(
                new LoginUserView(profile.id(), profile.displayName(), profile.avatarUrl()),
                templateData.currentQuestionBank(),
                resolveCurrentRecitePlan(studentId, templateData.currentRecitePlan()),
                templateData.summaryTemplate()
        );
    }

    private String resolveCurrentRecitePlan(Long studentId, String fallbackValue) {
        try {
            return reciteService.resolveCurrentPlanLabel(studentId);
        } catch (Exception exception) {
            return fallbackValue;
        }
    }
}
