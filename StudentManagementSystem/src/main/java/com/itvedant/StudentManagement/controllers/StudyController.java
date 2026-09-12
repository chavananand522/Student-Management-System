package com.itvedant.StudentManagement.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/study")
public class StudyController {

    @GetMapping
    public String study() {
        return "study";
    }

    @GetMapping("/{subject}")
    public String subject(@PathVariable String subject, Model model) {

        if (!subject.equalsIgnoreCase("physics")
                && !subject.equalsIgnoreCase("chemistry")
                && !subject.equalsIgnoreCase("biology")) {

            return "redirect:/study";
        }

        String subjectName;

        if (subject.equalsIgnoreCase("physics")) {
            subjectName = "Physics";
        } else if (subject.equalsIgnoreCase("chemistry")) {
            subjectName = "Chemistry";
        } else {
            subjectName = "Biology";
        }

        model.addAttribute("subjectName", subjectName);

        return "subject";
    }
}