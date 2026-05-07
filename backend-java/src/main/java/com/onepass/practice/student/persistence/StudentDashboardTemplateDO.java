package com.onepass.practice.student.persistence;

public class StudentDashboardTemplateDO {

    private Long id;
    private String templateCode;
    private String title;
    private String templateName;
    private String currentQuestionBank;
    private String currentRecitePlan;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getCurrentQuestionBank() {
        return currentQuestionBank;
    }

    public void setCurrentQuestionBank(String currentQuestionBank) {
        this.currentQuestionBank = currentQuestionBank;
    }

    public String getCurrentRecitePlan() {
        return currentRecitePlan;
    }

    public void setCurrentRecitePlan(String currentRecitePlan) {
        this.currentRecitePlan = currentRecitePlan;
    }
}
