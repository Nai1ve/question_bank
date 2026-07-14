package com.onepass.practice.contentimport.persistence;

public class QuestionImportItemDO {

    private Long id;
    private String batchId;
    private Integer itemOrder;
    private String sourceQuestionNo;
    private String questionType;
    private String normalizedQuestionType;
    private String categoryPath;
    private String status;
    private String errorsJson;
    private String warningsJson;
    private String parsedJson;
    private String markdownBlock;
    private String targetQuestionId;

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

    public Integer getItemOrder() {
        return itemOrder;
    }

    public void setItemOrder(Integer itemOrder) {
        this.itemOrder = itemOrder;
    }

    public String getSourceQuestionNo() {
        return sourceQuestionNo;
    }

    public void setSourceQuestionNo(String sourceQuestionNo) {
        this.sourceQuestionNo = sourceQuestionNo;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getNormalizedQuestionType() {
        return normalizedQuestionType;
    }

    public void setNormalizedQuestionType(String normalizedQuestionType) {
        this.normalizedQuestionType = normalizedQuestionType;
    }

    public String getCategoryPath() {
        return categoryPath;
    }

    public void setCategoryPath(String categoryPath) {
        this.categoryPath = categoryPath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorsJson() {
        return errorsJson;
    }

    public void setErrorsJson(String errorsJson) {
        this.errorsJson = errorsJson;
    }

    public String getWarningsJson() {
        return warningsJson;
    }

    public void setWarningsJson(String warningsJson) {
        this.warningsJson = warningsJson;
    }

    public String getParsedJson() {
        return parsedJson;
    }

    public void setParsedJson(String parsedJson) {
        this.parsedJson = parsedJson;
    }

    public String getMarkdownBlock() {
        return markdownBlock;
    }

    public void setMarkdownBlock(String markdownBlock) {
        this.markdownBlock = markdownBlock;
    }

    public String getTargetQuestionId() {
        return targetQuestionId;
    }

    public void setTargetQuestionId(String targetQuestionId) {
        this.targetQuestionId = targetQuestionId;
    }
}
