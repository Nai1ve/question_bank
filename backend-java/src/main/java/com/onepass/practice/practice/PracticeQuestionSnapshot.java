package com.onepass.practice.practice;

import java.util.ArrayList;
import java.util.List;

public class PracticeQuestionSnapshot {

    private String questionId;
    private int order;
    private String type;
    private List<String> tags = new ArrayList<>();
    private String stem;
    private List<PracticeQuestionOptionView> options = new ArrayList<>();
    private List<String> standardAnswer = new ArrayList<>();
    private String analysis;
    private boolean submitted;
    private boolean correct;
    private List<String> userAnswer = new ArrayList<>();
    private String userAnswerLabel = "";
    private String answerLabel = "";
    private long version;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getStem() {
        return stem;
    }

    public void setStem(String stem) {
        this.stem = stem;
    }

    public List<PracticeQuestionOptionView> getOptions() {
        return options;
    }

    public void setOptions(List<PracticeQuestionOptionView> options) {
        this.options = options;
    }

    public List<String> getStandardAnswer() {
        return standardAnswer;
    }

    public void setStandardAnswer(List<String> standardAnswer) {
        this.standardAnswer = standardAnswer;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public List<String> getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(List<String> userAnswer) {
        this.userAnswer = userAnswer;
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

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public PracticeQuestionSnapshot copy() {
        PracticeQuestionSnapshot result = new PracticeQuestionSnapshot();
        result.setQuestionId(questionId);
        result.setOrder(order);
        result.setType(type);
        result.setTags(tags == null ? new ArrayList<>() : new ArrayList<>(tags));
        result.setStem(stem);
        result.setOptions(options == null ? new ArrayList<>() : new ArrayList<>(options));
        result.setStandardAnswer(standardAnswer == null ? new ArrayList<>() : new ArrayList<>(standardAnswer));
        result.setAnalysis(analysis);
        result.setSubmitted(submitted);
        result.setCorrect(correct);
        result.setUserAnswer(userAnswer == null ? new ArrayList<>() : new ArrayList<>(userAnswer));
        result.setUserAnswerLabel(userAnswerLabel);
        result.setAnswerLabel(answerLabel);
        result.setVersion(version);
        return result;
    }
}
