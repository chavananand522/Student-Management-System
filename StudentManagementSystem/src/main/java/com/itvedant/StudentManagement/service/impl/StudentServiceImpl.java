// ============================================================
// FILE:
// src/main/java/com/itvedant/StudentManagement/service/impl/StudentServiceImpl.java
// ============================================================

package com.itvedant.StudentManagement.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itvedant.StudentManagement.dto.StudentDTO;
import com.itvedant.StudentManagement.model.Students;
import com.itvedant.StudentManagement.model.Users;
import com.itvedant.StudentManagement.reposatory.StudentRepositiry;
import com.itvedant.StudentManagement.reposatory.UserRepository;
import com.itvedant.StudentManagement.services.StudentService;

@Service
@Transactional
public class StudentServiceImpl
        implements StudentService {

    private static final Logger log =
            LoggerFactory.getLogger(
                    StudentServiceImpl.class);

    private final StudentRepositiry studentRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public StudentServiceImpl(
            StudentRepositiry studentRepository,
            UserRepository userRepository,
            ModelMapper mapper,
            PasswordEncoder passwordEncoder) {

        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean existsByEmailIgnoreCase(
            String email) {

        return studentRepository
                .existsByEmailIgnoreCase(email);
    }

    @Override
    public StudentDTO createStudent(
            StudentDTO studentDTO) {

        log.info(
                "Creating student: {}",
                studentDTO.getEmail());

        Students student =
                mapper.map(
                        studentDTO,
                        Students.class);

        student.setId(0);

        Students saved =
                studentRepository.save(student);

        /*
         * Automatically create the student's
         * login account.
         *
         * Username = email
         * Password = student@123
         * Role     = STUDENT
         */
        String email = saved.getEmail();

        if (email != null &&
            !email.isBlank()) {

            Users user =
                    userRepository
                        .findByUserName(email)
                        .orElse(null);

            if (user == null) {

                user = new Users();

                user.setUserName(email);

                user.setPassword(
                        passwordEncoder.encode(
                                "student@123"));
            }

            user.setActive(saved.isActive());

            user.setRole("STUDENT");

            user.setFullName(
                    saved.getFirstName()
                    + " "
                    + saved.getLastName());

            user.setEmail(saved.getEmail());

            user.setPhoneNumber(
                    saved.getPhoneNumber());

            userRepository.save(user);
        }

        return mapper.map(
                saved,
                StudentDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDTO> getAllStudents() {

        return studentRepository
                .findByActiveTrue()
                .stream()
                .map(student ->
                        mapper.map(
                                student,
                                StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentDTO> getStudents(
            int page,
            int size) {

        PageRequest pageRequest =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                Direction.DESC,
                                "id"));

        return studentRepository
                .findByActiveTrue(pageRequest)
                .map(student ->
                        mapper.map(
                                student,
                                StudentDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDTO getStudentById(
            Long id) {

        Students student =
                studentRepository
                    .findById(id)
                    .orElseThrow(() ->
                        new RuntimeException(
                            "No Student Found"));

        return mapper.map(
                student,
                StudentDTO.class);
    }

    @Override
    public boolean existsByEmailIgnoreCaseAndIdNot(
            String email,
            Long id) {

        return studentRepository
                .existsByEmailIgnoreCaseAndIdNot(
                        email,
                        id);
    }

    @Override
    public StudentDTO updateStudent(
            Long id,
            StudentDTO studentDTO) {

        Students student =
                studentRepository
                    .findById(id)
                    .orElseThrow(() ->
                        new RuntimeException(
                            "No Student Found"));

        mapper.map(
                studentDTO,
                student);

        Students updated =
                studentRepository.save(student);

        /*
         * Keep student login account synchronized.
         */
        if (updated.getEmail() != null &&
            !updated.getEmail().isBlank()) {

            Users user =
                    userRepository
                        .findByUserName(
                            updated.getEmail())
                        .orElse(null);

            if (user == null) {

                user = new Users();

                user.setUserName(
                        updated.getEmail());

                user.setPassword(
                        passwordEncoder.encode(
                                "student@123"));
            }

            user.setRole("STUDENT");
            user.setActive(
                    updated.isActive());

            user.setFullName(
                    updated.getFirstName()
                    + " "
                    + updated.getLastName());

            user.setEmail(
                    updated.getEmail());

            user.setPhoneNumber(
                    updated.getPhoneNumber());

            userRepository.save(user);
        }

        return mapper.map(
                updated,
                StudentDTO.class);
    }
}