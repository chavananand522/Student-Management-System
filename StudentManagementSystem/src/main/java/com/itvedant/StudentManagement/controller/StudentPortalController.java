// ============================================================
// FILE:
// src/main/java/com/itvedant/StudentManagement/controller/StudentPortalController.java
// ============================================================

package com.itvedant.StudentManagement.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itvedant.StudentManagement.model.Chapter;
import com.itvedant.StudentManagement.model.Courses;
import com.itvedant.StudentManagement.model.Enrollment;
import com.itvedant.StudentManagement.model.FeePayment;
import com.itvedant.StudentManagement.model.Module;
import com.itvedant.StudentManagement.model.Question;
import com.itvedant.StudentManagement.model.StudentAnswer;
import com.itvedant.StudentManagement.model.Students;
import com.itvedant.StudentManagement.model.Study;
import com.itvedant.StudentManagement.model.Test;
import com.itvedant.StudentManagement.model.TestAttempt;
import com.itvedant.StudentManagement.model.Users;
import com.itvedant.StudentManagement.reposatory.ChapterRepository;
import com.itvedant.StudentManagement.reposatory.FeePaymentRepository;
import com.itvedant.StudentManagement.reposatory.ModuleRepository;
import com.itvedant.StudentManagement.reposatory.StudentRepositiry;
import com.itvedant.StudentManagement.reposatory.StudyRepository;
import com.itvedant.StudentManagement.reposatory.TestAttemptRepository;
import com.itvedant.StudentManagement.reposatory.TestRepository;
import com.itvedant.StudentManagement.reposatory.UserRepository;

@Controller
@RequestMapping("/student")
public class StudentPortalController {

    private final StudentRepositiry studentRepository;
    private final UserRepository userRepository;
    private final TestRepository testRepository;
    private final TestAttemptRepository attemptRepository;
    private final StudyRepository studyRepository;
    private final ModuleRepository moduleRepository;
    private final ChapterRepository chapterRepository;
    private final FeePaymentRepository feePaymentRepository;

    public StudentPortalController(
            StudentRepositiry studentRepository,
            UserRepository userRepository,
            TestRepository testRepository,
            TestAttemptRepository attemptRepository,
            StudyRepository studyRepository,
            ModuleRepository moduleRepository,
            ChapterRepository chapterRepository,
            FeePaymentRepository feePaymentRepository) {

        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.testRepository = testRepository;
        this.attemptRepository = attemptRepository;
        this.studyRepository = studyRepository;
        this.moduleRepository = moduleRepository;
        this.chapterRepository = chapterRepository;
        this.feePaymentRepository = feePaymentRepository;
    }

    // =========================================================
    // DASHBOARD
    // =========================================================

    @GetMapping({
        "",
        "/dashboard"
    })
    @Transactional(readOnly = true)
    public String dashboard(
            Authentication authentication,
            Model model) {

        Students student =
                currentStudent(authentication);

        List<Enrollment> enrollments =
                student.getEnrollments();

        enrollments.size();

        long completedTests =
                attemptRepository
                    .findByStudentUsername(
                        authentication.getName())
                    .stream()
                    .filter(a ->
                        "SUBMITTED"
                            .equalsIgnoreCase(
                                a.getStatus()))
                    .count();

        model.addAttribute(
                "student",
                student);

        model.addAttribute(
                "courseCount",
                enrollments.size());

        model.addAttribute(
                "testCount",
                availableTests(student).size());

        model.addAttribute(
                "completedTests",
                completedTests);

        return "student/dashboard";
    }

    // =========================================================
    // COURSES
    // =========================================================

    @GetMapping("/courses")
    @Transactional(readOnly = true)
    public String courses(
            Authentication authentication,
            Model model) {

        Students student =
                currentStudent(authentication);

        List<Enrollment> enrollments =
                student.getEnrollments();

        enrollments.size();

        model.addAttribute(
                "student",
                student);

        model.addAttribute(
                "enrollments",
                enrollments);

        return "student/courses";
    }

    // =========================================================
    // STUDY
    // =========================================================

    @GetMapping("/study")
    public String study(Model model) {

        model.addAttribute(
                "subjects",
                studyRepository.findAll());

        model.addAttribute(
                "study",
                null);

        return "student/study";
    }

    @GetMapping("/study/{subject}")
    public String subject(
            @PathVariable String subject,
            Model model) {

        Study study =
                studyRepository
                    .findBySubjectIgnoreCase(
                        subject)
                    .orElse(null);

        if (study == null) {
            return "redirect:/student/study";
        }

        model.addAttribute(
                "study",
                study);

        model.addAttribute(
                "subjectName",
                study.getSubject());

        model.addAttribute(
                "modules",
                moduleRepository
                    .findBySubjectIgnoreCaseOrderByModuleNumberAsc(
                        study.getSubject()));

        return "student/study";
    }

    @GetMapping(
        "/study/{subject}/module/{moduleId}"
    )
    public String module(
            @PathVariable String subject,
            @PathVariable Long moduleId,
            Model model) {

        Study study =
                studyRepository
                    .findBySubjectIgnoreCase(
                        subject)
                    .orElse(null);

        Module module =
                moduleRepository
                    .findById(moduleId)
                    .orElse(null);

        if (study == null ||
            module == null ||
            !module.getSubject()
                .equalsIgnoreCase(
                    study.getSubject())) {

            return "redirect:/student/study";
        }

        model.addAttribute(
                "study",
                study);

        model.addAttribute(
                "subjectName",
                study.getSubject());

        model.addAttribute(
                "module",
                module);

        model.addAttribute(
                "chapters",
                chapterRepository
                    .findByModuleIdOrderByIdAsc(
                        moduleId));

        return "student/module-view";
    }

    @GetMapping(
        "/study/{subject}/module/{moduleId}/chapter/{chapterId}"
    )
    public String chapter(
            @PathVariable String subject,
            @PathVariable Long moduleId,
            @PathVariable Long chapterId,
            Model model) {

        Study study =
                studyRepository
                    .findBySubjectIgnoreCase(
                        subject)
                    .orElse(null);

        Module module =
                moduleRepository
                    .findById(moduleId)
                    .orElse(null);

        Chapter chapter =
                chapterRepository
                    .findById(chapterId)
                    .orElse(null);

        if (study == null ||
            module == null ||
            chapter == null ||
            !module.getSubject()
                .equalsIgnoreCase(
                    study.getSubject()) ||
            !moduleId.equals(
                chapter.getModuleId())) {

            return "redirect:/student/study";
        }

        model.addAttribute(
                "study",
                study);

        model.addAttribute(
                "subjectName",
                study.getSubject());

        model.addAttribute(
                "module",
                module);

        model.addAttribute(
                "chapter",
                chapter);

        return "student/chapter-view";
    }

    // =========================================================
    // TESTS
    // =========================================================

    @GetMapping("/tests")
    @Transactional(readOnly = true)
    public String tests(
            Authentication authentication,
            Model model) {

        Students student =
                currentStudent(authentication);

        /*
         * Get ALL PUBLISHED tests created by Admin.
         */
        List<Test> tests =
                availableTests(student);

        /*
         * Store number of attempts made by
         * the currently logged-in student.
         */
        Map<Long, Integer> attempts =
                new LinkedHashMap<>();

        for (Test test : tests) {

            attempts.put(
                test.getId(),
                attemptRepository
                    .findByTestIdAndStudentUsername(
                        test.getId(),
                        authentication.getName())
                    .size()
            );
        }

        model.addAttribute(
                "tests",
                tests);

        model.addAttribute(
                "attempts",
                attempts);

        return "student/tests";
    }

    // =========================================================
    // TEST DETAILS
    // =========================================================

    @GetMapping("/tests/{id}")
    @Transactional(readOnly = true)
    public String testDetails(
            @PathVariable Long id,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        Students student =
                currentStudent(authentication);

        Test test =
                publishedTest(id);

        if (!canAccessTest(test, student)) {

            redirectAttributes
                    .addFlashAttribute(
                        "error",
                        "Test is not available.");

            return "redirect:/student/tests";
        }

        List<TestAttempt> attempts =
                attemptRepository
                    .findByTestIdAndStudentUsername(
                        id,
                        authentication.getName());

        model.addAttribute(
                "test",
                test);

        model.addAttribute(
                "attempts",
                attempts);

        model.addAttribute(
                "canStart",
                canStart(test, attempts));

        return "student/test-details";
    }

    // =========================================================
    // START TEST
    // =========================================================

    @PostMapping("/tests/{id}/start")
    @Transactional
    public String startTest(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Students student =
                currentStudent(authentication);

        Test test =
                publishedTest(id);

        if (!canAccessTest(test, student)) {

            redirectAttributes
                    .addFlashAttribute(
                        "error",
                        "Test is not available.");

            return "redirect:/student/tests";
        }

        List<TestAttempt> attempts =
                attemptRepository
                    .findByTestIdAndStudentUsername(
                        id,
                        authentication.getName());

        /*
         * If the student already has an unfinished attempt,
         * continue that attempt.
         */
        TestAttempt inProgress =
                attempts.stream()
                    .filter(a ->
                        "IN_PROGRESS"
                            .equalsIgnoreCase(
                                a.getStatus()))
                    .findFirst()
                    .orElse(null);

        if (inProgress != null) {

            return "redirect:/student/tests/attempt/"
                    + inProgress.getId();
        }

        /*
         * Check allowed attempts.
         */
        if (!canStart(test, attempts)) {

            redirectAttributes
                    .addFlashAttribute(
                        "error",
                        "You have used all allowed attempts for this test.");

            return "redirect:/student/tests/"
                    + id;
        }

        TestAttempt attempt =
                new TestAttempt();

        attempt.setTest(test);

        attempt.setStudentUsername(
                authentication.getName());

        attempt.setAttemptNumber(
                attempts.size() + 1);

        attempt.setStatus(
                "IN_PROGRESS");

        attempt.setStartedAt(
                LocalDateTime.now());

        attemptRepository.save(attempt);

        return "redirect:/student/tests/attempt/"
                + attempt.getId();
    }

    // =========================================================
    // TAKE TEST
    // =========================================================

    @GetMapping("/tests/attempt/{attemptId}")
    @Transactional(readOnly = true)
    public String attempt(
            @PathVariable Long attemptId,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        TestAttempt attempt =
                attemptRepository
                    .findById(attemptId)
                    .orElse(null);

        if (attempt == null ||
            !authentication.getName()
                .equals(
                    attempt.getStudentUsername())) {

            redirectAttributes
                    .addFlashAttribute(
                        "error",
                        "Test attempt not found.");

            return "redirect:/student/tests";
        }

        if (!"IN_PROGRESS".equalsIgnoreCase(
                attempt.getStatus())) {

            return "redirect:/student/results/"
                    + attemptId;
        }

        Test test =
                attempt.getTest();

        test.getQuestions().size();

        List<Question> questions =
                new ArrayList<>(
                    test.getQuestions());

        if (Boolean.TRUE.equals(
                test.getShuffleQuestions())) {

            Collections.shuffle(questions);
        }

        model.addAttribute(
                "attempt",
                attempt);

        model.addAttribute(
                "test",
                test);

        model.addAttribute(
                "questions",
                questions);

        model.addAttribute(
                "durationSeconds",
                (test.getDuration() == null
                    ? 60
                    : test.getDuration()) * 60);

        return "student/test-attempt";
    }

    // =========================================================
    // SUBMIT TEST
    // =========================================================

    @PostMapping("/tests/attempt/{attemptId}/submit")
    @Transactional
    public String submitAttempt(
            @PathVariable Long attemptId,
            Authentication authentication,
            @RequestParam Map<String, String> form,
            RedirectAttributes redirectAttributes) {

        TestAttempt attempt =
                attemptRepository
                    .findById(attemptId)
                    .orElse(null);

        if (attempt == null ||
            !authentication.getName()
                .equals(
                    attempt.getStudentUsername())) {

            redirectAttributes
                    .addFlashAttribute(
                        "error",
                        "Test attempt not found.");

            return "redirect:/student/tests";
        }

        if (!"IN_PROGRESS".equalsIgnoreCase(
                attempt.getStatus())) {

            return "redirect:/student/results/"
                    + attemptId;
        }

        Test test =
                attempt.getTest();

        test.getQuestions().size();

        // =====================================================
        // CHECK TIME LIMIT
        // =====================================================

        if (attempt.getStartedAt() != null &&
            test.getDuration() != null) {

            long allowedSeconds =
                    test.getDuration()
                        .longValue() * 60L;

            long elapsedSeconds =
                    Duration.between(
                        attempt.getStartedAt(),
                        LocalDateTime.now())
                    .getSeconds();

            if (elapsedSeconds >
                    allowedSeconds + 30L) {

                attempt.setStatus(
                        "SUBMITTED");

                attempt.setSubmittedAt(
                        LocalDateTime.now());

                attempt.setUnansweredQuestions(
                        test.getQuestions().size());

                attempt.setCorrectAnswers(0);

                attempt.setWrongAnswers(0);

                attempt.setObtainedMarks(0);

                attemptRepository.save(
                        attempt);

                redirectAttributes
                    .addFlashAttribute(
                        "error",
                        "The test time expired. Your attempt was submitted.");

                return "redirect:/student/results/"
                        + attemptId;
            }
        }

        int correct = 0;
        int wrong = 0;
        int unanswered = 0;
        int marks = 0;

        // =====================================================
        // CHECK EACH QUESTION
        // =====================================================

        for (Question question :
                test.getQuestions()) {

            String selected =
                    form.get(
                        "answer_" +
                        question.getId());

            // Unanswered
            if (selected == null ||
                selected.trim().isEmpty()) {

                unanswered++;

                continue;
            }

            boolean isCorrect =
                    question.getCorrectAnswer() != null
                    &&
                    question.getCorrectAnswer()
                        .trim()
                        .equalsIgnoreCase(
                            selected.trim());

            int questionMarks =
                    question.getMarks() == null
                    ? 1
                    : question.getMarks();

            StudentAnswer answer =
                    new StudentAnswer();

            answer.setQuestion(
                    question);

            answer.setSelectedAnswer(
                    selected);

            answer.setCorrect(
                    isCorrect);

            answer.setMarksObtained(
                    isCorrect
                    ? questionMarks
                    : 0);

            attempt.addAnswer(
                    answer);

            if (isCorrect) {

                correct++;

                marks += questionMarks;

            } else {

                wrong++;
            }
        }

        // =====================================================
        // SAVE RESULT
        // =====================================================

        attempt.setCorrectAnswers(
                correct);

        attempt.setWrongAnswers(
                wrong);

        attempt.setUnansweredQuestions(
                unanswered);

        attempt.setObtainedMarks(
                marks);

        attempt.setStatus(
                "SUBMITTED");

        attempt.setSubmittedAt(
                LocalDateTime.now());

        attemptRepository.save(
                attempt);

        return "redirect:/student/results/"
                + attemptId;
    }

    // =========================================================
    // RESULTS
    // =========================================================

    @GetMapping("/results")
    @Transactional(readOnly = true)
    public String results(
            Authentication authentication,
            Model model) {

        List<TestAttempt> attempts =
                attemptRepository
                    .findByStudentUsername(
                        authentication.getName())
                    .stream()
                    .sorted(
                        Comparator.comparing(
                            TestAttempt::getSubmittedAt,
                            Comparator.nullsLast(
                                Comparator.reverseOrder())))
                    .collect(Collectors.toList());

        model.addAttribute(
                "attempts",
                attempts);

        return "student/results";
    }

    // =========================================================
    // SINGLE RESULT
    // =========================================================

    @GetMapping("/results/{attemptId}")
    @Transactional(readOnly = true)
    public String result(
            @PathVariable Long attemptId,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        TestAttempt attempt =
                attemptRepository
                    .findById(attemptId)
                    .orElse(null);

        if (attempt == null ||
            !authentication.getName()
                .equals(
                    attempt.getStudentUsername())) {

            redirectAttributes
                    .addFlashAttribute(
                        "error",
                        "Result not found.");

            return "redirect:/student/results";
        }

        attempt.getAnswers().size();

        model.addAttribute(
                "attempt",
                attempt);

        model.addAttribute(
                "test",
                attempt.getTest());

        return "student/result";
    }

    // =========================================================
    // FEES
    // =========================================================

    @GetMapping("/fees")
    @Transactional(readOnly = true)
    public String fees(
            Authentication authentication,
            Model model) {

        Students student =
                currentStudent(authentication);

        List<Enrollment> enrollments =
                student.getEnrollments();

        enrollments.size();

        List<StudentFeeRow> rows =
                new ArrayList<>();

        double totalFees = 0;

        double totalPaid = 0;

        for (Enrollment enrollment :
                enrollments) {

            Courses course =
                    enrollment.getCourse();

            if (course == null) {
                continue;
            }

            double fee =
                    course.getFee() == null
                    ? 0
                    : course.getFee()
                        .doubleValue();

            double paid =
                    feePaymentRepository
                        .getPaidByStudentAndCourse(
                            student.getId(),
                            course.getId());

            double pending =
                    Math.max(
                        0,
                        fee - paid);

            rows.add(
                new StudentFeeRow(
                    course.getCourseName(),
                    course.getCourseCode(),
                    fee,
                    paid,
                    pending));

            totalFees += fee;

            totalPaid += paid;
        }

        model.addAttribute(
                "rows",
                rows);

        model.addAttribute(
                "totalFees",
                totalFees);

        model.addAttribute(
                "totalPaid",
                totalPaid);

        model.addAttribute(
                "totalPending",
                Math.max(
                    0,
                    totalFees - totalPaid));

        List<FeePayment> payments =
                feePaymentRepository
                    .findByStudentIdOrderByPaymentDateDesc(
                        student.getId());

        model.addAttribute(
                "payments",
                payments);

        return "student/fees";
    }

    // =========================================================
    // PROFILE
    // =========================================================

    @GetMapping("/profile")
    public String profile(
            Authentication authentication,
            Model model) {

        Users user =
                currentUser(authentication);

        Students student =
                currentStudent(authentication);

        model.addAttribute(
                "user",
                user);

        model.addAttribute(
                "student",
                student);

        return "student/profile";
    }

    // =========================================================
    // CURRENT USER
    // =========================================================

    private Users currentUser(
            Authentication authentication) {

        return userRepository
                .findByUserName(
                    authentication.getName())
                .orElseThrow(() ->
                    new IllegalStateException(
                        "Logged-in user was not found."));
    }

    // =========================================================
    // CURRENT STUDENT
    // =========================================================

    private Students currentStudent(
            Authentication authentication) {

        Users user =
                currentUser(authentication);

        String email =
                user.getEmail() != null
                ? user.getEmail()
                : user.getUserName();

        return studentRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                    new IllegalStateException(
                        "Student profile was not found for "
                        + email));
    }

    // =========================================================
    // PUBLISHED TEST
    // =========================================================

    private Test publishedTest(
            Long id) {

        Test test =
                testRepository
                    .findById(id)
                    .orElse(null);

        if (test == null ||
            !"PUBLISHED".equalsIgnoreCase(
                test.getStatus())) {

            return null;
        }

        test.getQuestions().size();

        return test;
    }

    // =========================================================
    // AVAILABLE TESTS
    // =========================================================
    /*
     * IMPORTANT:
     *
     * This now returns ALL tests published by Admin.
     *
     * Previously the method filtered tests using the
     * student's enrolled course.
     *
     * That meant a published Admin test could disappear
     * from the Student Portal if the course name/code
     * did not exactly match.
     *
     * Now:
     *
     * Admin publishes test
     *        ↓
     * status = PUBLISHED
     *        ↓
     * Student Portal
     *        ↓
     * /student/tests
     *        ↓
     * Test appears
     */
    private List<Test> availableTests(
            Students student) {

        return testRepository
                .findByStatusIgnoreCase(
                    "PUBLISHED")
                .stream()
                .peek(test -> {

                    if (test.getQuestions() != null) {

                        test.getQuestions().size();
                    }

                })
                .collect(
                    Collectors.toList());
    }

    // =========================================================
    // TEST ACCESS
    // =========================================================

    private boolean canAccessTest(
            Test test,
            Students student) {

        /*
         * Since all published tests are now available
         * to students, only check that the test exists.
         */
        return test != null;
    }

    // =========================================================
    // ATTEMPT LIMIT
    // =========================================================

    private boolean canStart(
            Test test,
            List<TestAttempt> attempts) {

        if (test == null) {
            return false;
        }

        long submitted =
                attempts.stream()
                    .filter(a ->
                        "SUBMITTED"
                            .equalsIgnoreCase(
                                a.getStatus()))
                    .count();

        /*
         * Retake enabled
         */
        if (Boolean.TRUE.equals(
                test.getAllowTestRetake())) {

            int max =
                    test.getNumberOfAttempts()
                    == null
                    ? 1
                    : test.getNumberOfAttempts();

            return submitted < max;
        }

        /*
         * Retake disabled
         */
        return submitted == 0;
    }

    // =========================================================
    // STUDENT FEE ROW
    // =========================================================

    public record StudentFeeRow(
            String courseName,
            String courseCode,
            double fee,
            double paid,
            double pending) {
    }
}