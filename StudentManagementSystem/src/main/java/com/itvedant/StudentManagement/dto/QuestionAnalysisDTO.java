package com.itvedant.StudentManagement.dto;

public class QuestionAnalysisDTO {

    private Long questionId;

    private int questionNumber;

    private String questionText;

    private int marks;

    private int totalStudents;

    private int correctCount;

    private int incorrectCount;

    private int skippedCount;

    private double correctPercentage;

    private double incorrectPercentage;

    private double skippedPercentage;

    private String difficulty;

    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }

    public int getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(int totalStudents) {
        this.totalStudents = totalStudents;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(int correctCount) {
        this.correctCount = correctCount;
    }

    public int getIncorrectCount() {
        return incorrectCount;
    }

    public void setIncorrectCount(int incorrectCount) {
        this.incorrectCount = incorrectCount;
    }

    public int getSkippedCount() {
        return skippedCount;
    }

    public void setSkippedCount(int skippedCount) {
        this.skippedCount = skippedCount;
    }

    public double getCorrectPercentage() {
        return correctPercentage;
    }

    public void setCorrectPercentage(
            double correctPercentage
    ) {
        this.correctPercentage = correctPercentage;
    }

    public double getIncorrectPercentage() {
        return incorrectPercentage;
    }

    public void setIncorrectPercentage(
            double incorrectPercentage
    ) {
        this.incorrectPercentage = incorrectPercentage;
    }

    public double getSkippedPercentage() {
        return skippedPercentage;
    }

    public void setSkippedPercentage(
            double skippedPercentage
    ) {
        this.skippedPercentage = skippedPercentage;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
}