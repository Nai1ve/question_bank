package com.onepass.practice.practice;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class PracticeSessionAggregate {

    private String sessionId;
    private long studentId;
    private String entryType;
    private String categoryId;
    private String categoryName;
    private String feedbackMode;
    private PracticeSessionStatus status;
    private int currentIndex;
    private Instant startedAt;
    private Instant lastActiveAt;
    private Instant completedAt;
    private Instant expiredAt;
    private Instant abandonedAt;
    private long version;
    private List<PracticeQuestionSnapshot> questions = new ArrayList<>();

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    public String getEntryType() {
        return entryType;
    }

    public void setEntryType(String entryType) {
        this.entryType = entryType;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getFeedbackMode() {
        return feedbackMode;
    }

    public void setFeedbackMode(String feedbackMode) {
        this.feedbackMode = feedbackMode;
    }

    public PracticeSessionStatus getStatus() {
        return status;
    }

    public void setStatus(PracticeSessionStatus status) {
        this.status = status;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getLastActiveAt() {
        return lastActiveAt;
    }

    public void setLastActiveAt(Instant lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public Instant getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Instant expiredAt) {
        this.expiredAt = expiredAt;
    }

    public Instant getAbandonedAt() {
        return abandonedAt;
    }

    public void setAbandonedAt(Instant abandonedAt) {
        this.abandonedAt = abandonedAt;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public List<PracticeQuestionSnapshot> getQuestions() {
        return questions;
    }

    public void setQuestions(List<PracticeQuestionSnapshot> questions) {
        this.questions = questions;
    }

    public int getTotalCount() {
        return questions == null ? 0 : questions.size();
    }

    public PracticeQuestionSnapshot getCurrentQuestion() {
        if (questions == null || questions.isEmpty()) {
            return null;
        }
        if (currentIndex < 0 || currentIndex >= questions.size()) {
            return null;
        }
        return questions.get(currentIndex);
    }

    public PracticeQuestionSnapshot findQuestionById(String questionId) {
        if (questions == null) {
            return null;
        }

        return questions.stream()
                .filter(question -> question.getQuestionId().equals(questionId))
                .findFirst()
                .orElse(null);
    }

    public PracticeSessionAggregate copy() {
        PracticeSessionAggregate result = new PracticeSessionAggregate();
        result.setSessionId(sessionId);
        result.setStudentId(studentId);
        result.setEntryType(entryType);
        result.setCategoryId(categoryId);
        result.setCategoryName(categoryName);
        result.setFeedbackMode(feedbackMode);
        result.setStatus(status);
        result.setCurrentIndex(currentIndex);
        result.setStartedAt(startedAt);
        result.setLastActiveAt(lastActiveAt);
        result.setCompletedAt(completedAt);
        result.setExpiredAt(expiredAt);
        result.setAbandonedAt(abandonedAt);
        result.setVersion(version);
        List<PracticeQuestionSnapshot> copiedQuestions = new ArrayList<>();
        if (questions != null) {
            for (PracticeQuestionSnapshot question : questions) {
                copiedQuestions.add(question.copy());
            }
        }
        result.setQuestions(copiedQuestions);
        return result;
    }
}
