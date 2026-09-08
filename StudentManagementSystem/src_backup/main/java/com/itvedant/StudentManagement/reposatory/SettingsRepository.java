package com.itvedant.StudentManagement.reposatory;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itvedant.StudentManagement.model.Settings;

public interface SettingsRepository
        extends JpaRepository<Settings, Long> {

    Optional<Settings> findTopByOrderByIdAsc();
}