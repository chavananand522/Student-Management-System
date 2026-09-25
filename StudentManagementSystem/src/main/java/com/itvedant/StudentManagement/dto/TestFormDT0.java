package com.itvedant.StudentManagement.dto;

import java.util.ArrayList;
import java.util.List;

public class TestFormDT0 {

    private String testName;

    private String course;

    private Integer duration;

    private Integer totalMarks;

    private String status = "DRAFT";

    private List<String> biologyChapter = new ArrayList<>();

    private List<String> chemistryChapter = new ArrayList<>();

    private List<String> physicsChapter = new ArrayList<>();

    private Boolean shuffleQuestions = false;

    private Boolean shuffleOptions = false;

    private Boolean showResultImmediately = true;

    private Boolean allowTestRetake = false;

    private Integer numberOfAttempts = 1;

    private List<QuestionFormDTO> questions = new ArrayList<>();

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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(Integer totalMarks) {
        this.totalMarks = totalMarks;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getBiologyChapter() {
        return biologyChapter;
    }

    public void setBiologyChapter(List<String> biologyChapter) {
        this.biologyChapter = biologyChapter;
    }

    public List<String> getChemistryChapter() {
        return chemistryChapter;
    }

    public void setChemistryChapter(List<String> chemistryChapter) {
        this.chemistryChapter = chemistryChapter;
    }

    public List<String> getPhysicsChapter() {
        return physicsChapter;
    }

    public void setPhysicsChapter(List<String> physicsChapter) {
        this.physicsChapter = physicsChapter;
    }

    public Boolean getShuffleQuestions() {
        return shuffleQuestions;
    }

    public void setShuffleQuestions(Boolean shuffleQuestions) {
        this.shuffleQuestions = shuffleQuestions;
    }

    public Boolean getShuffleOptions() {
        return shuffleOptions;
    }

    public void setShuffleOptions(Boolean shuffleOptions) {
        this.shuffleOptions = shuffleOptions;
    }

    public Boolean getShowResultImmediately() {
        return showResultImmediately;
    }

    public void setShowResultImmediately(Boolean showResultImmediately) {
        this.showResultImmediately = showResultImmediately;
    }

    public Boolean getAllowTestRetake() {
        return allowTestRetake;
    }

    public void setAllowTestRetake(Boolean allowTestRetake) {
        this.allowTestRetake = allowTestRetake;
    }

    public Integer getNumberOfAttempts() {
        return numberOfAttempts;
    }

    public void setNumberOfAttempts(Integer numberOfAttempts) {
        this.numberOfAttempts = numberOfAttempts;
    }

    public List<QuestionFormDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionFormDTO> questions) {
        this.questions = questions;
    }
}