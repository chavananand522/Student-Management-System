package com.itvedant.StudentManagement.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name="courses")
public class Courses {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id; 
	
	@Column(nullable=false)
	private String courseName;
	
	@Column(nullable=false , unique =true)
	private String courseCode;
	
	private String duration;
	
	@Column(name="active",nullable=false)
	private boolean active =true;
	
	@Column (precision=12,scale =2, nullable =false)
	private BigDecimal fee;

	@Column(length = 1000) 
	private String description;
	
	@Column(nullable=false,updatable=false)
	private LocalDateTime createdAt;
	
	@PrePersist
	public void onCreate() {
		createdAt=LocalDateTime.now();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getCourseCode() {
		return courseCode;
	}

	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public String getDuration() {
	    return duration;
	}

	public void setDuration(String duration) {
	    this.duration = duration;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
	
}
