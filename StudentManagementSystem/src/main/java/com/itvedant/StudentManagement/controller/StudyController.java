package com.itvedant.StudentManagement.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.itvedant.StudentManagement.model.Chapter;
import com.itvedant.StudentManagement.model.Module;
import com.itvedant.StudentManagement.model.Study;
import com.itvedant.StudentManagement.services.ChapterService;
import com.itvedant.StudentManagement.services.ModuleService;
import com.itvedant.StudentManagement.services.StudyService;

@Controller
@RequestMapping("/study")
public class StudyController {

	private final StudyService studyService;
	private final ChapterService chapterService;
	private final ModuleService moduleService;

	public StudyController(StudyService studyService, ChapterService chapterService, ModuleService moduleService) {

		this.studyService = studyService;
		this.chapterService = chapterService;
		this.moduleService = moduleService;
	}

	// /study
	@GetMapping
	public String study(Model model) {

		List<Study> subjects = studyService.getAllSubjects();

		model.addAttribute("subjects", subjects);

		return "study";
	}

	// /study/physics
	// Shows Modules inside Physics
	@GetMapping("/{subject}")
	public String subject(@PathVariable String subject, Model model) {

		Study study = studyService.getSubjectByName(subject);

		if (study == null) {
			return "redirect:/study";
		}

		List<Module> modules = moduleService.getModulesBySubject(study.getSubject());

		model.addAttribute("study", study);
		model.addAttribute("subjectName", study.getSubject());
		model.addAttribute("modules", modules);

		return "study";
	}

	// /study/physics/module/1
	// Shows Chapters inside a Module
	@GetMapping("/{subject}/module/{moduleId}")
	public String module(@PathVariable String subject, @PathVariable Long moduleId, Model model) {

		Study study = studyService.getSubjectByName(subject);

		if (study == null) {
			return "redirect:/study";
		}

		Module module = moduleService.getModuleById(moduleId);

		if (module == null || !module.getSubject().equalsIgnoreCase(study.getSubject())) {
			return "redirect:/study/" + subject.toLowerCase();
		}

		List<Chapter> chapters = chapterService.getChaptersByModuleId(moduleId);

		model.addAttribute("study", study);
		model.addAttribute("subjectName", study.getSubject());
		model.addAttribute("module", module);
		model.addAttribute("chapters", chapters);

		return "module-view";
	}

	// /study/physics/module/1/chapter/1
	// Shows individual Chapter
	@GetMapping("/{subject}/module/{moduleId}/chapter/{chapterId}")
	public String chapter(@PathVariable String subject, @PathVariable Long moduleId, @PathVariable Long chapterId,
			Model model) {

		Study study = studyService.getSubjectByName(subject);

		if (study == null) {
			return "redirect:/study";
		}

		Module module = moduleService.getModuleById(moduleId);

		Chapter chapter = chapterService.getChapterById(chapterId);

		if (module == null || chapter == null || !moduleId.equals(chapter.getModuleId())) {
			return "redirect:/study/" + subject.toLowerCase() + "/module/" + moduleId;
		}

		model.addAttribute("study", study);
		model.addAttribute("subjectName", study.getSubject());
		model.addAttribute("module", module);
		model.addAttribute("chapter", chapter);

		return "chapter-view";
	}
}