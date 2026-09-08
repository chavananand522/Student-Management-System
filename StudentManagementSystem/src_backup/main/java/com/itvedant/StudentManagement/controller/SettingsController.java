
package com.itvedant.StudentManagement.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.itvedant.StudentManagement.dto.SettingsDTO;
import com.itvedant.StudentManagement.services.SettingsService;

@Controller
@RequestMapping("/settings")
public class SettingsController {

    private static final Logger logger =
            LoggerFactory.getLogger(SettingsController.class);

    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
        logger.info("SettingsController initialized!");
    }

    // Open Settings page
    @GetMapping("")
    public String showSettings(Model model) {

        logger.info("GET /settings called");

        SettingsDTO settingsDTO = settingsService.getSettings();

        model.addAttribute("settings", settingsDTO);

        return "settings";
    }

    // Update Settings
    @PostMapping("")
    public String updateSettings(SettingsDTO settingsDTO) {

        logger.info("POST /settings called");

        settingsService.updateSettings(settingsDTO);

        return "redirect:/settings?success";
    }
}

