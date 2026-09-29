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
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public void enrollStudentToCourses(
            EnrollmentDTO enrollmentDTO) {

        log.info("Request from enrollStudentToCourses");

        Long studentId =
                enrollmentDTO.getStudentId();

        Students student =
                studentRepository.findById(studentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Student Not Found"
                                ));

        List<Long> requestedCourseIds =
                enrollmentDTO.getCourseIds() == null
                        ? List.of()
                        : enrollmentDTO.getCourseIds()
                                .stream()
                                .distinct()
                                .toList();

        List<Enrollment> existingEnrollments =
                enrollmentRepository.findByStudentId(
                        studentId
                );

        int currentCourseCount =
                existingEnrollments.size();

        log.info(
                "Student ID: {}",
                studentId
        );

        log.info(
                "Current course count: {}",
                currentCourseCount
        );

        if (!ALLOW_MULTIPLE_ENROLLMENT &&
                currentCourseCount > 0) {

            throw new RuntimeException(
                    "Multiple course enrollment is disabled for this student."
            );
        }

        long newCoursesToAdd =
                requestedCourseIds.stream()
                        .filter(courseId ->
                                !PREVENT_DUPLICATE_ENROLLMENT
                                        || !enrollmentRepository
                                        .existsByStudentIdAndCourseId(
                                                studentId,
                                                courseId
                                        ))
                        .count();

        if (currentCourseCount + newCoursesToAdd >
                MAX_COURSES_PER_STUDENT) {

            throw new RuntimeException(
                    "Only can enroll in "
                            + MAX_COURSES_PER_STUDENT
                            + " courses"
            );
        }

        for (Long courseId : requestedCourseIds) {

            Courses course =
                    courseRepository.findById(courseId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Course Not Found"
                                    ));

            boolean alreadyEnrolled =
                    enrollmentRepository
                            .existsByStudentIdAndCourseId(
                                    studentId,
                                    courseId
                            );

            if (PREVENT_DUPLICATE_ENROLLMENT &&
                    alreadyEnrolled) {

                log.info(
                        "Student {} already enrolled in course {}",
                        studentId,
                        courseId
                );

                continue;
            }

            Enrollment enrollment =
                    new Enrollment();

            enrollment.setStudent(student);

            enrollment.setCourse(course);

            enrollment.setEnrolledDate(
                    LocalDateTime.now()
            );

            enrollmentRepository.save(
                    enrollment
            );

            currentCourseCount++;

            log.info(
                    "Added course {} to student {}",
                    courseId,
                    studentId
            );
        }
    }

    @Override
    public Page<EnrollmentSummeryDTO> getEnrolledStudents(
            int page,
            int size) {

        log.info(
                "List of Enrolled Students from page {}",
                page
        );

        PageRequest pageRequest =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                Direction.DESC,
                                "id"
                        )
                );

        return studentRepository
                .findEnrolledStudentIds(
                        pageRequest
                );
    }

    @Override
    @Transactional(readOnly = true)
    public EnrollmentSummeryDTO findEnrolledStudentCourseDetails(
            Long studentId) {

        return studentRepository
                .findEnrolledStudentCourseDetails(
                        studentId
                )
                .map(student -> {

                    EnrollmentSummeryDTO dto =
                            new EnrollmentSummeryDTO();

                    dto.setStudentId(
                            student.getId()
                    );

                    dto.setStudentName(
                            student.getFirstName()
                                    + " "
                                    + student.getLastName()
                    );

                    dto.setEmail(
                            student.getEmail()
                    );

                    List<Enrollment> enrollments =
                            student.getEnrollments();

                    dto.setCourseCount(
                            enrollments.size()
                    );

                    BigDecimal totalFee =
                            enrollments.stream()
                                    .map(enrollment ->
                                            enrollment
                                                    .getCourse()
                                                    .getFee()
                                    )
                                    .filter(fee ->
                                            fee != null)
                                    .reduce(
                                            BigDecimal.ZERO,
                                            BigDecimal::add
                                    );

                    dto.setTotalFee(
                            totalFee
                    );

                    List<CourseDTO> courseList =
                            enrollments.stream()
                                    .map(
                                            Enrollment::getCourse
                                    )
                                    .map(course ->
                                            mapper.map(
                                                    course,
                                                    CourseDTO.class
                                            )
                                    )
                                    .collect(
                                            Collectors.toList()
                                    );

                    dto.setCourseList(
                            courseList
                    );

                    return dto;

                })
                .orElseThrow(() ->
                        new RuntimeException(
                                "Enrolled Student Not Found"
                        ));
    }

    @Override
    @Transactional
    public List<EnrollmentSummeryDTO> getRecentlyEnrolledStudents(
            int page,
            int size) {

        log.info(
                "List of Recently Enrolled Students"
        );

        PageRequest pageRequest =
                PageRequest.of(
                        page,
                        size
                );

        Page<Long> idPage =
                studentRepository
                        .findRecentlyEnrolledStudentIds(
                                pageRequest
                        );

        List<Long> ids =
                idPage.getContent();

        if (ids.isEmpty()) {
            return List.of();
        }

        List<Students> students =
                studentRepository
                        .findStudentsWithCoursesByIds(
                                ids
                        );

        Map<Long, Students> studentsById =
                students.stream()
                        .collect(
                                Collectors.toMap(
                                        Students::getId,
                                        student -> student
                                )
                        );

        return ids.stream()
                .map(studentsById::get)
                .filter(student ->
                        student != null)
                .map(student -> {

                    EnrollmentSummeryDTO dto =
                            new EnrollmentSummeryDTO();

                    dto.setStudentId(
                            student.getId()
                    );

                    dto.setStudentName(
                            student.getFirstName()
                                    + " "
                                    + student.getLastName()
                    );

                    dto.setEmail(
                            student.getEmail()
                    );

                    List<Enrollment> enrollments =
                            student.getEnrollments();

                    dto.setCourseCount(
                            enrollments.size()
                    );

                    BigDecimal totalFee =
                            enrollments.stream()
                                    .map(enrollment ->
                                            enrollment
                                                    .getCourse()
                                                    .getFee()
                                    )
                                    .filter(fee ->
                                            fee != null)
                                    .reduce(
                                            BigDecimal.ZERO,
                                            BigDecimal::add
                                    );

                    dto.setTotalFee(
                            totalFee
                    );

                    List<CourseDTO> courseList =
                            enrollments.stream()
                                    .map(
                                            Enrollment::getCourse
                                    )
                                    .map(course ->
                                            mapper.map(
                                                    course,
                                                    CourseDTO.class
                                            )
                                    )
                                    .collect(
                                            Collectors.toList()
                                    );

                    dto.setCourseList(
                            courseList
                    );

                    return dto;

                })
                .collect(
                        Collectors.toList()
                );
    }

    @Override
    @Transactional(readOnly = true)
    public Enrollment findEnrollmentById(
            Long id) {

        return enrollmentRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Enrollment not found with id: "
                                        + id
                        ));
    }

    @Override
    @Transactional
    public void updateEnrollmentFee(
            Long id,
            BigDecimal newFee) {

        Enrollment enrollment =
                findEnrollmentById(id);

        if (newFee != null) {

            enrollment.setFee(
                    newFee
            );
        }

        enrollmentRepository.save(
                enrollment
        );
    }

    @Override
    @Transactional
    public void deleteEnrollment(
            Long id) {

        if (!enrollmentRepository.existsById(id)) {

            throw new RuntimeException(
                    "Enrollment not found with id: "
                            + id
            );
        }

        enrollmentRepository.deleteById(
                id
        );
    }

    @Override
    @Transactional
    public void deleteAllEnrollmentsForStudent(
            Long studentId) {

        log.info(
                "Deleting all enrollments for student {}",
                studentId
        );

        if (!studentRepository.existsById(studentId)) {

            throw new RuntimeException(
                    "Student not found with id: "
                            + studentId
            );
        }

        enrollmentRepository.deleteAllByStudentId(
                studentId
        );

        log.info(
                "Deleted all enrollments for student {}",
                studentId
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> findEnrolledCourseIdsByStudent(
            Long studentId) {

        if (!studentRepository.existsById(studentId)) {

            throw new RuntimeException(
                    "Student not found with id: "
                            + studentId
            );
        }

        return enrollmentRepository
                .findCourseIdsByStudentId(
                        studentId
                );
    }

    @Override
    @Transactional
    public void updateStudentCourses(
            Long studentId,
            List<Long> courseIds) {

        log.info(
                "Updating courses for student {}",
                studentId
        );

        Students student =
                studentRepository.findById(
                        studentId
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Student Not Found"
                        ));

        List<Long> selectedCourseIds =
                courseIds == null
                        ? List.of()
                        : courseIds.stream()
                                .distinct()
                                .toList();

        if (selectedCourseIds.size() >
                MAX_COURSES_PER_STUDENT) {

            throw new RuntimeException(
                    "Only can enroll in "
                            + MAX_COURSES_PER_STUDENT
                            + " courses"
            );
        }

        List<Enrollment> existingEnrollments =
                enrollmentRepository
                        .findByStudentId(
                                studentId
                        );

        List<Long> existingCourseIds =
                existingEnrollments.stream()
                        .map(enrollment ->
                                enrollment
                                        .getCourse()
                                        .getId()
                        )
                        .toList();

        for (Long existingCourseId :
                existingCourseIds) {

            if (!selectedCourseIds.contains(
                    existingCourseId
            )) {

                enrollmentRepository
                        .deleteByStudentIdAndCourseId(
                                studentId,
                                existingCourseId
                        );

                log.info(
                        "Removed course {} from student {}",
                        existingCourseId,
                        studentId
                );
            }
        }

        for (Long courseId :
                selectedCourseIds) {

            if (existingCourseIds.contains(
                    courseId
            )) {

                continue;
            }

            Courses course =
                    courseRepository.findById(
                            courseId
                    ).orElseThrow(() ->
                            new RuntimeException(
                                    "Course Not Found"
                            ));

            Enrollment enrollment =
                    new Enrollment();

            enrollment.setStudent(
                    student
            );

            enrollment.setCourse(
                    course
            );

            enrollment.setEnrolledDate(
                    LocalDateTime.now()
            );

            enrollmentRepository.save(
                    enrollment
            );

            log.info(
                    "Added course {} to student {}",
                    courseId,
                    studentId
            );
        }

        log.info(
                "Courses updated successfully for student {}",
                studentId
        );
    }
}