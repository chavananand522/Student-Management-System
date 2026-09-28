package com.itvedant.StudentManagement.dto;

import java.util.ArrayList;
import java.util.List;

public class DashboardStatsDTO {

    private long totalStudents;

    private long totalCourses;

    private String topPerformingCourse;

    private long studentEntolledThisMonth;

    /**
     * Dynamic data used by
     * "Students by Course".
     */
    private List<CourseStudentCountDTO> studentsByCourse =
            new ArrayList<>();

    /**
     * Dynamic data used by
     * "Student Enrollment Trend".
     */
    private List<MonthlyEnrollmentDTO> monthlyEnrollment =
            new ArrayList<>();

    // =========================================================
    // DEFAULT CONSTRUCTOR
    // =========================================================

    public DashboardStatsDTO() {
    }

    // =========================================================
    // EXISTING CONSTRUCTOR
    // =========================================================

    public DashboardStatsDTO(
            long totalStudents,
            long totalCourses,
            String topPerformingCourse,
            long studentEntolledThisMonth) {

        this.totalStudents = totalStudents;
        this.totalCourses = totalCourses;
        this.topPerformingCourse = topPerformingCourse;
        this.studentEntolledThisMonth =
                studentEntolledThisMonth;
    }

    // =========================================================
    // GET TOTAL STUDENTS
    // =========================================================

    public long getTotalStudents() {
        return totalStudents;
    }

    // =========================================================
    // SET TOTAL STUDENTS
    // =========================================================

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    // =========================================================
    // GET TOTAL COURSES
    // =========================================================

    public long getTotalCourses() {
        return totalCourses;
    }

    // =========================================================
    // SET TOTAL COURSES
    // =========================================================

    public void setTotalCourses(long totalCourses) {
        this.totalCourses = totalCourses;
    }

    // =========================================================
    // GET TOP PERFORMING COURSE
    // =========================================================

    public String getTopPerformingCourse() {
        return topPerformingCourse;
    }

    // =========================================================
    // SET TOP PERFORMING COURSE
    // =========================================================

    public void setTopPerformingCourse(
            String topPerformingCourse) {

        this.topPerformingCourse =
                topPerformingCourse;
    }

    // =========================================================
    // GET STUDENTS ENROLLED THIS MONTH
    // =========================================================

    public long getStudentEntolledThisMonth() {
        return studentEntolledThisMonth;
    }

    // =========================================================
    // SET STUDENTS ENROLLED THIS MONTH
    // =========================================================

    public void setStudentEntolledThisMonth(
            long studentEntolledThisMonth) {

        this.studentEntolledThisMonth =
                studentEntolledThisMonth;
    }

    // =========================================================
    // GET STUDENTS BY COURSE
    // =========================================================

    public List<CourseStudentCountDTO> getStudentsByCourse() {
        return studentsByCourse;
    }

    // =========================================================
    // SET STUDENTS BY COURSE
    // =========================================================

    public void setStudentsByCourse(
            List<CourseStudentCountDTO> studentsByCourse) {

        this.studentsByCourse =
                studentsByCourse;
    }

    // =========================================================
    // GET MONTHLY ENROLLMENT
    // =========================================================

    public List<MonthlyEnrollmentDTO> getMonthlyEnrollment() {
        return monthlyEnrollment;
    }

    // =========================================================
    // SET MONTHLY ENROLLMENT
    // =========================================================

    public void setMonthlyEnrollment(
            List<MonthlyEnrollmentDTO> monthlyEnrollment) {

        this.monthlyEnrollment =
                monthlyEnrollment;
    }
}