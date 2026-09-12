package com.itvedant.StudentManagement.controller;

import com.itvedant.StudentManagement.model.Module;
import com.itvedant.StudentManagement.services.ModuleService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/study")
public class StudyController {

    private final ModuleService moduleService;

    public StudyController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }


    // =========================================================
    // DEFAULT STUDY PAGE
    // =========================================================

    @GetMapping
    public String study(Model model) {

        model.addAttribute(
                "subjectName",
                "Physics"
        );

        model.addAttribute(
                "modules",
                moduleService.getModulesBySubject("Physics")
        );

        return "study";
    }


    // =========================================================
    // SUBJECT PAGE
    // =========================================================

    @GetMapping("/{subject}")
    public String subject(
            @PathVariable String subject,
            Model model) {


        // Check valid subject
        if (!subject.equalsIgnoreCase("physics")
                && !subject.equalsIgnoreCase("chemistry")
                && !subject.equalsIgnoreCase("biology")) {

            return "redirect:/study";
        }


        String subjectName;


        // Physics
        if (subject.equalsIgnoreCase("physics")) {

            subjectName = "Physics";

        }

        // Chemistry
        else if (subject.equalsIgnoreCase("chemistry")) {

            subjectName = "Chemistry";

        }

        // Biology
        else {

            subjectName = "Biology";

        }


        // Get modules for selected subject
        List<Module> modules =
                moduleService.getModulesBySubject(subjectName);


        model.addAttribute(
                "subjectName",
                subjectName
        );

        model.addAttribute(
                "modules",
                modules
        );


        return "study";
    }

}