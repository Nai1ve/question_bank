package com.onepass.practice.recite.persistence;

import java.time.LocalDateTime;

public class RecitePlanDayDO {

    private Long id;
    private Long planId;
    private Integer dayNumber;
    private String dayLabel;
    private Integer startWordOrder;
    private Integer endWordOrder;
    private Integer totalCount;
    private String status;
    private LocalDateTime studyCompletedAt;
    private String lastAccuracy;
    private Integer lastCorrectCount;
    private Integer lastWrongCount;
    private LocalDateTime completedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public Integer getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(Integer dayNumber) {
        this.dayNumber = dayNumber;
    }

    public String getDayLabel() {
        return dayLabel;
    }

    public void setDayLabel(String dayLabel) {
        this.dayLabel = dayLabel;
    }

    public Integer getStartWordOrder() {
        return startWordOrder;
    }

    public void setStartWordOrder(Integer startWordOrder) {
        this.startWordOrder = startWordOrder;
    }

    public Integer getEndWordOrder() {
        return endWordOrder;
    }

    public void setEndWordOrder(Integer endWordOrder) {
        this.endWordOrder = endWordOrder;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getStudyCompletedAt() {
        return studyCompletedAt;
    }

    public void setStudyCompletedAt(LocalDateTime studyCompletedAt) {
        this.studyCompletedAt = studyCompletedAt;
    }

    public String getLastAccuracy() {
        return lastAccuracy;
    }

    public void setLastAccuracy(String lastAccuracy) {
        this.lastAccuracy = lastAccuracy;
    }

    public Integer getLastCorrectCount() {
        return lastCorrectCount;
    }

    public void setLastCorrectCount(Integer lastCorrectCount) {
        this.lastCorrectCount = lastCorrectCount;
    }

    public Integer getLastWrongCount() {
        return lastWrongCount;
    }

    public void setLastWrongCount(Integer lastWrongCount) {
        this.lastWrongCount = lastWrongCount;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
