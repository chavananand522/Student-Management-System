package com.itvedant.StudentManagement.dto;

import java.util.ArrayList;
import java.util.List;

public class TestAnalysisDTO {

    // =====================================================
    // TEST
    // =====================================================

    private Long testId;

    private String testName;

    private String course;

    private String subject;

    private String chapter;

    private Integer totalMarks;

    private Integer passingMarks;

    private Integer duration;

    // =====================================================
    // SUMMARY
    // =====================================================

    private int totalStudents;

    private int attemptedStudents;

    private double attemptedPercentage;

    private double averageScore;

    private double averagePercentage;

    private int passedStudents;

    private int failedStudents;

    private double passRate;

    // =====================================================
    // SCORE DISTRIBUTION
    // =====================================================

    private int score0To20;

    private int score21To40;

    private int score41To60;

    private int score61To80;

    private int score81To100;

    // =====================================================
    // QUESTION ANALYSIS
    // =====================================================

    private int totalQuestions;

    private int totalCorrect;

    private int totalIncorrect;

    private int totalUnattempted;

    private double correctPercentage;

    private double incorrectPercentage;

    private double unattemptedPercentage;

    // =====================================================
    // LISTS
    // =====================================================

    private List<QuestionAnalysisDTO> questions =
            new ArrayList<>();

    private List<StudentPerformanceDTO> students =
            new ArrayList<>();

    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public Integer getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(Integer totalMarks) {
        this.totalMarks = totalMarks;
    }

    public Integer getPassingMarks() {
        return passingMarks;
    }

    public void setPassingMarks(Integer passingMarks) {
        this.passingMarks = passingMarks;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public int getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(int totalStudents) {
        this.totalStudents = totalStudents;
    }

    public int getAttemptedStudents() {
        return attemptedStudents;
    }

    public void setAttemptedStudents(int attemptedStudents) {
        this.attemptedStudents = attemptedStudents;
    }

    public double getAttemptedPercentage() {
        return attemptedPercentage;
    }

    public void setAttemptedPercentage(double attemptedPercentage) {
        this.attemptedPercentage = attemptedPercentage;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }

    public double getAveragePercentage() {
        return averagePercentage;
    }

    public void setAveragePercentage(double averagePercentage) {
        this.averagePercentage = averagePercentage;
    }

    public int getPassedStudents() {
        return passedStudents;
    }

    public void setPassedStudents(int passedStudents) {
        this.passedStudents = passedStudents;
    }

    public int getFailedStudents() {
        return failedStudents;
    }

    public void setFailedStudents(int failedStudents) {
        this.failedStudents = failedStudents;
    }

    public double getPassRate() {
        return passRate;
    }

    public void setPassRate(double passRate) {
        this.passRate = passRate;
    }

    public int getScore0To20() {
        return score0To20;
    }

    public void setScore0To20(int score0To20) {
        this.score0To20 = score0To20;
    }

    public int getScore21To40() {
        return score21To40;
    }

    public void setScore21To40(int score21To40) {
        this.score21To40 = score21To40;
    }

    public int getScore41To60() {
        return score41To60;
    }

    public void setScore41To60(int score41To60) {
        this.score41To60 = score41To60;
    }

    public int getScore61To80() {
        return score61To80;
    }

    public void setScore61To80(int score61To80) {
        this.score61To80 = score61To80;
    }

    public int getScore81To100() {
        return score81To100;
    }

    public void setScore81To100(int score81To100) {
        this.score81To100 = score81To100;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getTotalCorrect() {
        return totalCorrect;
    }

    public void setTotalCorrect(int totalCorrect) {
        this.totalCorrect = totalCorrect;
    }

    public int getTotalIncorrect() {
        return totalIncorrect;
    }

    public void setTotalIncorrect(int totalIncorrect) {
        this.totalIncorrect = totalIncorrect;
    }

    public int getTotalUnattempted() {
        return totalUnattempted;
    }

    public void setTotalUnattempted(int totalUnattempted) {
        this.totalUnattempted = totalUnattempted;
    }

    public double getCorrectPercentage() {
        return correctPercentage;
    }

    public void setCorrectPercentage(double correctPercentage) {
        this.correctPercentage = correctPercentage;
    }

    public double getIncorrectPercentage() {
        return incorrectPercentage;
    }

    public void setIncorrectPercentage(double incorrectPercentage) {
        this.incorrectPercentage = incorrectPercentage;
    }

    public double getUnattemptedPercentage() {
        return unattemptedPercentage;
    }

    public void setUnattemptedPercentage(double unattemptedPercentage) {
        this.unattemptedPercentage = unattemptedPercentage;
    }

    public List<QuestionAnalysisDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(
            List<QuestionAnalysisDTO> questions
    ) {
        this.questions = questions;
    }

    public List<StudentPerformanceDTO> getStudents() {
        return students;
    }

    public void setStudents(
            List<StudentPerformanceDTO> students
    ) {
        this.students = students;
    }
}