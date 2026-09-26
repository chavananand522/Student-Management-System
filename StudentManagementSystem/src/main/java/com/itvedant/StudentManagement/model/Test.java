package com.itvedant.StudentManagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tests")
public class Test {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String testName;

	private String course;

	private String subject;

	@Column(length = 2000)
	private String chapter;

	private Integer duration;

	private Integer totalMarks;

	private Integer passingMarks;

	private Boolean shuffleQuestions = false;

	private Boolean shuffleOptions = false;

	private Boolean showResultImmediately = true;

	private Boolean allowTestRetake = false;

	private Integer numberOfAttempts = 1;

	private String status = "DRAFT";

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	@OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("id ASC")
	private List<Question> questions = new ArrayList<>();

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();

		if (status == null || status.isBlank()) {
			status = "DRAFT";
		}

		if (numberOfAttempts == null || numberOfAttempts < 1) {
			numberOfAttempts = 1;
		}

		if (passingMarks == null && totalMarks != null) {
			passingMarks = (int) Math.ceil(totalMarks * 0.40);
		}
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public void addQuestion(Question question) {
		questions.add(question);
		question.setTest(this);
	}

	public void removeQuestion(Question question) {
		questions.remove(question);
		question.setTest(null);
	}

	public Long getId() {
		return id;
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

	public Integer getPassingMarks() {
		return passingMarks;
	}

	public void setPassingMarks(Integer passingMarks) {
		this.passingMarks = passingMarks;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public List<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;

		if (questions != null) {
			for (Question question : questions) {
				question.setTest(this);
			}
		}
	}
}