package com.onepass.practice.student;

import com.fasterxml.jackson.core.type.TypeReference;
import com.onepass.practice.common.JsonResourceReader;
import com.onepass.practice.student.persistence.StudentDashboardBlockDO;
import com.onepass.practice.student.persistence.StudentDashboardBlockMapper;
import com.onepass.practice.student.persistence.StudentDashboardTemplateDO;
import com.onepass.practice.student.persistence.StudentDashboardTemplateMapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StudentDashboardTemplateCatalog {

    private static final Logger log = LoggerFactory.getLogger(StudentDashboardTemplateCatalog.class);

    private final boolean mockEnabled;
    private final StudentDashboardTemplateData templateData;
    private final StudentDashboardTemplateMapper studentDashboardTemplateMapper;
    private final StudentDashboardBlockMapper studentDashboardBlockMapper;

    public StudentDashboardTemplateCatalog(
            @Value("${app.mock.enabled:true}") boolean mockEnabled,
            JsonResourceReader jsonResourceReader,
            ObjectProvider<StudentDashboardTemplateMapper> studentDashboardTemplateMapperProvider,
            ObjectProvider<StudentDashboardBlockMapper> studentDashboardBlockMapperProvider
    ) {
        this.mockEnabled = mockEnabled;
        this.studentDashboardTemplateMapper = studentDashboardTemplateMapperProvider.getIfAvailable();
        this.studentDashboardBlockMapper = studentDashboardBlockMapperProvider.getIfAvailable();
        this.templateData = jsonResourceReader.read(
                "mock-data/student-dashboard.json",
                new TypeReference<StudentDashboardTemplateData>() {
                }
        );
        log.info("Loaded student dashboard template mode={}", mockEnabled ? "resource" : "mysql");
    }

    public StudentDashboardTemplateData getTemplateData() {
        if (!mockEnabled) {
            if (studentDashboardTemplateMapper == null || studentDashboardBlockMapper == null) {
                throw new IllegalStateException("Student dashboard mappers are not available");
            }

            StudentDashboardTemplateDO template = studentDashboardTemplateMapper.selectActiveTemplate();
            if (template == null) {
                throw new IllegalStateException("Student dashboard template does not exist");
            }
            List<StudentSummaryBlockView> blocks = studentDashboardBlockMapper.selectByTemplateId(template.getId()).stream()
                    .map(this::toBlockView)
                    .toList();
            return new StudentDashboardTemplateData(
                    template.getCurrentQuestionBank(),
                    template.getCurrentRecitePlan(),
                    new StudentSummaryTemplateView(
                            template.getTitle(),
                            template.getTemplateName(),
                            blocks
                    )
            );
        }
        return templateData;
    }

    private StudentSummaryBlockView toBlockView(StudentDashboardBlockDO item) {
        return new StudentSummaryBlockView(item.getBlockKey(), item.getLabel(), item.getContent());
    }
}
