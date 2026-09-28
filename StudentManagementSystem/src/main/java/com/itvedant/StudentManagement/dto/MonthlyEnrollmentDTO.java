package com.itvedant.StudentManagement.dto;

public class MonthlyEnrollmentDTO {

    private String month;

    private long studentCount;

    // =========================================================
    // DEFAULT CONSTRUCTOR
    // =========================================================

    public MonthlyEnrollmentDTO() {
    }

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public MonthlyEnrollmentDTO(
            String month,
            long studentCount) {

        this.month = month;
        this.studentCount = studentCount;
    }

    // =========================================================
    // GET MONTH
    // =========================================================

    public String getMonth() {
        return month;
    }

    // =========================================================
    // SET MONTH
    // =========================================================

    public void setMonth(String month) {
        this.month = month;
    }

    // =========================================================
    // GET STUDENT COUNT
    // =========================================================

    public long getStudentCount() {
        return studentCount;
    }

    // =========================================================
    // SET STUDENT COUNT
    // =========================================================

    public void setStudentCount(long studentCount) {
        this.studentCount = studentCount;
    }
}