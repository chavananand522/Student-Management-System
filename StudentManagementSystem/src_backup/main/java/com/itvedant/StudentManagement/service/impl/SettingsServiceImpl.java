package com.itvedant.StudentManagement.service.impl;

import org.springframework.stereotype.Service;

import com.itvedant.StudentManagement.dto.SettingsDTO;
import com.itvedant.StudentManagement.model.Settings;
import com.itvedant.StudentManagement.reposatory.SettingsRepository;
import com.itvedant.StudentManagement.services.SettingsService;



@Service
public class SettingsServiceImpl implements SettingsService {

    private final SettingsRepository settingsRepository;

    public SettingsServiceImpl(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    private Settings getSettingsEntity() {

        return settingsRepository.findById(1L)
                .orElseGet(() -> {

                    Settings settings = new Settings();

                    settings.setId(1L);
                    settings.setShowStatistics(true);
                    settings.setAllowMultipleEnrollment(true);
                    settings.setPreventDuplicateEnrollment(true);
                    settings.setStudentIdFormatEnabled(true);
                    settings.setMaxCoursesPerStudent(5);
                    settings.setStudentIdPrefix("STU");
                    settings.setCurrency("INR");

                    return settingsRepository.save(settings);
                });
    }

    @Override
    public SettingsDTO getSettings() {

        Settings settings = getSettingsEntity();

        SettingsDTO dto = new SettingsDTO();

        dto.setShowStatistics(settings.isShowStatistics());
        dto.setAllowMultipleEnrollment(settings.isAllowMultipleEnrollment());
        dto.setPreventDuplicateEnrollment(
                settings.isPreventDuplicateEnrollment());

        dto.setStudentIdFormatEnabled(
                settings.isStudentIdFormatEnabled());

        dto.setMaxCoursesPerStudent(
                settings.getMaxCoursesPerStudent());

        dto.setStudentIdPrefix(
                settings.getStudentIdPrefix());

        dto.setCurrency(settings.getCurrency());

        return dto;
    }

    @Override
    public void updateSettings(SettingsDTO dto) {

        Settings settings = getSettingsEntity();

        settings.setShowStatistics(dto.isShowStatistics());
        settings.setAllowMultipleEnrollment(
                dto.isAllowMultipleEnrollment());

        settings.setPreventDuplicateEnrollment(
                dto.isPreventDuplicateEnrollment());

        settings.setStudentIdFormatEnabled(
                dto.isStudentIdFormatEnabled());

        settings.setMaxCoursesPerStudent(
                dto.getMaxCoursesPerStudent());

        settings.setStudentIdPrefix(
                dto.getStudentIdPrefix());

        settings.setCurrency(dto.getCurrency());

        settingsRepository.save(settings);
    }

    @Override
    public boolean isShowStatistics() {
        return getSettingsEntity().isShowStatistics();
    }

    @Override
    public boolean isAllowMultipleEnrollment() {
        return getSettingsEntity().isAllowMultipleEnrollment();
    }

    @Override
    public boolean isPreventDuplicateEnrollment() {
        return getSettingsEntity().isPreventDuplicateEnrollment();
    }

    @Override
    public boolean isStudentIdFormatEnabled() {
        return getSettingsEntity().isStudentIdFormatEnabled();
    }

    @Override
    public int getMaxCoursesPerStudent() {
        return getSettingsEntity().getMaxCoursesPerStudent();
    }

    @Override
    public String getStudentIdPrefix() {
        return getSettingsEntity().getStudentIdPrefix();
    }

    @Override
    public String getCurrency() {
        return getSettingsEntity().getCurrency();
    }
}