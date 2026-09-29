package com.itvedant.StudentManagement.controller;

import com.itvedant.StudentManagement.dto.QuestionResultStats;
import com.itvedant.StudentManagement.model.Chapter;
import com.itvedant.StudentManagement.model.Module;
import com.itvedant.StudentManagement.model.Question;
import com.itvedant.StudentManagement.model.Test;
import com.itvedant.StudentManagement.services.ChapterService;
import com.itvedant.StudentManagement.services.ModuleService;
import com.itvedant.StudentManagement.services.TestResultService;
import com.itvedant.StudentManagement.services.TestService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/tests")
public class TestController {

    private static final List<String> SUBJECTS =
            List.of("Physics", "Chemistry", "Biology");

    private final TestService testService;
    private final ModuleService moduleService;
    private final ChapterService chapterService;
    private final TestResultService testResultService;

    public TestController(
            TestService testService,
            ModuleService moduleService,
            ChapterService chapterService,
            TestResultService testResultService) {

        this.testService = testService;
        this.moduleService = moduleService;
        this.chapterService = chapterService;
        this.testResultService = testResultService;
    }

    private Map<String, List<Chapter>> getChaptersBySubject() {

        Map<String, List<Chapter>> result =
                new LinkedHashMap<>();

        for (String subject : SUBJECTS) {

            List<Chapter> chapters =
                    new ArrayList<>();

            List<Module> modules =
                    moduleService.getModulesBySubject(subject);

            if (modules != null) {

                for (Module module : modules) {

                    if (module == null ||
                            module.getId() == null) {
                        continue;
                    }

                    List<Chapter> moduleChapters =
                            chapterService.getChaptersByModuleId(
                                    module.getId());

                    if (moduleChapters != null) {
                        chapters.addAll(moduleChapters);
                    }
                }
            }

            result.put(subject, chapters);
        }

        return result;
    }

    @GetMapping("/create")
    public String showCreateTest(Model model) {

        Test test = new Test();

        model.addAttribute(
                "test",
                test);

        model.addAttribute(
                "chaptersBySubject",
                getChaptersBySubject());

        return "create-test";
    }

    @PostMapping("/save")
    public String saveTest(
            @ModelAttribute Test test,

            @RequestParam(
                    value = "biologyChapter",
                    required = false)
            List<String> biologyChapters,

            @RequestParam(
                    value = "chemistryChapter",
                    required = false)
            List<String> chemistryChapters,

            @RequestParam(
                    value = "physicsChapter",
                    required = false)
            List<String> physicsChapters,

            @RequestParam(
                    value = "chapter",
                    required = false)
            List<String> oldChapters,

            @RequestParam(
                    value = "chapterSubjects",
                    required = false)
            List<String> oldChapterSubjects,

            RedirectAttributes redirectAttributes) {

        Map<String, List<String>> chaptersBySelectedSubject =
                new LinkedHashMap<>();

        chaptersBySelectedSubject.put(
                "Biology",
                new ArrayList<>());

        chaptersBySelectedSubject.put(
                "Chemistry",
                new ArrayList<>());

        chaptersBySelectedSubject.put(
                "Physics",
                new ArrayList<>());

        if (biologyChapters != null) {

            for (String chapter : biologyChapters) {

                if (chapter == null ||
                        chapter.trim().isEmpty()) {
                    continue;
                }

                String chapterName =
                        chapter.trim();

                List<String> biologyList =
                        chaptersBySelectedSubject.get("Biology");

                if (!biologyList.contains(chapterName)) {
                    biologyList.add(chapterName);
                }
            }
        }

        if (chemistryChapters != null) {

            for (String chapter : chemistryChapters) {

                if (chapter == null ||
                        chapter.trim().isEmpty()) {
                    continue;
                }

                String chapterName =
                        chapter.trim();

                List<String> chemistryList =
                        chaptersBySelectedSubject.get("Chemistry");

                if (!chemistryList.contains(chapterName)) {
                    chemistryList.add(chapterName);
                }
            }
        }

        if (physicsChapters != null) {

            for (String chapter : physicsChapters) {

                if (chapter == null ||
                        chapter.trim().isEmpty()) {
                    continue;
                }

                String chapterName =
                        chapter.trim();

                List<String> physicsList =
                        chaptersBySelectedSubject.get("Physics");

                if (!physicsList.contains(chapterName)) {
                    physicsList.add(chapterName);
                }
            }
        }

        if (oldChapters != null &&
                !oldChapters.isEmpty() &&
                biologyChapters == null &&
                chemistryChapters == null &&
                physicsChapters == null) {

            for (int i = 0;
                 i < oldChapters.size();
                 i++) {

                String chapter =
                        oldChapters.get(i);

                if (chapter == null ||
                        chapter.trim().isEmpty()) {
                    continue;
                }

                String chapterName =
                        chapter.trim();

                String subject = "";

                if (oldChapterSubjects != null &&
                        i < oldChapterSubjects.size() &&
                        oldChapterSubjects.get(i) != null) {

                    subject =
                            oldChapterSubjects
                                    .get(i)
                                    .trim();
                }

                if (chaptersBySelectedSubject.containsKey(subject)) {

                    List<String> subjectChapters =
                            chaptersBySelectedSubject.get(subject);

                    if (!subjectChapters.contains(chapterName)) {
                        subjectChapters.add(chapterName);
                    }
                }
            }
        }

        List<String> selectedSubjects =
                new ArrayList<>();

        for (Map.Entry<String, List<String>> entry :
                chaptersBySelectedSubject.entrySet()) {

            if (!entry.getValue().isEmpty()) {
                selectedSubjects.add(entry.getKey());
            }
        }

        test.setSubject(
                selectedSubjects.isEmpty()
                        ? ""
                        : String.join(
                                " | ",
                                selectedSubjects));

        StringBuilder chapterBuilder =
                new StringBuilder();

        for (Map.Entry<String, List<String>> entry :
                chaptersBySelectedSubject.entrySet()) {

            String subject =
                    entry.getKey();

            List<String> subjectChapters =
                    entry.getValue();

            if (subjectChapters.isEmpty()) {
                continue;
            }

            if (chapterBuilder.length() > 0) {
                chapterBuilder.append("\n\n");
            }

            chapterBuilder
                    .append(subject)
                    .append(":\n");

            for (String chapter :
                    subjectChapters) {

                chapterBuilder
                        .append("    ")
                        .append(chapter)
                        .append("\n");
            }
        }

        test.setChapter(
                chapterBuilder
                        .toString()
                        .trim());

        if (test.getQuestions() != null) {

            List<Question> validQuestions =
                    new ArrayList<>();

            for (Question question :
                    test.getQuestions()) {

                if (question == null) {
                    continue;
                }

                if (question.getQuestionText() == null ||
                        question.getQuestionText()
                                .trim()
                                .isEmpty()) {
                    continue;
                }

                question.setTest(test);

                if (question.getMarks() == null ||
                        question.getMarks() <= 0) {

                    question.setMarks(1);
                }

                validQuestions.add(question);
            }

            test.getQuestions().clear();
            test.getQuestions().addAll(validQuestions);
        }

        int questionCount =
                test.getQuestions() != null
                        ? test.getQuestions().size()
                        : 0;

        if (test.getStatus() == null ||
                test.getStatus().trim().isEmpty()) {

            test.setStatus("DRAFT");
        }

        if (test.getPassingMarks() == null &&
                test.getTotalMarks() != null) {

            test.setPassingMarks(
                    (int) Math.ceil(
                            test.getTotalMarks() * 0.40));
        }

        if (test.getShuffleQuestions() == null) {
            test.setShuffleQuestions(false);
        }

        if (test.getShuffleOptions() == null) {
            test.setShuffleOptions(false);
        }

        if (test.getShowResultImmediately() == null) {
            test.setShowResultImmediately(true);
        }

        if (test.getAllowTestRetake() == null) {
            test.setAllowTestRetake(false);
        }

        if (test.getNumberOfAttempts() == null ||
                test.getNumberOfAttempts() < 1) {

            test.setNumberOfAttempts(1);
        }

        testService.save(test);

        redirectAttributes.addFlashAttribute(
                "message",
                "Test created successfully with "
                        + questionCount
                        + " questions!");

        return "redirect:/tests/list";
    }

    @GetMapping("/list")
    public String listTests(Model model) {

        List<Test> tests =
                testService.getAll();

        long totalTests =
                tests.size();

        long publishedTests =
                tests.stream()
                        .filter(test ->
                                "PUBLISHED".equalsIgnoreCase(
                                        test.getStatus()))
                        .count();

        long draftTests =
                tests.stream()
                        .filter(test ->
                                !"PUBLISHED".equalsIgnoreCase(
                                        test.getStatus()))
                        .count();

        long totalQuestions =
                tests.stream()
                        .mapToLong(test ->
                                test.getQuestions() != null
                                        ? test.getQuestions().size()
                                        : 0)
                        .sum();

        model.addAttribute(
                "tests",
                tests);

        model.addAttribute(
                "totalTests",
                totalTests);

        model.addAttribute(
                "publishedTests",
                publishedTests);

        model.addAttribute(
                "draftTests",
                draftTests);

        model.addAttribute(
                "totalQuestions",
                totalQuestions);

        return "test-list";
    }

    @GetMapping("/results")
    public String allTestResults(Model model) {

        List<Test> tests =
                testService.getAll();

        model.addAttribute(
                "tests",
                tests);

        return "test-results-list";
    }

    @GetMapping("/results/{id}")
    public String testResults(
            @PathVariable Long id,

            @RequestParam(
                    value = "question",
                    defaultValue = "0")
            int questionIndex,

            Model model) {

        Test test =
                testService.getById(id);

        if (test == null) {
            return "redirect:/tests/results";
        }

        List<Question> questions =
                test.getQuestions();

        if (questions == null) {
            questions = new ArrayList<>();
        }

        model.addAttribute(
                "test",
                test);

        model.addAttribute(
                "questions",
                questions);

        int questionCount =
                questions.size();

        model.addAttribute(
                "questionCount",
                questionCount);

        Map<Long, QuestionResultStats> questionStats =
                new LinkedHashMap<>();

        if (!questions.isEmpty()) {

            questionStats =
                    testResultService
                            .getQuestionResultStats(
                                    id,
                                    questions);
        }

        model.addAttribute(
                "questionStats",
                questionStats);

        if (questions.isEmpty()) {

            model.addAttribute(
                    "selectedQuestion",
                    null);

            model.addAttribute(
                    "selectedQuestionNumber",
                    0);

            model.addAttribute(
                    "submittedAttemptCount",
                    0);

            return "test-results";
        }

        if (questionIndex < 0 ||
                questionIndex >= questions.size()) {

            questionIndex = 0;
        }

        Question selectedQuestion =
                questions.get(questionIndex);

        model.addAttribute(
                "selectedQuestion",
                selectedQuestion);

        model.addAttribute(
                "selectedQuestionNumber",
                questionIndex + 1);

        return "test-results";
    }

    @GetMapping("/edit/{id}")
    public String editTest(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        Test test =
                testService.getById(id);

        if (test == null) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Test not found.");

            return "redirect:/tests/list";
        }

        model.addAttribute(
                "test",
                test);

        model.addAttribute(
                "chaptersBySubject",
                getChaptersBySubject());

        return "test-edit";
    }

    @PostMapping("/update/{id}")
    public String updateTest(
            @PathVariable Long id,
            @ModelAttribute Test updatedTest,
            RedirectAttributes redirectAttributes) {

        Test existingTest =
                testService.getById(id);

        if (existingTest == null) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Test not found.");

            return "redirect:/tests/list";
        }

        existingTest.setTestName(
                updatedTest.getTestName());

        existingTest.setCourse(
                updatedTest.getCourse());

        existingTest.setSubject(
                updatedTest.getSubject());

        existingTest.setChapter(
                updatedTest.getChapter());

        existingTest.setDuration(
                updatedTest.getDuration());

        existingTest.setTotalMarks(
                updatedTest.getTotalMarks());

        existingTest.setPassingMarks(
                updatedTest.getPassingMarks());

        existingTest.setShuffleQuestions(
                updatedTest.getShuffleQuestions());

        existingTest.setShuffleOptions(
                updatedTest.getShuffleOptions());

        existingTest.setShowResultImmediately(
                updatedTest.getShowResultImmediately());

        existingTest.setAllowTestRetake(
                updatedTest.getAllowTestRetake());

        existingTest.setNumberOfAttempts(
                updatedTest.getNumberOfAttempts());

        existingTest.setStatus(
                updatedTest.getStatus());

        if (updatedTest.getQuestions() != null) {

            existingTest.getQuestions().clear();

            for (Question question :
                    updatedTest.getQuestions()) {

                if (question == null) {
                    continue;
                }

                if (question.getQuestionText() == null ||
                        question.getQuestionText()
                                .trim()
                                .isEmpty()) {

                    continue;
                }

                question.setTest(existingTest);

                if (question.getMarks() == null ||
                        question.getMarks() <= 0) {

                    question.setMarks(1);
                }

                existingTest
                        .getQuestions()
                        .add(question);
            }
        }

        testService.save(existingTest);

        int questionCount =
                existingTest.getQuestions() != null
                        ? existingTest.getQuestions().size()
                        : 0;

        redirectAttributes.addFlashAttribute(
                "message",
                "Test updated successfully with "
                        + questionCount
                        + " questions!");

        return "redirect:/tests/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteTest(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        Test test =
                testService.getById(id);

        if (test == null) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Test not found.");

            return "redirect:/tests/list";
        }

        testService.delete(id);

        redirectAttributes.addFlashAttribute(
                "message",
                "Test deleted successfully!");

        return "redirect:/tests/list";
    }

    @GetMapping("/analysis")
    public String testAnalysis(Model model) {

        List<Test> tests =
                testService.getAll();

        long totalTests =
                tests.size();

        long publishedTests =
                tests.stream()
                        .filter(test ->
                                "PUBLISHED".equalsIgnoreCase(
                                        test.getStatus()))
                        .count();

        long draftTests =
                tests.stream()
                        .filter(test ->
                                !"PUBLISHED".equalsIgnoreCase(
                                        test.getStatus()))
                        .count();

        long totalQuestions =
                tests.stream()
                        .mapToLong(test ->
                                test.getQuestions() != null
                                        ? test.getQuestions().size()
                                        : 0)
                        .sum();

        model.addAttribute(
                "tests",
                tests);

        model.addAttribute(
                "totalTests",
                totalTests);

        model.addAttribute(
                "publishedTests",
                publishedTests);

        model.addAttribute(
                "draftTests",
                draftTests);

        model.addAttribute(
                "totalQuestions",
                totalQuestions);

        return "analysis";
    }
}