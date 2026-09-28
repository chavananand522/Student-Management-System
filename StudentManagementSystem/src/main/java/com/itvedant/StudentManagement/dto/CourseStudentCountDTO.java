package com.itvedant.StudentManagement.dto;

public class CourseStudentCountDTO {

    private String courseName;

    private long studentCount;

    public CourseStudentCountDTO() {
    }

    public CourseStudentCountDTO(
            String courseName,
            long studentCount) {

        this.courseName = courseName;
        this.studentCount = studentCount;
    }

    // =========================================================
    // GETTER
    // =========================================================

    public String getCourseName() {
        return courseName;
    }

    // =========================================================
    // SETTER
    // =========================================================

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    // =========================================================
    // GETTER
    // =========================================================

    public long getStudentCount() {
        return studentCount;
    }

    // =========================================================
    // SETTER
    // =========================================================

    public void setStudentCount(long studentCount) {
        this.studentCount = studentCount;
    }
}