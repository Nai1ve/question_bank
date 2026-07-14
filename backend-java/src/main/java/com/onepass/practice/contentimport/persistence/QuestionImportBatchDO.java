package com.onepass.practice.contentimport.persistence;

import java.time.LocalDateTime;

public class QuestionImportBatchDO {

    private Long id;
    private String batchId;
    private String originalFilename;
    private String status;
    private String storageDir;
    private String markdownPath;
    private Integer totalCount;
    private Integer supportedCount;
    private Integer unsupportedCount;
    private Integer errorCount;
    private Integer warningCount;
    private Integer importedCount;
    private String errorReportJson;
    private LocalDateTime importedAt;
    private LocalDateTime canceledAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStorageDir() {
        return storageDir;
    }

    public void setStorageDir(String storageDir) {
        this.storageDir = storageDir;
    }

    public String getMarkdownPath() {
        return markdownPath;
    }

    public void setMarkdownPath(String markdownPath) {
        this.markdownPath = markdownPath;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getSupportedCount() {
        return supportedCount;
    }

    public void setSupportedCount(Integer supportedCount) {
        this.supportedCount = supportedCount;
    }

    public Integer getUnsupportedCount() {
        return unsupportedCount;
    }

    public void setUnsupportedCount(Integer unsupportedCount) {
        this.unsupportedCount = unsupportedCount;
    }

    public Integer getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
    }

    public Integer getWarningCount() {
        return warningCount;
    }

    public void setWarningCount(Integer warningCount) {
        this.warningCount = warningCount;
    }

    public Integer getImportedCount() {
        return importedCount;
    }

    public void setImportedCount(Integer importedCount) {
        this.importedCount = importedCount;
    }

    public String getErrorReportJson() {
        return errorReportJson;
    }

    public void setErrorReportJson(String errorReportJson) {
        this.errorReportJson = errorReportJson;
    }

    public LocalDateTime getImportedAt() {
        return importedAt;
    }

    public void setImportedAt(LocalDateTime importedAt) {
        this.importedAt = importedAt;
    }

    public LocalDateTime getCanceledAt() {
        return canceledAt;
    }

    public void setCanceledAt(LocalDateTime canceledAt) {
        this.canceledAt = canceledAt;
    }
}
