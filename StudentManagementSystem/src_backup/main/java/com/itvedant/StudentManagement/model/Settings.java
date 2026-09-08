package com.itvedant.StudentManagement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "settings")
public class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean showStatistics = true;

    @Column(nullable = false)
    private boolean allowMultipleEnrollment = true;

    @Column(nullable = false)
    private boolean preventDuplicateEnrollment = true;

    @Column(nullable = false)
    private boolean studentIdFormatEnabled = true;

    @Column(nullable = false)
    private int maxCoursesPerStudent = 5;

    @Column(nullable = false)
    private String studentIdPrefix = "STU";

    @Column(nullable = false)
    private String currency = "INR";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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