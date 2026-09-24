package com.itvedant.StudentManagement.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tests")
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String testName;

    private String course;

    /*
     * Stores multiple subjects.
     * Example:
     * Biology | Chemistry | Physics
     */
    @Column(name = "subject", columnDefinition = "TEXT")
    private String subject;

    /*
     * Stores multiple subject + chapter combinations.
     * Example:
     *
     * Biology: The Living World |
     * Biology: Plant Kingdom |
     * Chemistry: Thermodynamics |
     * Physics: Laws of Motion
     */
    @Column(name = "chapter", columnDefinition = "TEXT")
    private String chapter;

    private Integer duration;

    private Integer totalMarks;

    private Integer passingMarks;

    private Boolean shuffleQuestions;

    private Boolean shuffleOptions;

    private Boolean showResultImmediately;

    private Boolean allowTestRetake;

    private Integer numberOfAttempts;

    private String status;

    @OneToMany(
            mappedBy = "test",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Question> questions = new ArrayList<>();

    public Test() {
    }

    // =========================================================
    // ID
    // =========================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // =========================================================
    // TEST NAME
    // =========================================================

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    // =========================================================
    // COURSE
    // =========================================================

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    // =========================================================
    // SUBJECT
    // =========================================================

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    // =========================================================
    // CHAPTER
    // =========================================================

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    // =========================================================
    // DURATION
    // =========================================================

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    // =========================================================
    // TOTAL MARKS
    // =========================================================

    public Integer getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(Integer totalMarks) {
        this.totalMarks = totalMarks;
    }

    // =========================================================
    // PASSING MARKS
    // =========================================================

    public Integer getPassingMarks() {
        return passingMarks;
    }

    public void setPassingMarks(Integer passingMarks) {
        this.passingMarks = passingMarks;
    }

    // =========================================================
    // SHUFFLE QUESTIONS
    // =========================================================

    public Boolean getShuffleQuestions() {
        return shuffleQuestions;
    }

    public void setShuffleQuestions(Boolean shuffleQuestions) {
        this.shuffleQuestions = shuffleQuestions;
    }

    // =========================================================
    // SHUFFLE OPTIONS
    // =========================================================

    public Boolean getShuffleOptions() {
        return shuffleOptions;
    }

    public void setShuffleOptions(Boolean shuffleOptions) {
        this.shuffleOptions = shuffleOptions;
    }

    // =========================================================
    // SHOW RESULT IMMEDIATELY
    // =========================================================

    public Boolean getShowResultImmediately() {
        return showResultImmediately;
    }

    public void setShowResultImmediately(Boolean showResultImmediately) {
        this.showResultImmediately = showResultImmediately;
    }

    // =========================================================
    // ALLOW TEST RETAKE
    // =========================================================

    public Boolean getAllowTestRetake() {
        return allowTestRetake;
    }

    public void setAllowTestRetake(Boolean allowTestRetake) {
        this.allowTestRetake = allowTestRetake;
    }

    // =========================================================
    // NUMBER OF ATTEMPTS
    // =========================================================

    public Integer getNumberOfAttempts() {
        return numberOfAttempts;
    }

    public void setNumberOfAttempts(Integer numberOfAttempts) {
        this.numberOfAttempts = numberOfAttempts;
    }

    // =========================================================
    // STATUS
    // =========================================================

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // =========================================================
    // QUESTIONS
    // =========================================================

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    // =========================================================
    // ADD QUESTION
    // =========================================================

    public void addQuestion(Question question) {

        questions.add(question);

        question.setTest(this);
    }

    // =========================================================
    // REMOVE QUESTION
    // =========================================================

    public void removeQuestion(Question question) {

        questions.remove(question);

        question.setTest(null);
    }
}