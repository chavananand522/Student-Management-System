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
import com.itvedant.StudentManagement.services.ChapterService;
import com.itvedant.StudentManagement.services.TopicService;

@Controller
@RequestMapping("/chapter")
public class ChapterController {

	private final ChapterService chapterService;
	private final TopicService topicService;

	public ChapterController(ChapterService chapterService, TopicService topicService) {

		this.chapterService = chapterService;
		this.topicService = topicService;
	}

	@GetMapping
	public String chapterHome() {
		return "redirect:/chapter/list";
	}

	@GetMapping("/list")
	public String chapterList(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			Model model) {

		model.addAttribute("chapters", chapterService.getChapters(page, size));

		return "chapter-list";
	}

	@GetMapping("/new")
	public String addChapter(@RequestParam(required = false) String subject, Model model) {

		String normalizedSubject = normalizeSubject(subject);

		Chapter chapter = new Chapter();
		chapter.setSubject(normalizedSubject);

		model.addAttribute("chapter", chapter);
		model.addAttribute("subject", normalizedSubject);

		return "add-chapter";
	}

	@PostMapping("/list")
	public String saveChapter(Chapter chapter, RedirectAttributes redirectAttributes) {

		String subject = normalizeSubject(chapter.getSubject());

		chapter.setSubject(subject);

		chapterService.createChapter(chapter);

		redirectAttributes.addFlashAttribute("message", "Chapter added successfully.");

		return "redirect:/study/" + subject.toLowerCase();
	}

	@GetMapping("/{id}")
	public String viewChapter(@PathVariable Long id, Model model) {

		Chapter chapter = chapterService.getChapterById(id);

		model.addAttribute("chapter", chapter);

		model.addAttribute("topics", topicService.getTopicsByChapterId(chapter.getId()));

		return "chapter-view";
	}

	@GetMapping("/{id}/edit")
	public String editChapter(@PathVariable Long id, Model model) {

		Chapter chapter = chapterService.getChapterById(id);

		model.addAttribute("chapter", chapter);

		return "chapter-edit";
	}

	@PostMapping("/{id}/update")
	public String updateChapter(@PathVariable Long id, Chapter chapter, RedirectAttributes redirectAttributes) {

		Chapter existingChapter = chapterService.getChapterById(id);

		chapter.setSubject(existingChapter.getSubject());

		chapterService.updateChapter(id, chapter);

		redirectAttributes.addFlashAttribute("message", "Chapter updated successfully.");

		return "redirect:/study/" + existingChapter.getSubject().toLowerCase();
	}

	@PostMapping("/{id}/delete")
	public String deleteChapter(@PathVariable Long id, RedirectAttributes redirectAttributes) {

		Chapter chapter = chapterService.getChapterById(id);

		String subject = chapter.getSubject();

		chapterService.deleteChapter(id);

		redirectAttributes.addFlashAttribute("message", "Chapter deleted successfully.");

		return "redirect:/study/" + subject.toLowerCase();
	}

	private String normalizeSubject(String subject) {

		if (subject == null) {
			return "";
		}

		if (subject.equalsIgnoreCase("physics")) {
			return "Physics";
		}

		if (subject.equalsIgnoreCase("chemistry")) {
			return "Chemistry";
		}

		if (subject.equalsIgnoreCase("biology")) {
			return "Biology";
		}

		return subject;
	}
}