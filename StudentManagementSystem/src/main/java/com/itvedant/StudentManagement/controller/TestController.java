package com.itvedant.StudentManagement.controller;

import com.itvedant.StudentManagement.model.Chapter;
import com.itvedant.StudentManagement.model.Module;
import com.itvedant.StudentManagement.model.Question;
import com.itvedant.StudentManagement.model.Test;
import com.itvedant.StudentManagement.services.ChapterService;
import com.itvedant.StudentManagement.services.ModuleService;
import com.itvedant.StudentManagement.services.TestService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/tests")
public class TestController {

	private static final List<String> SUBJECTS = List.of("Physics", "Chemistry", "Biology");

	private final TestService testService;
	private final ModuleService moduleService;
	private final ChapterService chapterService;

	public TestController(TestService testService, ModuleService moduleService, ChapterService chapterService) {

		this.testService = testService;
		this.moduleService = moduleService;
		this.chapterService = chapterService;
	}

	// =========================================================
	// GET CHAPTERS GROUPED BY SUBJECT
	// =========================================================

	private Map<String, List<Chapter>> getChaptersBySubject() {

		Map<String, List<Chapter>> result = new LinkedHashMap<>();

		for (String subject : SUBJECTS) {

			List<Chapter> chapters = new ArrayList<>();

			List<Module> modules = moduleService.getModulesBySubject(subject);

			if (modules != null) {

				for (Module module : modules) {

					if (module == null || module.getId() == null) {
						continue;
					}

					List<Chapter> moduleChapters = chapterService.getChaptersByModuleId(module.getId());

					if (moduleChapters != null) {
						chapters.addAll(moduleChapters);
					}
				}
			}

			result.put(subject, chapters);
		}

		return result;
	}

	// =========================================================
	// CREATE TEST
	// =========================================================

	@GetMapping("/create")
	public String showCreateTest(Model model) {

		Test test = new Test();

		model.addAttribute("test", test);

		model.addAttribute("chaptersBySubject", getChaptersBySubject());

		return "create-test";
	}

	// =========================================================
	// SAVE TEST
	// =========================================================

	@PostMapping("/save")
	public String saveTest(

			@ModelAttribute Test test,

			@RequestParam(value = "biologyChapter", required = false) List<String> biologyChapters,

			@RequestParam(value = "chemistryChapter", required = false) List<String> chemistryChapters,

			@RequestParam(value = "physicsChapter", required = false) List<String> physicsChapters,

			@RequestParam(value = "chapter", required = false) List<String> oldChapters,

			@RequestParam(value = "chapterSubjects", required = false) List<String> oldChapterSubjects,

			RedirectAttributes redirectAttributes) {

		// =====================================================
		// COLLECT SELECTED CHAPTERS
		// =====================================================

		List<String> chapterPairs = new ArrayList<>();

		List<String> subjects = new ArrayList<>();

		// =====================================================
		// BIOLOGY
		// =====================================================

		if (biologyChapters != null) {

			for (String chapter : biologyChapters) {

				if (chapter == null || chapter.trim().isEmpty()) {
					continue;
				}

				chapterPairs.add("Biology: " + chapter.trim());

				if (!subjects.contains("Biology")) {
					subjects.add("Biology");
				}
			}
		}

		// =====================================================
		// CHEMISTRY
		// =====================================================

		if (chemistryChapters != null) {

			for (String chapter : chemistryChapters) {

				if (chapter == null || chapter.trim().isEmpty()) {
					continue;
				}

				chapterPairs.add("Chemistry: " + chapter.trim());

				if (!subjects.contains("Chemistry")) {
					subjects.add("Chemistry");
				}
			}
		}

		// =====================================================
		// PHYSICS
		// =====================================================

		if (physicsChapters != null) {

			for (String chapter : physicsChapters) {

				if (chapter == null || chapter.trim().isEmpty()) {
					continue;
				}

				chapterPairs.add("Physics: " + chapter.trim());

				if (!subjects.contains("Physics")) {
					subjects.add("Physics");
				}
			}
		}

		// =====================================================
		// OLD CHAPTER FORMAT SUPPORT
		// =====================================================

		if (oldChapters != null && !oldChapters.isEmpty() && chapterPairs.isEmpty()) {

			for (int i = 0; i < oldChapters.size(); i++) {

				String chapter = oldChapters.get(i);

				if (chapter == null || chapter.trim().isEmpty()) {
					continue;
				}

				String subject = "";

				if (oldChapterSubjects != null && i < oldChapterSubjects.size() && oldChapterSubjects.get(i) != null) {

					subject = oldChapterSubjects.get(i).trim();
				}

				if (!subject.isEmpty()) {

					chapterPairs.add(subject + ": " + chapter.trim());

					if (!subjects.contains(subject)) {
						subjects.add(subject);
					}

				} else {

					chapterPairs.add(chapter.trim());
				}
			}
		}

		// =====================================================
		// SAVE SUBJECT
		// =====================================================

		if (!subjects.isEmpty()) {

			test.setSubject(String.join(" | ", subjects));

		} else {

			test.setSubject("");
		}

		// =====================================================
		// SAVE CHAPTER
		// =====================================================

		if (!chapterPairs.isEmpty()) {

			test.setChapter(String.join(" | ", chapterPairs));

		} else {

			test.setChapter("");
		}

		// =====================================================
		// CONNECT QUESTIONS TO TEST
		// =====================================================

		if (test.getQuestions() != null) {

			List<Question> validQuestions = new ArrayList<>();

			for (Question question : test.getQuestions()) {

				if (question == null) {
					continue;
				}

				if (question.getQuestionText() == null || question.getQuestionText().trim().isEmpty()) {
					continue;
				}

				question.setTest(test);

				validQuestions.add(question);
			}

			test.getQuestions().clear();

			test.getQuestions().addAll(validQuestions);
		}

		// =====================================================
		// QUESTION COUNT
		// =====================================================

		int questionCount = test.getQuestions() != null ? test.getQuestions().size() : 0;

		// =====================================================
		// DEFAULT VALUES
		// =====================================================

		if (test.getStatus() == null || test.getStatus().trim().isEmpty()) {

			test.setStatus("DRAFT");
		}

		if (test.getPassingMarks() == null && test.getTotalMarks() != null) {

			test.setPassingMarks((int) Math.ceil(test.getTotalMarks() * 0.40));
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

		if (test.getNumberOfAttempts() == null || test.getNumberOfAttempts() < 1) {

			test.setNumberOfAttempts(1);
		}

		// =====================================================
		// SAVE TEST
		// =====================================================

		testService.save(test);

		// =====================================================
		// SUCCESS MESSAGE
		// =====================================================

		redirectAttributes.addFlashAttribute("message",
				"Test created successfully with " + questionCount + " questions!");

		return "redirect:/tests/list";
	}

	// =========================================================
	// TEST LIST
	// =========================================================

	@GetMapping("/list")
	public String listTests(Model model) {

		List<Test> tests = testService.getAll();

		// Force question collections to load
		for (Test test : tests) {

			if (test.getQuestions() != null) {
				test.getQuestions().size();
			}
		}

		// =====================================================
		// TOTAL TESTS
		// =====================================================

		long totalTests = tests.size();

		// =====================================================
		// PUBLISHED TESTS
		// =====================================================

		long publishedTests = tests.stream().filter(test -> "PUBLISHED".equalsIgnoreCase(test.getStatus())).count();

		// =====================================================
		// DRAFT TESTS
		// =====================================================

		long draftTests = tests.stream().filter(test -> !"PUBLISHED".equalsIgnoreCase(test.getStatus())).count();

		// =====================================================
		// TOTAL CREATED QUESTIONS
		// =====================================================

		long totalQuestions = tests.stream()
				.mapToLong(test -> test.getQuestions() != null ? test.getQuestions().size() : 0).sum();

		model.addAttribute("tests", tests);

		model.addAttribute("totalTests", totalTests);

		model.addAttribute("publishedTests", publishedTests);

		model.addAttribute("draftTests", draftTests);

		model.addAttribute("totalQuestions", totalQuestions);

		return "test-list";
	}

	// =========================================================
	// ALL TEST RESULTS
	// =========================================================

	@GetMapping("/results")
	public String allTestResults(Model model) {

		List<Test> tests = testService.getAll();

		model.addAttribute("tests", tests);

		return "test-results-list";
	}

	// =========================================================
	// INDIVIDUAL TEST RESULTS
	// =========================================================

	@GetMapping("/results/{id}")
	public String testResults(

			@PathVariable Long id,

			@RequestParam(value = "question", defaultValue = "0") int questionIndex,

			Model model) {

		Test test = testService.getById(id);

		if (test == null) {
			return "redirect:/tests/results";
		}

		List<Question> questions = test.getQuestions();

		model.addAttribute("test", test);

		// =====================================================
		// CREATED QUESTION COUNT
		// =====================================================

		int questionCount = questions != null ? questions.size() : 0;

		model.addAttribute("questionCount", questionCount);

		// =====================================================
		// NO QUESTIONS
		// =====================================================

		if (questions == null || questions.isEmpty()) {

			model.addAttribute("selectedQuestion", null);

			model.addAttribute("selectedQuestionNumber", 0);

			model.addAttribute("correctCount", 0);

			model.addAttribute("wrongCount", 0);

			model.addAttribute("neverOpenedCount", 0);

			return "test-results";
		}

		// =====================================================
		// VALIDATE QUESTION INDEX
		// =====================================================

		if (questionIndex < 0 || questionIndex >= questions.size()) {

			questionIndex = 0;
		}

		Question selectedQuestion = questions.get(questionIndex);

		model.addAttribute("selectedQuestion", selectedQuestion);

		model.addAttribute("selectedQuestionNumber", questionIndex + 1);

		// =====================================================
		// DEFAULT RESULT COUNTS
		// =====================================================

		model.addAttribute("correctCount", 0);

		model.addAttribute("wrongCount", 0);

		model.addAttribute("neverOpenedCount", 0);

		return "test-results";
	}

	// =========================================================
	// EDIT TEST
	// =========================================================

	@GetMapping("/edit/{id}")
	public String editTest(

			@PathVariable Long id,

			Model model,

			RedirectAttributes redirectAttributes) {

		Test test = testService.getById(id);

		if (test == null) {

			redirectAttributes.addFlashAttribute("error", "Test not found.");

			return "redirect:/tests/list";
		}

		model.addAttribute("test", test);

		model.addAttribute("chaptersBySubject", getChaptersBySubject());

		return "test-edit";
	}

	// =========================================================
	// UPDATE TEST
	// =========================================================

	@PostMapping("/update/{id}")
	public String updateTest(

			@PathVariable Long id,

			@ModelAttribute Test updatedTest,

			RedirectAttributes redirectAttributes) {

		Test existingTest = testService.getById(id);

		if (existingTest == null) {

			redirectAttributes.addFlashAttribute("error", "Test not found.");

			return "redirect:/tests/list";
		}

		// =====================================================
		// BASIC INFORMATION
		// =====================================================

		existingTest.setTestName(updatedTest.getTestName());

		existingTest.setCourse(updatedTest.getCourse());

		existingTest.setSubject(updatedTest.getSubject());

		existingTest.setChapter(updatedTest.getChapter());

		existingTest.setDuration(updatedTest.getDuration());

		existingTest.setTotalMarks(updatedTest.getTotalMarks());

		existingTest.setPassingMarks(updatedTest.getPassingMarks());

		// =====================================================
		// SETTINGS
		// =====================================================

		existingTest.setShuffleQuestions(updatedTest.getShuffleQuestions());

		existingTest.setShuffleOptions(updatedTest.getShuffleOptions());

		existingTest.setShowResultImmediately(updatedTest.getShowResultImmediately());

		existingTest.setAllowTestRetake(updatedTest.getAllowTestRetake());

		existingTest.setNumberOfAttempts(updatedTest.getNumberOfAttempts());

		existingTest.setStatus(updatedTest.getStatus());

		// =====================================================
		// UPDATE QUESTIONS
		// =====================================================

		if (updatedTest.getQuestions() != null) {

			existingTest.getQuestions().clear();

			for (Question question : updatedTest.getQuestions()) {

				if (question == null) {
					continue;
				}

				if (question.getQuestionText() == null || question.getQuestionText().trim().isEmpty()) {
					continue;
				}

				question.setTest(existingTest);

				existingTest.getQuestions().add(question);
			}
		}

		// =====================================================
		// SAVE
		// =====================================================

		testService.save(existingTest);

		// =====================================================
		// QUESTION COUNT AFTER UPDATE
		// =====================================================

		int questionCount = existingTest.getQuestions() != null ? existingTest.getQuestions().size() : 0;

		redirectAttributes.addFlashAttribute("message",
				"Test updated successfully with " + questionCount + " questions!");

		return "redirect:/tests/list";
	}

	// =========================================================
	// DELETE TEST
	// =========================================================

	@GetMapping("/delete/{id}")
	public String deleteTest(

			@PathVariable Long id,

			RedirectAttributes redirectAttributes) {

		Test test = testService.getById(id);

		if (test == null) {

			redirectAttributes.addFlashAttribute("error", "Test not found.");

			return "redirect:/tests/list";
		}

		testService.delete(id);

		redirectAttributes.addFlashAttribute("message", "Test deleted successfully!");

		return "redirect:/tests/list";
	}

	// =========================================================
	// TEST ANALYSIS
	// =========================================================

	@GetMapping("/analysis")
	public String testAnalysis(Model model) {

		List<Test> tests = testService.getAll();

		// =====================================================
		// TOTAL TESTS
		// =====================================================

		long totalTests = tests.size();

		// =====================================================
		// PUBLISHED
		// =====================================================

		long publishedTests = tests.stream().filter(test -> "PUBLISHED".equalsIgnoreCase(test.getStatus())).count();

		// =====================================================
		// DRAFT
		// =====================================================

		long draftTests = tests.stream().filter(test -> !"PUBLISHED".equalsIgnoreCase(test.getStatus())).count();

		// =====================================================
		// TOTAL CREATED QUESTIONS
		// =====================================================

		long totalQuestions = tests.stream()
				.mapToLong(test -> test.getQuestions() != null ? test.getQuestions().size() : 0).sum();

		model.addAttribute("tests", tests);

		model.addAttribute("totalTests", totalTests);

		model.addAttribute("publishedTests", publishedTests);

		model.addAttribute("draftTests", draftTests);

		model.addAttribute("totalQuestions", totalQuestions);

		return "analysis";
	}
}