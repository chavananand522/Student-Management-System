package com.itvedant.StudentManagement.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.itvedant.StudentManagement.dto.CourseDTO;
import com.itvedant.StudentManagement.dto.EnrollmentDTO;
import com.itvedant.StudentManagement.dto.EnrollmentSummeryDTO;
import com.itvedant.StudentManagement.model.Courses;
import com.itvedant.StudentManagement.model.Enrollment;
import com.itvedant.StudentManagement.model.Students;
import com.itvedant.StudentManagement.reposatory.CourseRepository;
import com.itvedant.StudentManagement.reposatory.EnrollmentRepository;
import com.itvedant.StudentManagement.reposatory.StudentRepositiry;
import com.itvedant.StudentManagement.services.EnrollmentService;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    private static final Logger log =
            LoggerFactory.getLogger(EnrollmentServiceImpl.class);

    // Hardcoded defaults (previously came from SettingsService)
    private static final boolean ALLOW_MULTIPLE_ENROLLMENT = true;
    private static final boolean PREVENT_DUPLICATE_ENROLLMENT = true;
    private static final int MAX_COURSES_PER_STUDENT = 5;

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepositiry studentRepository;
    private final CourseRepository courseRepository;
    private final ModelMapper mapper;

    public EnrollmentServiceImpl(
            EnrollmentRepository enrollmentRepository,
            StudentRepositiry studentRepository,
            CourseRepository courseRepository,
            ModelMapper mapper) {

        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.mapper = mapper;
    }

    @Override
    public void enrollStudentToCourses(EnrollmentDTO enrollmentDTO) {

        log.info("Request from enrollStudentToCourses");

        Students student = studentRepository
                .findById(enrollmentDTO.getStudentId())
                .orElseThrow(() ->
                        new RuntimeException("Student Not Found"));

        boolean allowMultipleEnrollment = ALLOW_MULTIPLE_ENROLLMENT;

        boolean preventDuplicateEnrollment = PREVENT_DUPLICATE_ENROLLMENT;

        int maxCoursesPerStudent = MAX_COURSES_PER_STUDENT;

        int currentCourseCount =
                student.getEnrollments().size();

        log.info("Student ID: {}", student.getId());
        log.info("Current course count: {}", currentCourseCount);
        log.info("Allow multiple enrollment: {}",
                allowMultipleEnrollment);
        log.info("Prevent duplicate enrollment: {}",
                preventDuplicateEnrollment);
        log.info("Maximum courses: {}", maxCoursesPerStudent);

        if (!allowMultipleEnrollment && currentCourseCount > 0) {
            throw new RuntimeException(
                    "Multiple course enrollment is disabled for this student.");
        }

        for (Long courseId : enrollmentDTO.getCourseIds()) {

            if (currentCourseCount >= maxCoursesPerStudent) {
                throw new RuntimeException(
                        "Student cannot enroll in more than "
                                + maxCoursesPerStudent + " courses.");
            }

            Courses course = courseRepository
                    .findById(courseId)
                    .orElseThrow(() ->
                            new RuntimeException("Course Not Found"));

            boolean alreadyEnrolled =
                    enrollmentRepository
                            .existsByStudentIdAndCourseId(
                                    enrollmentDTO.getStudentId(),
                                    courseId);

            if (preventDuplicateEnrollment && alreadyEnrolled) {

                log.info(
                        "Student {} is already enrolled in course {}",
                        student.getId(),
                        courseId);

                continue;
            }

            Enrollment enrollment = new Enrollment();

            enrollment.setStudent(student);
            enrollment.setCourse(course);
            enrollment.setEnrolledDate(LocalDateTime.now());

            student.getEnrollments().add(enrollment);
            course.getEnrollments().add(enrollment);

            enrollmentRepository.save(enrollment);

            currentCourseCount++;

            log.info(
                    "Student {} enrolled in course {} at {}",
                    student.getId(),
                    courseId,
                    enrollment.getEnrolledDate());
        }
    }

    @Override
    public Page<EnrollmentSummeryDTO> getEnrolledStudents(
            int page,
            int size) {

        log.info(
                "List of Enrolled Students from page {}",
                page);

        PageRequest pageRequest =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(Direction.DESC, "id"));

        return studentRepository.findEnrolledStudentIds(pageRequest);
    }

    @Override
    public EnrollmentSummeryDTO findEnrolledStudentCourseDetails(
            Long studentId) {

        return studentRepository
                .findEnrolledStudentCourseDetails(studentId)
                .map(student -> {

                    EnrollmentSummeryDTO dto =
                            new EnrollmentSummeryDTO();

                    dto.setStudentId(student.getId());

                    dto.setStudentName(
                            student.getFirstName()
                                    + " "
                                    + student.getLastName());

                    dto.setEmail(student.getEmail());

                    dto.setCourseCount(
                            student.getEnrollments().size());

                    BigDecimal totalFee =
                            student.getEnrollments()
                                    .stream()
                                    .map(enrollment ->
                                            enrollment.getCourse().getFee())
                                    .filter(fee -> fee != null)
                                    .reduce(
                                            BigDecimal.ZERO,
                                            BigDecimal::add);

                    dto.setTotalFee(totalFee);

                    List<CourseDTO> courseList =
                            student.getEnrollments()
                                    .stream()
                                    .map(Enrollment::getCourse)
                                    .map(course ->
                                            mapper.map(
                                                    course,
                                                    CourseDTO.class))
                                    .collect(Collectors.toList());

                    dto.setCourseList(courseList);

                    return dto;

                })
                .orElseThrow(() ->
                        new RuntimeException(
                                "Enrolled Student Not Found"));
    }

    @Override
    public List<EnrollmentSummeryDTO> getRecentlyEnrolledStudents(
            int page,
            int size) {

        log.info("List of Recently Enrolled Students");

        PageRequest pageRequest =
                PageRequest.of(page, size);

        // Step 1:
        // Get recently enrolled student IDs.
        Page<Long> idPage =
                studentRepository
                        .findRecentlyEnrolledStudentIds(
                                pageRequest);

        List<Long> ids = idPage.getContent();

        if (ids.isEmpty()) {
            return List.of();
        }

        // Step 2:
        // Fetch complete student + course information.
        List<Students> students =
                studentRepository
                        .findStudentsWithCoursesByIds(ids);

        // Convert students to Map using student ID.
        Map<Long, Students> studentsById =
                students.stream()
                        .collect(Collectors.toMap(
                                Students::getId,
                                student -> student));

        // Keep the same order as the IDs returned above.
        return ids.stream()
                .map(studentsById::get)
                .filter(student -> student != null)
                .map(student -> {

                    EnrollmentSummeryDTO dto =
                            new EnrollmentSummeryDTO();

                    dto.setStudentId(student.getId());

                    dto.setStudentName(
                            student.getFirstName()
                                    + " "
                                    + student.getLastName());

                    dto.setEmail(student.getEmail());

                    dto.setCourseCount(
                            student.getEnrollments().size());

                    BigDecimal totalFee =
                            student.getEnrollments()
                                    .stream()
                                    .map(enrollment ->
                                            enrollment.getCourse().getFee())
                                    .filter(fee -> fee != null)
                                    .reduce(
                                            BigDecimal.ZERO,
                                            BigDecimal::add);

                    dto.setTotalFee(totalFee);

                    List<CourseDTO> courseList =
                            student.getEnrollments()
                                    .stream()
                                    .map(Enrollment::getCourse)
                                    .map(course ->
                                            mapper.map(
                                                    course,
                                                    CourseDTO.class))
                                    .collect(Collectors.toList());

                    dto.setCourseList(courseList);

                    return dto;

                })
                .collect(Collectors.toList());
    }
}