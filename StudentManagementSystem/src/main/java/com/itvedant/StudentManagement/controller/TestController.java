package com.itvedant.StudentManagement.controller;

import com.itvedant.StudentManagement.model.Test;
import com.itvedant.StudentManagement.services.TestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

        if (test.getQuestions() != null) {

            test.getQuestions().forEach(question -> {
                question.setTest(test);
            });

        }

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

    @GetMapping("/edit/{id}")
    public String editTest(@PathVariable Long id, Model model) {

        Test test = testService.getTestById(id);

        if (test == null) {
            return "redirect:/tests/list";
        }

        model.addAttribute("test", test);

        return "test-edit";
    }

    @PostMapping("/update/{id}")
    public String updateTest(@PathVariable Long id,
                             @ModelAttribute Test updatedTest) {

        Test existingTest = testService.getTestById(id);

        if (existingTest == null) {
            return "redirect:/tests/list";
        }

        existingTest.setTestName(updatedTest.getTestName());
        existingTest.setCourse(updatedTest.getCourse());
        existingTest.setSubject(updatedTest.getSubject());
        existingTest.setChapter(updatedTest.getChapter());
        existingTest.setDuration(updatedTest.getDuration());
        existingTest.setTotalMarks(updatedTest.getTotalMarks());
        existingTest.setPassingMarks(updatedTest.getPassingMarks());
        existingTest.setShuffleQuestions(updatedTest.getShuffleQuestions());
        existingTest.setShuffleOptions(updatedTest.getShuffleOptions());
        existingTest.setShowResultImmediately(updatedTest.getShowResultImmediately());
        existingTest.setAllowTestRetake(updatedTest.getAllowTestRetake());
        existingTest.setNumberOfAttempts(updatedTest.getNumberOfAttempts());
        existingTest.setStatus(updatedTest.getStatus());

        existingTest.getQuestions().clear();

        if (updatedTest.getQuestions() != null) {

            updatedTest.getQuestions().forEach(question -> {

                question.setTest(existingTest);

                existingTest.getQuestions().add(question);

            });

        }

        testService.saveTest(existingTest);

        return "redirect:/tests/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteTest(@PathVariable Long id) {

        testService.deleteTest(id);

        return "redirect:/tests/list";
    }

    @GetMapping("/analysis")
    public String testAnalysis(Model model) {

        List<Test> tests = testService.getAllTests();

        long totalTests = tests.size();

        long publishedTests = tests.stream()
                .filter(test ->
                        "PUBLISHED".equalsIgnoreCase(test.getStatus()))
                .count();

        long draftTests = tests.stream()
                .filter(test ->
                        !"PUBLISHED".equalsIgnoreCase(test.getStatus()))
                .count();

        long totalQuestions = tests.stream()
                .mapToLong(test ->
                        test.getQuestions() != null
                                ? test.getQuestions().size()
                                : 0)
                .sum();

        model.addAttribute("tests", tests);
        model.addAttribute("totalTests", totalTests);
        model.addAttribute("publishedTests", publishedTests);
        model.addAttribute("draftTests", draftTests);
        model.addAttribute("totalQuestions", totalQuestions);

        return "analysis";
    }
}