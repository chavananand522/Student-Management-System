package com.itvedant.StudentManagement.dto;

public class StudentAttendanceDTO {

    private Long courseId;
    private String courseName;
    private String courseCode;
    private long totalClasses;
    private long presentCount;
    private long absentCount;
    private long lateCount;
    private double attendancePercentage;

    public StudentAttendanceDTO() {
    }

    public StudentAttendanceDTO(Long courseId,
                                String courseName,
                                String courseCode,
                                long totalClasses,
                                long presentCount,
                                long absentCount,
                                long lateCount) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.totalClasses = totalClasses;
        this.presentCount = presentCount;
        this.absentCount = absentCount;
        this.lateCount = lateCount;

        // WEIGHTED: PRESENT = 1.0, LATE = 0.5, ABSENT = 0.0
        double weightedPresent = presentCount + (lateCount * 0.5);
        this.attendancePercentage = totalClasses > 0
                ? (weightedPresent / totalClasses) * 100.0
                : 0.0;
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

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public long getTotalClasses() {
        return totalClasses;
    }

    public void setTotalClasses(long totalClasses) {
        this.totalClasses = totalClasses;
    }

    public long getPresentCount() {
        return presentCount;
    }

    public void setPresentCount(long presentCount) {
        this.presentCount = presentCount;
    }

    public long getAbsentCount() {
        return absentCount;
    }

    public void setAbsentCount(long absentCount) {
        this.absentCount = absentCount;
    }

    public long getLateCount() {
        return lateCount;
    }

    public void setLateCount(long lateCount) {
        this.lateCount = lateCount;
    }

    public double getAttendancePercentage() {
        return attendancePercentage;
    }

    public void setAttendancePercentage(double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }
}