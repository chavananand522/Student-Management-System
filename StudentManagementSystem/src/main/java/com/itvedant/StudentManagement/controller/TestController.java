package com.itvedant.StudentManagement.controller;

import com.itvedant.StudentManagement.model.Test;
import com.itvedant.StudentManagement.services.TestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/tests")
public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping("/create")
    public String showCreateTest(Model model) {
        model.addAttribute("test", new Test());
        return "create-test";
    }

    @PostMapping("/save")
    public String saveTest(@ModelAttribute Test test) {
        System.out.println("TEST NAME = " + test.getTestName());
        System.out.println("COURSE = " + test.getCourse());
        System.out.println("SUBJECT = " + test.getSubject());
        System.out.println("CHAPTER = " + test.getChapter());
        System.out.println("DURATION = " + test.getDuration());
        System.out.println("TOTAL MARKS = " + test.getTotalMarks());
        System.out.println("PASSING MARKS = " + test.getPassingMarks());
        System.out.println("QUESTIONS = " + (test.getQuestions() != null ? test.getQuestions().size() : 0));
        System.out.println("STATUS = " + test.getStatus());

        testService.saveTest(test);

        return "redirect:/tests/list";
    }

    @GetMapping("/list")
    public String listTests(Model model) {
        List<Test> tests = testService.getAllTests();
        model.addAttribute("tests", tests);
        return "test-list";
    }

    @GetMapping("/results/{id}")
    public String testResults(@PathVariable Long id, Model model) {
        Test test = testService.getTestById(id);

        if (test == null) {
            return "redirect:/tests/list";
        }

        model.addAttribute("test", test);
        return "test-results";
    }

    @GetMapping("/analysis")
    public String testAnalysis(Model model) {
        List<Test> tests = testService.getAllTests();

        long totalTests = tests.size();

        long publishedTests = tests.stream()
                .filter(test -> "PUBLISHED".equalsIgnoreCase(test.getStatus()))
                .count();

        long draftTests = tests.stream()
                .filter(test -> !"PUBLISHED".equalsIgnoreCase(test.getStatus()))
                .count();

        long totalQuestions = tests.stream()
                .mapToLong(test -> test.getQuestions() != null ? test.getQuestions().size() : 0)
                .sum();

        model.addAttribute("tests", tests);
        model.addAttribute("totalTests", totalTests);
        model.addAttribute("publishedTests", publishedTests);
        model.addAttribute("draftTests", draftTests);
        model.addAttribute("totalQuestions", totalQuestions);

        return "analysis";
    }
}