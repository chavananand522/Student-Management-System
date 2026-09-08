package com.itvedant.StudentManagement.dto;

public class EnrollmentExcelDTO {

    private Long enrollmentId;

    private Long courseId;

    private String courseName;

    private Long studentId;

    private String studentName;

    public EnrollmentExcelDTO() {
    }

    public EnrollmentExcelDTO(
            Long enrollmentId,
            Long courseId,
            String courseName,
            Long studentId,
            String studentName) {

        this.enrollmentId = enrollmentId;
        this.courseId = courseId;
        this.courseName = courseName;
        this.studentId = studentId;
        this.studentName = studentName;
    }

    public Long getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(Long enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
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
}