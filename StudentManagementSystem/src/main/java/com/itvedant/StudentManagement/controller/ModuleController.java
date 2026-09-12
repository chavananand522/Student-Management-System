package com.itvedant.StudentManagement.controller;

import com.itvedant.StudentManagement.model.Module;
import com.itvedant.StudentManagement.services.ModuleService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/module")
public class ModuleController {

    private static final Logger log =
            LoggerFactory.getLogger(ModuleController.class);

    private final ModuleService moduleService;

    public ModuleController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    // ---------------------------------------------------------
    // OPEN MODULE LIST
    // ---------------------------------------------------------

    @GetMapping("")
    public String redirectToList() {

        log.info("Get /module - redirecting to /module/list");

        return "redirect:/module/list";
    }

    // ---------------------------------------------------------
    // SHOW ADD MODULE PAGE
    // ---------------------------------------------------------

    @GetMapping("/new")
    public String showCreateModule(
            @RequestParam(defaultValue = "Physics") String subject,
            Model model) {

        log.info("Get /module/new - subject: {}", subject);

        String subjectName = normalizeSubject(subject);

        if (subjectName == null) {
            return "redirect:/study";
        }

        Module module = new Module();

        // Automatically set subject
        module.setSubject(subjectName);

        model.addAttribute("module", module);

        return "add-module";
    }

    // ---------------------------------------------------------
    // CREATE MODULE
    // ---------------------------------------------------------

    @PostMapping("/list")
    public String createModule(
            @ModelAttribute("module") Module module,
            RedirectAttributes redirectAttributes) {

        log.info("Post /module/list - create module request received.");

        String subjectName = normalizeSubject(module.getSubject());

        if (subjectName == null) {
            return "redirect:/study";
        }

        // Make sure correct subject is stored
        module.setSubject(subjectName);

        moduleService.createModule(module);

        redirectAttributes.addFlashAttribute(
                "message",
                "Module is created successfully"
        );

        log.info(
                "Module '{}' added successfully for {}",
                module.getName(),
                subjectName
        );

        // Redirect to same subject page
        return "redirect:/study/" + subjectName.toLowerCase();
    }

    // ---------------------------------------------------------
    // VIEW MODULE
    // ---------------------------------------------------------

    @GetMapping("/{id}")
    public String getModuleById(
            @PathVariable Long id,
            Model model) {

        log.info("Get /module/{} - showing module details.", id);

        Module module = moduleService.getModuleById(id);

        model.addAttribute("module", module);

        return "module-view";
    }

    // ---------------------------------------------------------
    // SHOW EDIT MODULE PAGE
    // ---------------------------------------------------------

    @GetMapping("/{id}/edit")
    public String editModule(
            @PathVariable Long id,
            Model model) {

        log.info(
                "Get /module/{}/edit - showing edit module page.",
                id
        );

        Module module = moduleService.getModuleById(id);

        model.addAttribute("module", module);

        return "module-edit";
    }

    // ---------------------------------------------------------
    // UPDATE MODULE
    // ---------------------------------------------------------

    @PostMapping("/{id}/update")
    public String updateModule(
            @PathVariable Long id,
            @ModelAttribute("module") Module module,
            RedirectAttributes redirectAttributes) {

        log.info(
                "Post /module/{}/update - update module request received.",
                id
        );

        // Get original module to keep its subject
        Module existingModule = moduleService.getModuleById(id);

        String subjectName = existingModule.getSubject();

        // Keep subject unchanged
        module.setSubject(subjectName);

        moduleService.updateModule(id, module);

        redirectAttributes.addFlashAttribute(
                "message",
                "Module is updated successfully"
        );

        log.info(
                "Module {} updated successfully.",
                id
        );

        // Redirect to same subject page
        return "redirect:/study/" + subjectName.toLowerCase();
    }

    // ---------------------------------------------------------
    // DELETE MODULE
    // ---------------------------------------------------------

    @PostMapping("/{id}/delete")
    public String deleteModule(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        log.info(
                "Post /module/{}/delete - delete module request received.",
                id
        );

        // Get module before deleting it
        Module module = moduleService.getModuleById(id);

        String subjectName = module.getSubject();

        moduleService.deleteModule(id);

        redirectAttributes.addFlashAttribute(
                "message",
                "Module is deleted successfully"
        );

        log.info(
                "Module {} deleted successfully.",
                id
        );

        // Redirect to same subject page
        return "redirect:/study/" + subjectName.toLowerCase();
    }

    // ---------------------------------------------------------
    // NORMALIZE SUBJECT
    // ---------------------------------------------------------

    private String normalizeSubject(String subject) {

        if (subject == null) {
            return null;
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

        return null;
    }
}