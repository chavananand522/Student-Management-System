package com.itvedant.StudentManagement.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.itvedant.StudentManagement.services.StudentAttendanceService;

@Controller
@RequestMapping("/student")
public class StudentPortalController {

    private static final Logger log = LoggerFactory.getLogger(StudentPortalController.class);

    /** Grace period (seconds) allowed past the test duration before auto-submit. */
    private static final long TIME_LIMIT_GRACE_SECONDS = 30L;

    /** How many tests to show in the dashboard "Available Tests" card. */
    private static final int DASHBOARD_TEST_LIMIT = 3;

    private final StudentRepositiry studentRepository;
    private final UserRepository userRepository;
    private final TestRepository testRepository;
    private final TestAttemptRepository attemptRepository;
    private final StudyRepository studyRepository;
    private final ModuleRepository moduleRepository;
    private final ChapterRepository chapterRepository;
    private final FeePaymentRepository feePaymentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentAttendanceService attendanceService;

    public StudentPortalController(StudentRepositiry studentRepository, UserRepository userRepository,
            TestRepository testRepository, TestAttemptRepository attemptRepository, StudyRepository studyRepository,
            ModuleRepository moduleRepository, ChapterRepository chapterRepository,
            FeePaymentRepository feePaymentRepository, EnrollmentRepository enrollmentRepository,
            StudentAttendanceService attendanceService) {

        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.testRepository = testRepository;
        this.attemptRepository = attemptRepository;
        this.studyRepository = studyRepository;
        this.moduleRepository = moduleRepository;
        this.chapterRepository = chapterRepository;
        this.feePaymentRepository = feePaymentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.attendanceService = attendanceService;
    }

    // =========================================================
    // DASHBOARD
    // =========================================================

    @GetMapping({ "", "/dashboard" })
    @Transactional(readOnly = true)
    public String dashboard(Authentication authentication, Model model) {

        Students student = currentStudent(authentication);

        List<Enrollment> enrollments = new ArrayList<>(student.getEnrollments());

        List<TestAttempt> myAttempts = attemptRepository.findByStudentUsername(authentication.getName());

        long completedTests = myAttempts.stream()
                .filter(a -> "SUBMITTED".equalsIgnoreCase(a.getStatus())).count();

        // Test ids the student has already submitted
        Set<Long> attemptedTestIds = new HashSet<>();
        for (TestAttempt a : myAttempts) {
            if ("SUBMITTED".equalsIgnoreCase(a.getStatus()) && a.getTest() != null) {
                attemptedTestIds.add(a.getTest().getId());
            }
        }

        // ---- Attendance: same service the attendance page uses ----
        long totalClasses = 0;
        long presentDays = 0;
        long absentDays = 0;
        long lateDays = 0;
        double attendancePercentage = 0;

        try {
            Long studentId = student.getId();

            totalClasses = attendanceService.getTotalCount(studentId);
            presentDays = attendanceService.getPresentCount(studentId);
            absentDays = attendanceService.getAbsentCount(studentId);
            lateDays = attendanceService.getLateCount(studentId);
            attendancePercentage = attendanceService.getOverallPercentage(studentId);

            // one decimal place, clamped to 0-100
            attendancePercentage = Math.round(attendancePercentage * 10.0) / 10.0;
            attendancePercentage = Math.max(0, Math.min(100, attendancePercentage));

        } catch (Exception ex) {
            log.warn("Attendance summary failed for student {}", student.getId(), ex);
        }

        // ---- Available tests for the dashboard card ----
        List<Test> tests = availableTests(student);
        List<DashboardTestRow> upcomingTests = tests.stream().map(t -> {
            int questionCount = t.getQuestions() == null ? 0 : t.getQuestions().size();
            int totalMarks = t.getQuestions() == null ? 0
                    : t.getQuestions().stream().mapToInt(q -> q.getMarks() == null ? 1 : q.getMarks()).sum();
            int duration = t.getDuration() == null ? 0 : t.getDuration();
            String name = t.getTestName() != null ? t.getTestName() : "Test #" + t.getId();
            return new DashboardTestRow(t.getId(), name, questionCount, totalMarks, duration,
                    attemptedTestIds.contains(t.getId()));
        })
                // not-attempted tests first
                .sorted(Comparator.comparing(DashboardTestRow::attempted))
                .limit(DASHBOARD_TEST_LIMIT).collect(Collectors.toList());

        model.addAttribute("student", student);
        model.addAttribute("courseCount", enrollments.size());
        model.addAttribute("testCount", tests.size());
        model.addAttribute("completedTests", completedTests);
        model.addAttribute("upcomingTests", upcomingTests);

        model.addAttribute("totalClasses", totalClasses);
        model.addAttribute("presentDays", presentDays);
        model.addAttribute("absentDays", absentDays);
        model.addAttribute("lateDays", lateDays);
        model.addAttribute("attendancePercentage", attendancePercentage);

        return "student/dashboard";
    }

    // =========================================================
    // COURSES
    // =========================================================

    @GetMapping("/courses")
    @Transactional(readOnly = true)
    public String courses(Authentication authentication, Model model) {

        Students student = currentStudent(authentication);

        List<Enrollment> enrollments = new ArrayList<>(student.getEnrollments());

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
    public String subject(@PathVariable String subject, Model model) {

        Study study = studyRepository.findBySubjectIgnoreCase(subject).orElse(null);

        if (study == null) {
            return "redirect:/student/study";
        }

        model.addAttribute("study", study);
        model.addAttribute("subjectName", study.getSubject());
        model.addAttribute("modules",
                moduleRepository.findBySubjectIgnoreCaseOrderByModuleNumberAsc(study.getSubject()));

        return "student/study";
    }

    @GetMapping("/study/{subject}/module/{moduleId}")
    public String module(@PathVariable String subject, @PathVariable Long moduleId, Model model) {

        Study study = studyRepository.findBySubjectIgnoreCase(subject).orElse(null);

        Module module = moduleRepository.findById(moduleId).orElse(null);

        if (study == null || module == null || !module.getSubject().equalsIgnoreCase(study.getSubject())) {

            return "redirect:/student/study";
        }

        model.addAttribute("study", study);
        model.addAttribute("subjectName", study.getSubject());
        model.addAttribute("module", module);
        model.addAttribute("chapters", chapterRepository.findByModuleIdOrderByIdAsc(moduleId));

        return "student/module-view";
    }

    @GetMapping("/study/{subject}/module/{moduleId}/chapter/{chapterId}")
    public String chapter(@PathVariable String subject, @PathVariable Long moduleId, @PathVariable Long chapterId,
            Model model) {

        Study study = studyRepository.findBySubjectIgnoreCase(subject).orElse(null);

        Module module = moduleRepository.findById(moduleId).orElse(null);

        Chapter chapter = chapterRepository.findById(chapterId).orElse(null);

        if (study == null || module == null || chapter == null
                || !module.getSubject().equalsIgnoreCase(study.getSubject())
                || !moduleId.equals(chapter.getModuleId())) {

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
    public String tests(Authentication authentication, Model model) {

        Students student = currentStudent(authentication);

        List<Test> tests = availableTests(student);

        // Single grouped query instead of one query per test (fixes N+1).
        Map<Long, Integer> attempts = new LinkedHashMap<>();
        try {
            // Repository returns List<Object[]> of [testId, count].
            List<Object[]> rows = attemptRepository.countAttemptsByStudentGroupedByTest(authentication.getName());

            Map<Long, Long> counts = new HashMap<>();
            for (Object[] row : rows) {
                Long testId = ((Number) row[0]).longValue();
                Long count = ((Number) row[1]).longValue();
                counts.put(testId, count);
            }

            for (Test test : tests) {
                attempts.put(test.getId(), counts.getOrDefault(test.getId(), 0L).intValue());
            }
        } catch (Exception ex) {
            // Fallback to per-test count if the aggregate query is unavailable.
            log.warn("Grouped attempt count failed, falling back to per-test counts", ex);
            for (Test test : tests) {
                attempts.put(test.getId(), attemptRepository
                        .findByTestIdAndStudentUsername(test.getId(), authentication.getName()).size());
            }
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
    public String testAnalysis(Authentication authentication, Model model) {

        Students student = currentStudent(authentication);

        List<TestAttempt> attempts = attemptRepository.findByStudentUsername(authentication.getName()).stream()
                .filter(a -> "SUBMITTED".equalsIgnoreCase(a.getStatus())).sorted(Comparator
                        .comparing(TestAttempt::getSubmittedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());

        List<AnalysisRow> rows = new ArrayList<>();

        for (TestAttempt attempt : attempts) {

            Test test = attempt.getTest();
            if (test == null) {
                continue;
            }

            String testName = test.getTestName() != null ? test.getTestName() : "Test #" + test.getId();

            int correct = nz(attempt.getCorrectAnswers());
            int wrong = nz(attempt.getWrongAnswers());
            int unanswered = nz(attempt.getUnansweredQuestions());
            int myMarks = nz(attempt.getObtainedMarks());

            int topMarks = myMarks;
            String topName = null;

            try {
                List<TestAttempt> topList = attemptRepository
                        .findFirstByTestIdAndStatusIgnoreCaseOrderByObtainedMarksDesc(test.getId(), "SUBMITTED");

                if (topList != null && !topList.isEmpty()) {
                    TestAttempt top = topList.get(0);
                    if (top.getObtainedMarks() != null) {
                        topMarks = top.getObtainedMarks();
                    }

                    List<String> topperNameList = attemptRepository.findTopperNameByTestId(test.getId(),
                            PageRequest.of(0, 1));

                    if (topperNameList != null && !topperNameList.isEmpty()) {
                        topName = resolveStudentName(topperNameList.get(0));
                    }
                }

                if (topName == null && topMarks == myMarks) {
                    topName = resolveStudentName(attempt.getStudentUsername());
                }
            } catch (Exception ex) {
                log.warn("Failed to compute topper for test {}", test.getId(), ex);
            }

            rows.add(new AnalysisRow(testName, correct, wrong, unanswered, myMarks, topMarks, topName));
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
    public String testDetails(@PathVariable Long id, Authentication authentication, Model model,
            RedirectAttributes redirectAttributes) {

        Students student = currentStudent(authentication);

        Test test = publishedTest(id);

        if (!canAccessTest(test, student)) {
            redirectAttributes.addFlashAttribute("error", "Test is not available.");
            return "redirect:/student/tests";
        }

        List<TestAttempt> attempts = attemptRepository.findByTestIdAndStudentUsername(id, authentication.getName());

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
    public String startTest(@PathVariable Long id, Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Students student = currentStudent(authentication);

        Test test = publishedTest(id);

        if (!canAccessTest(test, student)) {
            redirectAttributes.addFlashAttribute("error", "Test is not available.");
            return "redirect:/student/tests";
        }

        List<TestAttempt> attempts = attemptRepository.findByTestIdAndStudentUsername(id, authentication.getName());

        TestAttempt inProgress = attempts.stream().filter(a -> "IN_PROGRESS".equalsIgnoreCase(a.getStatus()))
                .findFirst().orElse(null);

        if (inProgress != null) {
            return "redirect:/student/tests/attempt/" + inProgress.getId();
        }

        if (!canStart(test, attempts)) {
            redirectAttributes.addFlashAttribute("error", "You have used all allowed attempts for this test.");
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
    public String attempt(@PathVariable Long attemptId, Authentication authentication, Model model,
            RedirectAttributes redirectAttributes) {

        TestAttempt attempt = attemptRepository.findById(attemptId).orElse(null);

        if (attempt == null || !authentication.getName().equals(attempt.getStudentUsername())) {
            redirectAttributes.addFlashAttribute("error", "Test attempt not found.");
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
        model.addAttribute("durationSeconds", (test.getDuration() == null ? 60 : test.getDuration()) * 60);

        return "student/test-attempt";
    }

    // =========================================================
    // SUBMIT TEST — NEGATIVE MARKING ENABLED
    // =========================================================

    @PostMapping("/tests/attempt/{attemptId:\\d+}/submit")
    @Transactional
    public String submitAttempt(@PathVariable Long attemptId, Authentication authentication,
            @RequestParam Map<String, String> form, RedirectAttributes redirectAttributes) {

        TestAttempt attempt = attemptRepository.findById(attemptId).orElse(null);

        if (attempt == null || !authentication.getName().equals(attempt.getStudentUsername())) {
            redirectAttributes.addFlashAttribute("error", "Test attempt not found.");
            return "redirect:/student/tests";
        }

        if (!"IN_PROGRESS".equalsIgnoreCase(attempt.getStatus())) {
            return "redirect:/student/results/" + attemptId;
        }

        Test test = attempt.getTest();
        test.getQuestions().size();

        // ---- TIME LIMIT CHECK ----
        if (attempt.getStartedAt() != null && test.getDuration() != null) {

            long allowedSeconds = test.getDuration().longValue() * 60L;

            long elapsedSeconds = Duration.between(attempt.getStartedAt(), LocalDateTime.now()).getSeconds();

            if (elapsedSeconds > allowedSeconds + TIME_LIMIT_GRACE_SECONDS) {

                attempt.setStatus("SUBMITTED");
                attempt.setSubmittedAt(LocalDateTime.now());
                attempt.setUnansweredQuestions(test.getQuestions().size());
                attempt.setCorrectAnswers(0);
                attempt.setWrongAnswers(0);
                attempt.setObtainedMarks(0);

                attemptRepository.save(attempt);

                redirectAttributes.addFlashAttribute("error", "The test time expired. Your attempt was submitted.");

                return "redirect:/student/results/" + attemptId;
            }
        }

        // ---- NEGATIVE MARKING CONFIG ----
        boolean negativeEnabled = Boolean.TRUE.equals(test.getNegativeMarking());
        int defaultPenalty = test.getNegativeMarks() == null ? 1 : test.getNegativeMarks();

        int correct = 0;
        int wrong = 0;
        int unanswered = 0;
        int marks = 0;

        // ---- GRADE EACH QUESTION ----
        for (Question question : test.getQuestions()) {

            String selected = form.get("answer_" + question.getId());

            if (selected == null || selected.trim().isEmpty()) {
                unanswered++;
                continue;
            }

            boolean isCorrect = question.getCorrectAnswer() != null
                    && question.getCorrectAnswer().trim().equalsIgnoreCase(selected.trim());

            int questionMarks = question.getMarks() == null ? 1 : question.getMarks();

            int penalty = 0;
            if (negativeEnabled && !isCorrect) {
                penalty = (question.getNegativeMarks() != null) ? question.getNegativeMarks() : defaultPenalty;
            }

            int marksObtained = isCorrect ? questionMarks : -penalty;

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
    public String results(Authentication authentication, Model model) {

        List<TestAttempt> attempts = attemptRepository
                .findByStudentUsername(authentication.getName()).stream().sorted(Comparator
                        .comparing(TestAttempt::getSubmittedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());

        model.addAttribute("attempts", attempts);

        return "student/results";
    }

    // =========================================================
    // SINGLE RESULT — WITH ALL QUESTIONS (answered + unanswered)
    // =========================================================

    @GetMapping("/results/{attemptId:\\d+}")
    @Transactional(readOnly = true)
    public String result(@PathVariable Long attemptId, Authentication authentication, Model model,
            RedirectAttributes redirectAttributes) {

        TestAttempt attempt = attemptRepository.findById(attemptId).orElse(null);

        if (attempt == null || !authentication.getName().equals(attempt.getStudentUsername())) {
            redirectAttributes.addFlashAttribute("error", "Result not found.");
            return "redirect:/student/results";
        }

        Test test = attempt.getTest();
        if (test == null) {
            redirectAttributes.addFlashAttribute("error", "Result not found.");
            return "redirect:/student/results";
        }

        // Build a view-model list (do NOT mutate the managed entity).
        List<Question> allQuestions = new ArrayList<>(test.getQuestions());

        Map<Long, StudentAnswer> answeredByQuestionId = new HashMap<>();
        List<StudentAnswer> existingAnswers = attempt.getAnswers();
        if (existingAnswers != null) {
            for (StudentAnswer ans : existingAnswers) {
                if (ans.getQuestion() != null) {
                    answeredByQuestionId.put(ans.getQuestion().getId(), ans);
                }
            }
        }

        List<StudentAnswer> fullAnswerList = new ArrayList<>(allQuestions.size());

        for (Question q : allQuestions) {

            StudentAnswer ans = answeredByQuestionId.get(q.getId());

            if (ans == null) {
                // Transient placeholder — never persisted because we do NOT
                // attach it to the managed attempt.
                ans = new StudentAnswer();
                ans.setQuestion(q);
                ans.setSelectedAnswer(null);
                ans.setCorrect(false);
                ans.setMarksObtained(0);
            }

            fullAnswerList.add(ans);
        }

        fullAnswerList.sort(Comparator.comparing(a -> a.getQuestion().getId()));

        model.addAttribute("attempt", attempt);
        model.addAttribute("test", test);
        model.addAttribute("answers", fullAnswerList);

        return "student/result";
    }

    // =========================================================
    // FEES
    // =========================================================

    @GetMapping("/fees")
    @Transactional(readOnly = true)
    public String fees(Authentication authentication, Model model) {

        Students student = currentStudent(authentication);

        List<Enrollment> enrollments = new ArrayList<>(student.getEnrollments());

        List<StudentFeeRow> rows = new ArrayList<>();
        double totalFees = 0;
        double totalPaid = 0;

        for (Enrollment enrollment : enrollments) {

            Courses course = enrollment.getCourse();
            if (course == null)
                continue;

            double fee = course.getFee() == null ? 0 : course.getFee().doubleValue();

            double paid = feePaymentRepository.getPaidByStudentAndCourse(student.getId(), course.getId());

            double pending = Math.max(0, fee - paid);

            rows.add(new StudentFeeRow(course.getCourseName(), course.getCourseCode(), fee, paid, pending));

            totalFees += fee;
            totalPaid += paid;
        }

        model.addAttribute("rows", rows);
        model.addAttribute("totalFees", totalFees);
        model.addAttribute("totalPaid", totalPaid);
        model.addAttribute("totalPending", Math.max(0, totalFees - totalPaid));

        List<FeePayment> payments = feePaymentRepository.findByStudentIdOrderByPaymentDateDesc(student.getId());

        model.addAttribute("payments", payments);

        return "student/fees";
    }

    // =========================================================
    // PROFILE
    // =========================================================

    @GetMapping("/profile")
    @Transactional(readOnly = true)
    public String profile(Authentication authentication, Model model) {

        Users user = currentUser(authentication);
        Students student = currentStudent(authentication);

        List<Enrollment> enrollments = new ArrayList<>(student.getEnrollments());

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
    public String idCard(Authentication authentication, Model model) {

        Students student = currentStudent(authentication);

        List<Enrollment> enrollments = new ArrayList<>();

        try {
            enrollments = enrollmentRepository.findByStudentId(student.getId());
        } catch (Exception ex) {
            log.warn("Enrollment lookup by studentId failed for {}", student.getId(), ex);
        }

        if (enrollments.isEmpty()) {
            List<Enrollment> direct = student.getEnrollments();
            if (direct != null) {
                enrollments = new ArrayList<>(direct);
            }
        }

        // Force-initialize simple fields used by the view.
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
        return userRepository.findByUserName(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Logged-in user was not found."));
    }

    private Students currentStudent(Authentication authentication) {

        Users user = currentUser(authentication);

        // Prefer matching by the user's email; fall back to username
        // matched against email only if the user has no email.
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            Students byEmail = studentRepository.findByEmailIgnoreCase(user.getEmail()).orElse(null);
            if (byEmail != null) {
                return byEmail;
            }
        }

        Students byUsername = studentRepository.findByEmailIgnoreCase(user.getUserName()).orElse(null);
        if (byUsername != null) {
            return byUsername;
        }

        throw new IllegalStateException("Student profile was not found for user " + user.getUserName());
    }

    private Test publishedTest(Long id) {

        Test test = testRepository.findById(id).orElse(null);

        if (test == null || !"PUBLISHED".equalsIgnoreCase(test.getStatus())) {
            return null;
        }

        test.getQuestions().size();
        return test;
    }

    private List<Test> availableTests(Students student) {
        // NOTE: currently returns all published tests regardless of student.
        // If tests should be scoped by enrollment, add the filter here.
        return testRepository.findByStatusIgnoreCase("PUBLISHED").stream().peek(test -> {
            if (test.getQuestions() != null) {
                test.getQuestions().size();
            }
        }).collect(Collectors.toList());
    }

    private boolean canAccessTest(Test test, Students student) {
        if (test == null) {
            return false;
        }
        // TODO: enforce enrollment / assignment checks if required.
        return true;
    }

    private boolean canStart(Test test, List<TestAttempt> attempts) {

        if (test == null)
            return false;

        long submitted = attempts.stream().filter(a -> "SUBMITTED".equalsIgnoreCase(a.getStatus())).count();

        if (Boolean.TRUE.equals(test.getAllowTestRetake())) {
            int max = test.getNumberOfAttempts() == null ? 1 : test.getNumberOfAttempts();
            return submitted < max;
        }

        return submitted == 0;
    }

    private String resolveStudentName(String username) {

        if (username == null)
            return "Student";

        try {
            Users user = userRepository.findByUserName(username).orElse(null);

            if (user != null) {
                if (user.getFullName() != null && !user.getFullName().isBlank()) {
                    return user.getFullName();
                }
                if (user.getEmail() != null && !user.getEmail().isBlank()) {
                    return user.getEmail();
                }
            }

            Students student = studentRepository.findByEmailIgnoreCase(username).orElse(null);

            if (student != null) {
                String first = student.getFirstName() == null ? "" : student.getFirstName();
                String last = student.getLastName() == null ? "" : student.getLastName();
                String full = (first + " " + last).trim();
                if (!full.isEmpty())
                    return full;
            }
        } catch (Exception ex) {
            log.warn("Failed to resolve student name for {}", username, ex);
        }

        return username;
    }

    private static int nz(Integer value) {
        return value == null ? 0 : value;
    }

    // =========================================================
    // RECORDS
    // =========================================================

    public record StudentFeeRow(String courseName, String courseCode, double fee, double paid, double pending) {
    }

    public record AnalysisRow(String testName, int correct, int wrong, int unanswered, int obtainedMarks,
            int topperMarks, String topperName) {
    }

    public record DashboardTestRow(Long id, String testName, int questionCount, int totalMarks, int duration,
            boolean attempted) {
    }
}