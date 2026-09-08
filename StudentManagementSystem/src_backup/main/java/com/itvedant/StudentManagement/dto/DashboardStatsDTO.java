package com.itvedant.StudentManagement.dto;

public class DashboardStatsDTO {

    private long totalStudents;
    private long totalCourses;
    private String topPerformingCourse;
    private long studentEntolledThisMonth;

    public DashboardStatsDTO() {
    }

    public DashboardStatsDTO(long totalStudents, long totalCourses,
                             String topPerformingCourse, long studentEntolledThisMonth) {
        this.totalStudents = totalStudents;
        this.totalCourses = totalCourses;
        this.topPerformingCourse = topPerformingCourse;
        this.studentEntolledThisMonth = studentEntolledThisMonth;
    }

    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public long getTotalCourses() {
        return totalCourses;
    }

    public void setTotalCourses(long totalCourses) {
        this.totalCourses = totalCourses;
    }

    public String getTopPerformingCourse() {
        return topPerformingCourse;
    }

    public void setTopPerformingCourse(String topPerformingCourse) {
        this.topPerformingCourse = topPerformingCourse;
    }

    public long getStudentEntolledThisMonth() {
        return studentEntolledThisMonth;
    }

    public void setStudentEntolledThisMonth(long studentEntolledThisMonth) {
        this.studentEntolledThisMonth = studentEntolledThisMonth;
    }
}