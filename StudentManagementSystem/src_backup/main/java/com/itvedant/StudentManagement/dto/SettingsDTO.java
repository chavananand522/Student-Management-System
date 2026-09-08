package com.itvedant.StudentManagement.dto;

public class SettingsDTO {

    private boolean showStatistics;

    private boolean allowMultipleEnrollment;

    private boolean preventDuplicateEnrollment;

    private boolean studentIdFormatEnabled;

    private int maxCoursesPerStudent;

    private String studentIdPrefix;

    private String currency;

    public boolean isShowStatistics() {
        return showStatistics;
    }

    public void setShowStatistics(boolean showStatistics) {
        this.showStatistics = showStatistics;
    }

    public boolean isAllowMultipleEnrollment() {
        return allowMultipleEnrollment;
    }

    public void setAllowMultipleEnrollment(boolean allowMultipleEnrollment) {
        this.allowMultipleEnrollment = allowMultipleEnrollment;
    }

    public boolean isPreventDuplicateEnrollment() {
        return preventDuplicateEnrollment;
    }

    public void setPreventDuplicateEnrollment(boolean preventDuplicateEnrollment) {
        this.preventDuplicateEnrollment = preventDuplicateEnrollment;
    }

    public boolean isStudentIdFormatEnabled() {
        return studentIdFormatEnabled;
    }

    public void setStudentIdFormatEnabled(boolean studentIdFormatEnabled) {
        this.studentIdFormatEnabled = studentIdFormatEnabled;
    }

    public int getMaxCoursesPerStudent() {
        return maxCoursesPerStudent;
    }

    public void setMaxCoursesPerStudent(int maxCoursesPerStudent) {
        this.maxCoursesPerStudent = maxCoursesPerStudent;
    }

    public String getStudentIdPrefix() {
        return studentIdPrefix;
    }

    public void setStudentIdPrefix(String studentIdPrefix) {
        this.studentIdPrefix = studentIdPrefix;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}