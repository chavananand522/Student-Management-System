package com.itvedant.StudentManagement.controller;

import com.itvedant.StudentManagement.model.Chapter;
import com.itvedant.StudentManagement.model.Study;
import com.itvedant.StudentManagement.services.ChapterService;
import com.itvedant.StudentManagement.services.StudyService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/study")
public class StudyController {

    private final StudyService studyService;
    private final ChapterService chapterService;

    public StudyController(
            StudyService studyService,
            ChapterService chapterService) {

        this.studyService = studyService;
        this.chapterService = chapterService;
    }

    // =========================================================
    // STUDY HOME
    // =========================================================

    @GetMapping
    public String study(Model model) {

        List<Study> subjects =
                studyService.getAllSubjects();

        model.addAttribute(
                "subjects",
                subjects
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

        Study study =
                studyService.getSubjectByName(subject);

        if (study == null) {
            return "redirect:/study";
        }

        List<Chapter> chapters =
                chapterService.getChaptersBySubject(
                        study.getSubject()
                );

        model.addAttribute(
                "study",
                study
        );

        model.addAttribute(
                "subjectName",
                study.getSubject()
        );

        model.addAttribute(
                "chapters",
                chapters
        );

        return "study";
    }
}