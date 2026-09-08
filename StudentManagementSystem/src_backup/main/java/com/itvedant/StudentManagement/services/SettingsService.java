package com.itvedant.StudentManagement.services;

import com.itvedant.StudentManagement.dto.SettingsDTO;

public interface SettingsService {

    SettingsDTO getSettings();

    void updateSettings(SettingsDTO dto);

    boolean isShowStatistics();

    boolean isAllowMultipleEnrollment();

    boolean isPreventDuplicateEnrollment();

    boolean isStudentIdFormatEnabled();

    int getMaxCoursesPerStudent();

    String getStudentIdPrefix();

    String getCurrency();
}