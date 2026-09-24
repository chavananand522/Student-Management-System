package com.itvedant.StudentManagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itvedant.StudentManagement.model.Chapter;
import com.itvedant.StudentManagement.model.Module;
import com.itvedant.StudentManagement.services.ChapterService;
import com.itvedant.StudentManagement.services.ModuleService;

@Controller
@RequestMapping("/chapter")
public class ChapterController {

	private final ChapterService chapterService;
	private final ModuleService moduleService;

	public ChapterController(ChapterService chapterService, ModuleService moduleService) {

		this.chapterService = chapterService;
		this.moduleService = moduleService;
	}

	// /chapter
	@GetMapping
	public String chapterHome() {
		return "redirect:/chapter/list";
	}

	// /chapter/list
	@GetMapping("/list")
	public String chapterList(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			Model model) {

		model.addAttribute("chapters", chapterService.getChapters(page, size));

		return "chapter-list";
	}

	// /chapter/new?moduleId=1
	@GetMapping("/new")
	public String addChapter(@RequestParam Long moduleId, Model model) {

		Module module = moduleService.getModuleById(moduleId);

		Chapter chapter = new Chapter();

		chapter.setSubject(module.getSubject());
		chapter.setModuleId(module.getId());

		model.addAttribute("chapter", chapter);
		model.addAttribute("module", module);

		return "add-chapter";
	}

	// Save Chapter
	@PostMapping("/list")
	public String saveChapter(Chapter chapter, RedirectAttributes redirectAttributes) {

		Chapter savedChapter = chapterService.createChapter(chapter);

		Module module = moduleService.getModuleById(savedChapter.getModuleId());

		redirectAttributes.addFlashAttribute("message", "Chapter added successfully.");

		return "redirect:/study/" + module.getSubject().toLowerCase() + "/module/" + module.getId();
	}

	// /chapter/{id} -> chapter details
	@GetMapping("/{id}")
	public String viewChapter(@PathVariable Long id, Model model) {

		Chapter chapter = chapterService.getChapterById(id);

		Module module = moduleService.getModuleById(chapter.getModuleId());

		model.addAttribute("chapter", chapter);
		model.addAttribute("module", module);
		model.addAttribute("subjectName", module.getSubject());

		return "chapter-view";
	}

	// /chapter/{id}/edit
	@GetMapping("/{id}/edit")
	public String editChapter(@PathVariable Long id, Model model) {

		Chapter chapter = chapterService.getChapterById(id);

		Module module = moduleService.getModuleById(chapter.getModuleId());

		model.addAttribute("chapter", chapter);
		model.addAttribute("module", module);

		return "chapter-edit";
	}

	// Update Chapter
	@PostMapping("/{id}/update")
	public String updateChapter(@PathVariable Long id, Chapter chapter, RedirectAttributes redirectAttributes) {

		Chapter existingChapter = chapterService.getChapterById(id);

		chapter.setSubject(existingChapter.getSubject());
		chapter.setModuleId(existingChapter.getModuleId());

		chapterService.updateChapter(id, chapter);

		Module module = moduleService.getModuleById(existingChapter.getModuleId());

		redirectAttributes.addFlashAttribute("message", "Chapter updated successfully.");

		return "redirect:/study/" + module.getSubject().toLowerCase() + "/module/" + module.getId();
	}

	// Delete Chapter
	@PostMapping("/{id}/delete")
	public String deleteChapter(@PathVariable Long id, RedirectAttributes redirectAttributes) {

		Chapter chapter = chapterService.getChapterById(id);

		Long moduleId = chapter.getModuleId();

		Module module = moduleService.getModuleById(moduleId);

		chapterService.deleteChapter(id);

		redirectAttributes.addFlashAttribute("message", "Chapter deleted successfully.");

		return "redirect:/study/" + module.getSubject().toLowerCase() + "/module/" + module.getId();
	}
}