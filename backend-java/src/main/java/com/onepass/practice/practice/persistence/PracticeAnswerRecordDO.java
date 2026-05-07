package com.onepass.practice.practice.persistence;

import java.time.LocalDateTime;

public class PracticeAnswerRecordDO {

    private Long id;
    private String sessionId;
    private String questionId;
    private Integer submitSeq;
    private String selectedAnswerJson;
    private Boolean correct;
    private LocalDateTime submittedAt;
    private LocalDateTime createdAt;

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

    public Integer getSubmitSeq() {
        return submitSeq;
    }

    public void setSubmitSeq(Integer submitSeq) {
        this.submitSeq = submitSeq;
    }

    public String getSelectedAnswerJson() {
        return selectedAnswerJson;
    }

    public void setSelectedAnswerJson(String selectedAnswerJson) {
        this.selectedAnswerJson = selectedAnswerJson;
    }

    public Boolean getCorrect() {
        return correct;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
