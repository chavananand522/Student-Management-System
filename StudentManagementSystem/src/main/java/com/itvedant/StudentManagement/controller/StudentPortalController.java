package com.itvedant.StudentManagement.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
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
import com.itvedant.StudentManagement.reposatory.EnrollmentRepository;
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
    private final EnrollmentRepository enrollmentRepository;

    public StudentPortalController(
            StudentRepositiry studentRepository,
            UserRepository userRepository,
            TestRepository testRepository,
            TestAttemptRepository attemptRepository,
            StudyRepository studyRepository,
            ModuleRepository moduleRepository,
            ChapterRepository chapterRepository,
            FeePaymentRepository feePaymentRepository,
            EnrollmentRepository enrollmentRepository) {

        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.testRepository = testRepository;
        this.attemptRepository = attemptRepository;
        this.studyRepository = studyRepository;
        this.moduleRepository = moduleRepository;
        this.chapterRepository = chapterRepository;
        this.feePaymentRepository = feePaymentRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    // =========================================================
    // DASHBOARD
    // =========================================================

    @GetMapping({ "", "/dashboard" })
    @Transactional(readOnly = true)
    public String dashboard(
            Authentication authentication,
            Model model) {

        Students student = currentStudent(authentication);

        List<Enrollment> enrollments = student.getEnrollments();
        enrollments.size();

        long completedTests = attemptRepository
                .findByStudentUsername(authentication.getName())
                .stream()
                .filter(a -> "SUBMITTED".equalsIgnoreCase(a.getStatus()))
                .count();

        model.addAttribute("student", student);
        model.addAttribute("courseCount", enrollments.size());
        model.addAttribute("testCount", availableTests(student).size());
        model.addAttribute("completedTests", completedTests);

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

        Students student = currentStudent(authentication);

        List<Enrollment> enrollments = student.getEnrollments();
        enrollments.size();

        model.addAttribute("student", student);
        model.addAttribute("enrollments", enrollments);

        return "student/courses";
    }

    // =========================================================
    // STUDY
    // =========================================================

    @GetMapping("/study")
    public String study(Model model) {

        model.addAttribute("subjects", studyRepository.findAll());
        model.addAttribute("study", null);

        return "student/study";
    }

    @GetMapping("/study/{subject}")
    public String subject(
            @PathVariable String subject,
            Model model) {

        Study study = studyRepository
                .findBySubjectIgnoreCase(subject)
                .orElse(null);

        if (study == null) {
            return "redirect:/student/study";
        }

        model.addAttribute("study", study);
        model.addAttribute("subjectName", study.getSubject());
        model.addAttribute(
                "modules",
                moduleRepository
                    .findBySubjectIgnoreCaseOrderByModuleNumberAsc(
                        study.getSubject()));

        return "student/study";
    }

    @GetMapping("/study/{subject}/module/{moduleId}")
    public String module(
            @PathVariable String subject,
            @PathVariable Long moduleId,
            Model model) {

        Study study = studyRepository
                .findBySubjectIgnoreCase(subject)
                .orElse(null);

        Module module = moduleRepository
                .findById(moduleId)
                .orElse(null);

        if (study == null ||
            module == null ||
            !module.getSubject().equalsIgnoreCase(study.getSubject())) {

            return "redirect:/student/study";
        }

        model.addAttribute("study", study);
        model.addAttribute("subjectName", study.getSubject());
        model.addAttribute("module", module);
        model.addAttribute(
                "chapters",
                chapterRepository.findByModuleIdOrderByIdAsc(moduleId));

        return "student/module-view";
    }

    @GetMapping("/study/{subject}/module/{moduleId}/chapter/{chapterId}")
    public String chapter(
            @PathVariable String subject,
            @PathVariable Long moduleId,
            @PathVariable Long chapterId,
            Model model) {

        Study study = studyRepository
                .findBySubjectIgnoreCase(subject)
                .orElse(null);

        Module module = moduleRepository
                .findById(moduleId)
                .orElse(null);

        Chapter chapter = chapterRepository
                .findById(chapterId)
                .orElse(null);

        if (study == null ||
            module == null ||
            chapter == null ||
            !module.getSubject().equalsIgnoreCase(study.getSubject()) ||
            !moduleId.equals(chapter.getModuleId())) {

            return "redirect:/student/study";
        }

        model.addAttribute("study", study);
        model.addAttribute("subjectName", study.getSubject());
        model.addAttribute("module", module);
        model.addAttribute("chapter", chapter);

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

        Students student = currentStudent(authentication);

        List<Test> tests = availableTests(student);

        Map<Long, Integer> attempts = new LinkedHashMap<>();

        for (Test test : tests) {
            attempts.put(
                    test.getId(),
                    attemptRepository
                        .findByTestIdAndStudentUsername(
                            test.getId(),
                            authentication.getName())
                        .size());
        }

        model.addAttribute("tests", tests);
        model.addAttribute("attempts", attempts);

        return "student/tests";
    }

    // =========================================================
    // TEST ANALYSIS PAGE
    // =========================================================

    @GetMapping("/tests/analysis")
    @Transactional(readOnly = true)
    public String testAnalysis(
            Authentication authentication,
            Model model) {

        Students student = currentStudent(authentication);

        List<TestAttempt> attempts = attemptRepository
                .findByStudentUsername(authentication.getName())
                .stream()
                .filter(a -> "SUBMITTED".equalsIgnoreCase(a.getStatus()))
                .sorted(Comparator.comparing(
                        TestAttempt::getSubmittedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());

        List<AnalysisRow> rows = new ArrayList<>();

        for (TestAttempt attempt : attempts) {

            Test test = attempt.getTest();
            if (test == null) {
                continue;
            }

            String testName = test.getTestName() != null
                    ? test.getTestName()
                    : "Test #" + test.getId();

            int correct = attempt.getCorrectAnswers() == null
                    ? 0 : attempt.getCorrectAnswers();

            int wrong = attempt.getWrongAnswers() == null
                    ? 0 : attempt.getWrongAnswers();

            int unanswered = attempt.getUnansweredQuestions() == null
                    ? 0 : attempt.getUnansweredQuestions();

            int myMarks = attempt.getObtainedMarks() == null
                    ? 0 : attempt.getObtainedMarks();

            int topMarks = myMarks;
            String topName = null;

            try {

                List<TestAttempt> topList =
                        attemptRepository
                            .findFirstByTestIdAndStatusIgnoreCaseOrderByObtainedMarksDesc(
                                test.getId(),
                                "SUBMITTED");

                if (topList != null && !topList.isEmpty()) {

                    TestAttempt top = topList.get(0);

                    if (top.getObtainedMarks() != null) {
                        topMarks = top.getObtainedMarks();
                    }

                    List<String> topperNameList =
                            attemptRepository.findTopperNameByTestId(
                                    test.getId(),
                                    PageRequest.of(0, 1));

                    if (topperNameList != null && !topperNameList.isEmpty()) {
                        topName = resolveStudentName(topperNameList.get(0));
                    }
                }

                if (topName == null && topMarks == myMarks) {
                    topName = resolveStudentName(
                            attempt.getStudentUsername());
                }

            } catch (Exception ignored) {
            }

            rows.add(new AnalysisRow(
                    testName,
                    correct,
                    wrong,
                    unanswered,
                    myMarks,
                    topMarks,
                    topName
            ));
        }

        model.addAttribute("student", student);
        model.addAttribute("rows", rows);

        return "student/tests-analysis";
    }

    // =========================================================
    // TEST DETAILS
    // =========================================================

    @GetMapping("/tests/{id:\\d+}")
    @Transactional(readOnly = true)
    public String testDetails(
            @PathVariable Long id,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        Students student = currentStudent(authentication);

        Test test = publishedTest(id);

        if (!canAccessTest(test, student)) {
            redirectAttributes.addFlashAttribute(
                    "error", "Test is not available.");
            return "redirect:/student/tests";
        }

        List<TestAttempt> attempts = attemptRepository
                .findByTestIdAndStudentUsername(
                        id, authentication.getName());

        model.addAttribute("test", test);
        model.addAttribute("attempts", attempts);
        model.addAttribute("canStart", canStart(test, attempts));

        return "student/test-details";
    }

    // =========================================================
    // START TEST
    // =========================================================

    @PostMapping("/tests/{id:\\d+}/start")
    @Transactional
    public String startTest(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Students student = currentStudent(authentication);

        Test test = publishedTest(id);

        if (!canAccessTest(test, student)) {
            redirectAttributes.addFlashAttribute(
                    "error", "Test is not available.");
            return "redirect:/student/tests";
        }

        List<TestAttempt> attempts = attemptRepository
                .findByTestIdAndStudentUsername(
                        id, authentication.getName());

        TestAttempt inProgress = attempts.stream()
                .filter(a -> "IN_PROGRESS".equalsIgnoreCase(a.getStatus()))
                .findFirst()
                .orElse(null);

        if (inProgress != null) {
            return "redirect:/student/tests/attempt/" + inProgress.getId();
        }

        if (!canStart(test, attempts)) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "You have used all allowed attempts for this test.");
            return "redirect:/student/tests/" + id;
        }

        TestAttempt attempt = new TestAttempt();
        attempt.setTest(test);
        attempt.setStudentUsername(authentication.getName());
        attempt.setAttemptNumber(attempts.size() + 1);
        attempt.setStatus("IN_PROGRESS");
        attempt.setStartedAt(LocalDateTime.now());

        attemptRepository.save(attempt);

        return "redirect:/student/tests/attempt/" + attempt.getId();
    }

    // =========================================================
    // TAKE TEST
    // =========================================================

    @GetMapping("/tests/attempt/{attemptId:\\d+}")
    @Transactional(readOnly = true)
    public String attempt(
            @PathVariable Long attemptId,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        TestAttempt attempt = attemptRepository
                .findById(attemptId)
                .orElse(null);

        if (attempt == null ||
            !authentication.getName().equals(attempt.getStudentUsername())) {

            redirectAttributes.addFlashAttribute(
                    "error", "Test attempt not found.");
            return "redirect:/student/tests";
        }

        if (!"IN_PROGRESS".equalsIgnoreCase(attempt.getStatus())) {
            return "redirect:/student/results/" + attemptId;
        }

        Test test = attempt.getTest();
        test.getQuestions().size();

        List<Question> questions = new ArrayList<>(test.getQuestions());

        if (Boolean.TRUE.equals(test.getShuffleQuestions())) {
            Collections.shuffle(questions);
        }

        model.addAttribute("attempt", attempt);
        model.addAttribute("test", test);
        model.addAttribute("questions", questions);
        model.addAttribute("durationSeconds",
                (test.getDuration() == null ? 60 : test.getDuration()) * 60);

        return "student/test-attempt";
    }

    // =========================================================
    // SUBMIT TEST — NEGATIVE MARKING ENABLED
    // =========================================================

    @PostMapping("/tests/attempt/{attemptId:\\d+}/submit")
    @Transactional
    public String submitAttempt(
            @PathVariable Long attemptId,
            Authentication authentication,
            @RequestParam Map<String, String> form,
            RedirectAttributes redirectAttributes) {

        TestAttempt attempt = attemptRepository
                .findById(attemptId)
                .orElse(null);

        if (attempt == null ||
            !authentication.getName().equals(attempt.getStudentUsername())) {

            redirectAttributes.addFlashAttribute(
                    "error", "Test attempt not found.");
            return "redirect:/student/tests";
        }

        if (!"IN_PROGRESS".equalsIgnoreCase(attempt.getStatus())) {
            return "redirect:/student/results/" + attemptId;
        }

        Test test = attempt.getTest();
        test.getQuestions().size();

        // =====================================================
        // TIME LIMIT CHECK
        // =====================================================

        if (attempt.getStartedAt() != null && test.getDuration() != null) {

            long allowedSeconds = test.getDuration().longValue() * 60L;

            long elapsedSeconds = Duration.between(
                    attempt.getStartedAt(),
                    LocalDateTime.now()).getSeconds();

            if (elapsedSeconds > allowedSeconds + 30L) {

                attempt.setStatus("SUBMITTED");
                attempt.setSubmittedAt(LocalDateTime.now());
                attempt.setUnansweredQuestions(test.getQuestions().size());
                attempt.setCorrectAnswers(0);
                attempt.setWrongAnswers(0);
                attempt.setObtainedMarks(0);

                attemptRepository.save(attempt);

                redirectAttributes.addFlashAttribute(
                        "error",
                        "The test time expired. Your attempt was submitted.");

                return "redirect:/student/results/" + attemptId;
            }
        }

        // =====================================================
        // NEGATIVE MARKING CONFIG
        // =====================================================

        boolean negativeEnabled =
                Boolean.TRUE.equals(test.getNegativeMarking());

        int defaultPenalty =
                test.getNegativeMarks() == null
                        ? 1
                        : test.getNegativeMarks();

        int correct = 0;
        int wrong = 0;
        int unanswered = 0;
        int marks = 0;

        // =====================================================
        // GRADE EACH QUESTION
        // =====================================================

        for (Question question : test.getQuestions()) {

            String selected = form.get("answer_" + question.getId());

            if (selected == null || selected.trim().isEmpty()) {
                unanswered++;
                continue;
            }

            boolean isCorrect =
                    question.getCorrectAnswer() != null
                    && question.getCorrectAnswer().trim()
                        .equalsIgnoreCase(selected.trim());

            int questionMarks =
                    question.getMarks() == null
                            ? 1
                            : question.getMarks();

            int penalty = 0;

            if (negativeEnabled && !isCorrect) {

                penalty = (question.getNegativeMarks() != null)
                        ? question.getNegativeMarks()
                        : defaultPenalty;
            }

            int marksObtained =
                    isCorrect
                            ? questionMarks
                            : -penalty;

            StudentAnswer answer = new StudentAnswer();
            answer.setQuestion(question);
            answer.setSelectedAnswer(selected);
            answer.setCorrect(isCorrect);
            answer.setMarksObtained(marksObtained);

            attempt.addAnswer(answer);

            if (isCorrect) {
                correct++;
                marks += questionMarks;
            } else {
                wrong++;
                marks -= penalty;
            }
        }

        if (marks < 0) {
            marks = 0;
        }

        // =====================================================
        // SAVE RESULT
        // =====================================================

        attempt.setCorrectAnswers(correct);
        attempt.setWrongAnswers(wrong);
        attempt.setUnansweredQuestions(unanswered);
        attempt.setObtainedMarks(marks);
        attempt.setStatus("SUBMITTED");
        attempt.setSubmittedAt(LocalDateTime.now());

        attemptRepository.save(attempt);

        return "redirect:/student/results/" + attemptId;
    }

    // =========================================================
    // RESULTS
    // =========================================================

    @GetMapping("/results")
    @Transactional(readOnly = true)
    public String results(
            Authentication authentication,
            Model model) {

        List<TestAttempt> attempts = attemptRepository
                .findByStudentUsername(authentication.getName())
                .stream()
                .sorted(Comparator.comparing(
                        TestAttempt::getSubmittedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());

        model.addAttribute("attempts", attempts);

        return "student/results";
    }

    // =========================================================
    // SINGLE RESULT — WITH ALL QUESTIONS (answered + unanswered)
    // =========================================================

    @GetMapping("/results/{attemptId:\\d+}")
    @Transactional(readOnly = true)
    public String result(
            @PathVariable Long attemptId,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        TestAttempt attempt = attemptRepository
                .findById(attemptId)
                .orElse(null);

        if (attempt == null ||
            !authentication.getName().equals(attempt.getStudentUsername())) {

            redirectAttributes.addFlashAttribute(
                    "error", "Result not found.");
            return "redirect:/student/results";
        }

        Test test = attempt.getTest();

        // =========================================================
        // BUILD A COMPLETE ANSWER LIST
        // =========================================================
        // The attempt only stores answers for questions the student
        // actually interacted with. We build one entry per question
        // in the test so the navigation grid can show correct,
        // wrong, AND unanswered tiles.
        // =========================================================

        // 1. All questions belonging to the test
        List<Question> allQuestions = new ArrayList<>(test.getQuestions());

        // 2. Index the student's existing answers by question id
        Map<Long, StudentAnswer> answeredByQuestionId = new HashMap<>();

        List<StudentAnswer> existingAnswers = attempt.getAnswers();
        if (existingAnswers != null) {
            for (StudentAnswer ans : existingAnswers) {
                if (ans.getQuestion() != null) {
                    answeredByQuestionId.put(ans.getQuestion().getId(), ans);
                }
            }
        }

        // 3. Build the full list — one entry per question
        List<StudentAnswer> fullAnswerList = new ArrayList<>();

        for (Question q : allQuestions) {

            StudentAnswer ans = answeredByQuestionId.get(q.getId());

            if (ans == null) {
                // Placeholder for unanswered questions
                ans = new StudentAnswer();
                ans.setQuestion(q);
                ans.setSelectedAnswer(null);   // triggers 'never-opened' CSS
                ans.setCorrect(false);
                ans.setMarksObtained(0);
                ans.setAttempt(attempt);       // ← matches mappedBy = "attempt"
            }

            fullAnswerList.add(ans);
        }

        // 4. Sort by question id
        fullAnswerList.sort(
                Comparator.comparing(a -> a.getQuestion().getId()));

        // 5. Replace the answer list with the complete one
        attempt.setAnswers(fullAnswerList);

        model.addAttribute("attempt", attempt);
        model.addAttribute("test", test);

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

        Students student = currentStudent(authentication);

        List<Enrollment> enrollments = student.getEnrollments();
        enrollments.size();

        List<StudentFeeRow> rows = new ArrayList<>();
        double totalFees = 0;
        double totalPaid = 0;

        for (Enrollment enrollment : enrollments) {

            Courses course = enrollment.getCourse();

            if (course == null) continue;

            double fee = course.getFee() == null
                    ? 0 : course.getFee().doubleValue();

            double paid = feePaymentRepository
                    .getPaidByStudentAndCourse(
                            student.getId(), course.getId());

            double pending = Math.max(0, fee - paid);

            rows.add(new StudentFeeRow(
                    course.getCourseName(),
                    course.getCourseCode(),
                    fee, paid, pending));

            totalFees += fee;
            totalPaid += paid;
        }

        model.addAttribute("rows", rows);
        model.addAttribute("totalFees", totalFees);
        model.addAttribute("totalPaid", totalPaid);
        model.addAttribute("totalPending", Math.max(0, totalFees - totalPaid));

        List<FeePayment> payments = feePaymentRepository
                .findByStudentIdOrderByPaymentDateDesc(student.getId());

        model.addAttribute("payments", payments);

        return "student/fees";
    }

    // =========================================================
    // PROFILE
    // =========================================================

    @GetMapping("/profile")
    @Transactional(readOnly = true)
    public String profile(
            Authentication authentication,
            Model model) {

        Users user = currentUser(authentication);
        Students student = currentStudent(authentication);

        List<Enrollment> enrollments = student.getEnrollments();
        enrollments.size();

        model.addAttribute("user", user);
        model.addAttribute("student", student);
        model.addAttribute("courseCount", enrollments.size());

        return "student/profile";
    }

    // =========================================================
    // ID CARD
    // =========================================================

    @GetMapping("/id-card")
    @Transactional(readOnly = true)
    public String idCard(
            Authentication authentication,
            Model model) {

        Students student = currentStudent(authentication);

        List<Enrollment> enrollments = new ArrayList<>();

        try {
            enrollments = enrollmentRepository
                    .findByStudentId(student.getId());
        } catch (Exception ignored) { }

        if (enrollments.isEmpty()) {
            List<Enrollment> direct = student.getEnrollments();
            if (direct != null) {
                direct.size();
                enrollments = direct;
            }
        }

        student.getFirstName();
        student.getLastName();
        student.getEmail();
        student.getPhoneNumber();

        model.addAttribute("student", student);
        model.addAttribute("courseCount", enrollments.size());

        return "student/id-card";
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private Users currentUser(Authentication authentication) {
        return userRepository
                .findByUserName(authentication.getName())
                .orElseThrow(() -> new IllegalStateException(
                        "Logged-in user was not found."));
    }

    private Students currentStudent(Authentication authentication) {
        Users user = currentUser(authentication);

        String email = user.getEmail() != null
                ? user.getEmail()
                : user.getUserName();

        return studentRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalStateException(
                        "Student profile was not found for " + email));
    }

    private Test publishedTest(Long id) {

        Test test = testRepository.findById(id).orElse(null);

        if (test == null ||
            !"PUBLISHED".equalsIgnoreCase(test.getStatus())) {
            return null;
        }

        test.getQuestions().size();
        return test;
    }

    private List<Test> availableTests(Students student) {
        return testRepository
                .findByStatusIgnoreCase("PUBLISHED")
                .stream()
                .peek(test -> {
                    if (test.getQuestions() != null) {
                        test.getQuestions().size();
                    }
                })
                .collect(Collectors.toList());
    }

    private boolean canAccessTest(Test test, Students student) {
        return test != null;
    }

    private boolean canStart(Test test, List<TestAttempt> attempts) {

        if (test == null) return false;

        long submitted = attempts.stream()
                .filter(a -> "SUBMITTED".equalsIgnoreCase(a.getStatus()))
                .count();

        if (Boolean.TRUE.equals(test.getAllowTestRetake())) {
            int max = test.getNumberOfAttempts() == null
                    ? 1 : test.getNumberOfAttempts();
            return submitted < max;
        }

        return submitted == 0;
    }

    private String resolveStudentName(String username) {

        if (username == null) return "Student";

        try {

            Users user = userRepository
                    .findByUserName(username)
                    .orElse(null);

            if (user != null) {

                if (user.getFullName() != null &&
                    !user.getFullName().isBlank()) {
                    return user.getFullName();
                }

                if (user.getEmail() != null &&
                    !user.getEmail().isBlank()) {
                    return user.getEmail();
                }
            }

            Students student = studentRepository
                    .findByEmailIgnoreCase(username)
                    .orElse(null);

            if (student != null) {

                String first = student.getFirstName() == null
                        ? "" : student.getFirstName();

                String last = student.getLastName() == null
                        ? "" : student.getLastName();

                String full = (first + " " + last).trim();

                if (!full.isEmpty()) return full;
            }

        } catch (Exception ignored) {
        }

        return username;
    }

    // =========================================================
    // RECORDS
    // =========================================================

    public record StudentFeeRow(
            String courseName,
            String courseCode,
            double fee,
            double paid,
            double pending) {
    }

    public record AnalysisRow(
            String testName,
            int correct,
            int wrong,
            int unanswered,
            int obtainedMarks,
            int topperMarks,
            String topperName) {
    }
}