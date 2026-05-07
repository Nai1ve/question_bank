package com.onepass.practice.practice.persistence;

import java.time.LocalDateTime;

public class PracticeSessionQuestionDO {

    private Long id;
    private String sessionId;
    private String questionId;
    private Integer questionOrder;
    private String questionType;
    private String stem;
    private String tagsJson;
    private String optionsJson;
    private String standardAnswerJson;
    private String analysis;
    private String userAnswerJson;
    private String userAnswerLabel;
    private String answerLabel;
    private Boolean submitted;
    private Boolean correct;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public Integer getQuestionOrder() {
        return questionOrder;
    }

    public void setQuestionOrder(Integer questionOrder) {
        this.questionOrder = questionOrder;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getStem() {
        return stem;
    }

    public void setStem(String stem) {
        this.stem = stem;
    }

    public String getTagsJson() {
        return tagsJson;
    }

    public void setTagsJson(String tagsJson) {
        this.tagsJson = tagsJson;
    }

    public String getOptionsJson() {
        return optionsJson;
    }

    public void setOptionsJson(String optionsJson) {
        this.optionsJson = optionsJson;
    }

    public String getStandardAnswerJson() {
        return standardAnswerJson;
    }

    public void setStandardAnswerJson(String standardAnswerJson) {
        this.standardAnswerJson = standardAnswerJson;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public String getUserAnswerJson() {
        return userAnswerJson;
    }

    public void setUserAnswerJson(String userAnswerJson) {
        this.userAnswerJson = userAnswerJson;
    }

    public String getUserAnswerLabel() {
        return userAnswerLabel;
    }

    public void setUserAnswerLabel(String userAnswerLabel) {
        this.userAnswerLabel = userAnswerLabel;
    }

    public String getAnswerLabel() {
        return answerLabel;
    }

    public void setAnswerLabel(String answerLabel) {
        this.answerLabel = answerLabel;
    }

    public Boolean getSubmitted() {
        return submitted;
    }

    public void setSubmitted(Boolean submitted) {
        this.submitted = submitted;
    }

    public Boolean getCorrect() {
        return correct;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
