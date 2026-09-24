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
import com.itvedant.StudentManagement.reposatory.ModuleRepository;
import com.itvedant.StudentManagement.services.ChapterService;
import com.itvedant.StudentManagement.services.ModuleService;

@Controller
@RequestMapping("/module")
public class ModuleController {

	private final ModuleService moduleService;
	private final ModuleRepository moduleRepository;
	private final ChapterService chapterService;

	public ModuleController(ModuleService moduleService, ModuleRepository moduleRepository,
			ChapterService chapterService) {

		this.moduleService = moduleService;
		this.moduleRepository = moduleRepository;
		this.chapterService = chapterService;
	}

	// /module/new?subject=Physics
	@GetMapping("/new")
	public String addModule(@RequestParam String subject, Model model) {

		Module module = new Module();

		module.setSubject(subject);

		// suggest the next module number
		module.setModuleNumber(moduleService.getModulesBySubject(subject).size() + 1);

		model.addAttribute("module", module);

		return "add-module";
	}

	// Save Module
	@PostMapping("/save")
	public String saveModule(Module module, RedirectAttributes redirectAttributes) {

		Module saved = moduleRepository.save(module);

		redirectAttributes.addFlashAttribute("message", "Module added successfully.");

		return "redirect:/study/" + saved.getSubject().toLowerCase();
	}

	// /module/{id} -> module details (view page)
	@GetMapping("/{id}")
	public String viewModule(@PathVariable Long id, Model model) {

		Module module = moduleService.getModuleById(id);

		model.addAttribute("module", module);
		model.addAttribute("subjectName", module.getSubject());
		model.addAttribute("chapterCount", chapterService.getChaptersByModuleId(id).size());

		return "module-details";
	}

	// /module/{id}/edit
	@GetMapping("/{id}/edit")
	public String editModule(@PathVariable Long id, Model model) {

		model.addAttribute("module", moduleService.getModuleById(id));

		return "module-edit";
	}

	// Update Module
	@PostMapping("/{id}/update")
	public String updateModule(@PathVariable Long id, Module module, RedirectAttributes redirectAttributes) {

		Module existing = moduleService.getModuleById(id);

		existing.setModuleNumber(module.getModuleNumber());
		existing.setName(module.getName());
		existing.setDescription(module.getDescription());

		moduleRepository.save(existing);

		redirectAttributes.addFlashAttribute("message", "Module updated successfully.");

		return "redirect:/study/" + existing.getSubject().toLowerCase();
	}

	// Delete Module (and all chapters inside it)
	@PostMapping("/{id}/delete")
	public String deleteModule(@PathVariable Long id, RedirectAttributes redirectAttributes) {

		Module module = moduleService.getModuleById(id);

		String subject = module.getSubject();

		for (Chapter chapter : chapterService.getChaptersByModuleId(id)) {
			chapterService.deleteChapter(chapter.getId());
		}

		moduleRepository.deleteById(id);

		redirectAttributes.addFlashAttribute("message", "Module deleted successfully.");

		return "redirect:/study/" + subject.toLowerCase();
	}
}