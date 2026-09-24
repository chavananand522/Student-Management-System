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
	// HELPER
	// Get chapters grouped by subject
	// Subject -> Modules -> Chapters
	// =========================================================

	private Map<String, List<Chapter>> getChaptersBySubject() {

		Map<String, List<Chapter>> result = new LinkedHashMap<>();

		for (String subject : SUBJECTS) {

			List<Chapter> chapters = new ArrayList<>();

			List<Module> modules = moduleService.getModulesBySubject(subject);

			if (modules != null) {

				for (Module module : modules) {

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

		model.addAttribute("test", new Test());

		model.addAttribute("chaptersBySubject", getChaptersBySubject());

		return "create-test";
	}

	// =========================================================
	// SAVE TEST
	// =========================================================

	@PostMapping("/save")
	public String saveTest(

			@ModelAttribute Test test,

			/*
			 * Multiple selected chapters from:
			 *
			 * Biology Chemistry Physics
			 */
			@RequestParam(value = "chapter", required = false) List<String> chapters,

			/*
			 * Subject corresponding to each chapter.
			 */
			@RequestParam(value = "chapterSubjects", required = false) List<String> chapterSubjects,

			RedirectAttributes redirectAttributes) {

		// =====================================================
		// HANDLE MULTIPLE CHAPTERS
		// =====================================================

		if (chapters != null && !chapters.isEmpty()) {

			List<String> chapterPairs = new ArrayList<>();

			for (int i = 0; i < chapters.size(); i++) {

				String chapter = chapters.get(i);

				if (chapter == null || chapter.trim().isEmpty()) {

					continue;
				}

				chapter = chapter.trim();

				String subject = "";

				/*
				 * Get the subject at the same index.
				 */
				if (chapterSubjects != null && i < chapterSubjects.size() && chapterSubjects.get(i) != null) {

					subject = chapterSubjects.get(i).trim();
				}

				/*
				 * Store:
				 *
				 * Biology: Plant Kingdom
				 *
				 * instead of only:
				 *
				 * Plant Kingdom
				 */
				if (!subject.isEmpty()) {

					chapterPairs.add(subject + ": " + chapter);

				} else {

					chapterPairs.add(chapter);
				}
			}

			/*
			 * Store all chapters in one TEXT column.
			 */
			test.setChapter(String.join(" | ", chapterPairs));

		} else {

			test.setChapter("");
		}

		// =====================================================
		// HANDLE MULTIPLE SUBJECTS
		// =====================================================

		if (chapterSubjects != null && !chapterSubjects.isEmpty()) {

			List<String> subjects = chapterSubjects.stream()

					.filter(subject -> subject != null && !subject.trim().isEmpty())

					.map(String::trim)

					.distinct()

					.toList();

			/*
			 * Example:
			 *
			 * Biology | Chemistry | Physics
			 */
			test.setSubject(String.join(" | ", subjects));

		} else {

			test.setSubject("");
		}

		// =====================================================
		// CONNECT QUESTIONS TO TEST
		// =====================================================

		if (test.getQuestions() != null) {

			test.getQuestions().forEach(question -> {

				question.setTest(test);

			});
		}

		// =====================================================
		// SAVE TEST
		// =====================================================

		testService.saveTest(test);

		// =====================================================
		// SUCCESS MESSAGE
		// =====================================================

		redirectAttributes.addFlashAttribute("message", "Test created successfully!");

		return "redirect:/tests/results";
	}

	// =========================================================
	// TEST LIST
	// =========================================================

	@GetMapping("/list")
	public String listTests(Model model) {

		List<Test> tests = testService.getAllTests();

		model.addAttribute("tests", tests);

		return "test-list";
	}

	// =========================================================
	// ALL TEST RESULTS
	// =========================================================

	@GetMapping("/results")
	public String allTestResults(Model model) {

		List<Test> tests = testService.getAllTests();

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

		Test test = testService.getTestById(id);

		if (test == null) {

			return "redirect:/tests/results";
		}

		List<Question> questions = test.getQuestions();

		model.addAttribute("test", test);

		// =====================================================
		// NO QUESTIONS
		// =====================================================

		if (questions == null || questions.isEmpty()) {

			model.addAttribute("selectedQuestion", null);

			model.addAttribute("selectedQuestionNumber", 0);

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

		Test test = testService.getTestById(id);

		if (test == null) {

			redirectAttributes.addFlashAttribute("error", "Test not found.");

			return "redirect:/tests/results";
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

		Test existingTest = testService.getTestById(id);

		if (existingTest == null) {

			redirectAttributes.addFlashAttribute("error", "Test not found.");

			return "redirect:/tests/results";
		}

		// =====================================================
		// UPDATE BASIC INFORMATION
		// =====================================================

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

		// =====================================================
		// UPDATE QUESTIONS
		// =====================================================

		existingTest.getQuestions().clear();

		if (updatedTest.getQuestions() != null) {

			updatedTest.getQuestions().forEach(question -> {

				question.setTest(existingTest);

				existingTest.getQuestions().add(question);

			});
		}

		// =====================================================
		// SAVE
		// =====================================================

		testService.saveTest(existingTest);

		redirectAttributes.addFlashAttribute("message", "Test updated successfully!");

		return "redirect:/tests/results";
	}

	// =========================================================
	// DELETE TEST
	// =========================================================

	@GetMapping("/delete/{id}")
	public String deleteTest(

			@PathVariable Long id,

			RedirectAttributes redirectAttributes) {

		if (testService.getTestById(id) == null) {

			redirectAttributes.addFlashAttribute("error", "Test not found.");

			return "redirect:/tests/results";
		}

		testService.deleteTest(id);

		redirectAttributes.addFlashAttribute("message", "Test deleted successfully!");

		return "redirect:/tests/results";
	}

	// =========================================================
	// TEST ANALYSIS
	// =========================================================

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