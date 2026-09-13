package com.itvedant.StudentManagement.dto;

public class StudentCourseFeeDTO {

	private Long studentId;
	private String studentName;
	private String courseIds;
	private String courseNames;
	private String courseFees;
	private double paid;
	private double pending;

	public StudentCourseFeeDTO() {
	}

	public StudentCourseFeeDTO(Long studentId, String studentName, String courseIds, String courseNames,
			String courseFees, double paid, double pending) {

		this.studentId = studentId;
		this.studentName = studentName;
		this.courseIds = courseIds;
		this.courseNames = courseNames;
		this.courseFees = courseFees;
		this.paid = paid;
		this.pending = pending;
	}

	public Long getStudentId() {
		return studentId;
	}

	public void setStudentId(Long studentId) {
		this.studentId = studentId;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getCourseIds() {
		return courseIds;
	}

	public void setCourseIds(String courseIds) {
		this.courseIds = courseIds;
	}

	public String getCourseNames() {
		return courseNames;
	}

	public void setCourseNames(String courseNames) {
		this.courseNames = courseNames;
	}

	public String getCourseFees() {
		return courseFees;
	}

	public void setCourseFees(String courseFees) {
		this.courseFees = courseFees;
	}

	public double getPaid() {
		return paid;
	}

	public void setPaid(double paid) {
		this.paid = paid;
	}

	public double getPending() {
		return pending;
	}

	public void setPending(double pending) {
		this.pending = pending;
	}
}