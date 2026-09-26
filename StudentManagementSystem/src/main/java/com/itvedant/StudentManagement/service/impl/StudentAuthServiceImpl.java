package com.itvedant.StudentManagement.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itvedant.StudentManagement.dto.StudentSignupDTO;
import com.itvedant.StudentManagement.model.Students;
import com.itvedant.StudentManagement.model.Users;
import com.itvedant.StudentManagement.reposatory.StudentRepositiry;
import com.itvedant.StudentManagement.reposatory.UserRepository;
import com.itvedant.StudentManagement.services.StudentAuthService;

@Service
@Transactional
public class StudentAuthServiceImpl implements StudentAuthService {

    private final StudentRepositiry studentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentAuthServiceImpl(
            StudentRepositiry studentRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void register(StudentSignupDTO dto) {

        String email = dto.getEmail().trim().toLowerCase();

        if (studentRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        if (userRepository.existsByUserNameIgnoreCase(email)) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        // ---- Create Students row ----
        Students student = new Students();
        student.setFirstName(dto.getFirstName().trim());
        student.setLastName(dto.getLastName().trim());
        student.setEmail(email);
        student.setPhoneNumber(dto.getPhoneNumber());
        student.setAddress(dto.getAddress());
        student.setActive(true);

        Students savedStudent = studentRepository.save(student);

        // ---- Create Users row (for login) ----
        Users user = new Users();
        user.setUserName(savedStudent.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setActive(true);          // ✅ enabled
        user.setRole("STUDENT");       // ✅ role
        user.setFullName(
                savedStudent.getFirstName()
                + " "
                + savedStudent.getLastName()
        );
        user.setEmail(savedStudent.getEmail());
        user.setPhoneNumber(savedStudent.getPhoneNumber());

        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {

        if (email == null || email.isBlank()) {
            return false;
        }

        String trimmedEmail = email.trim().toLowerCase();

        return studentRepository.existsByEmailIgnoreCase(trimmedEmail)
                || userRepository.existsByUserNameIgnoreCase(trimmedEmail);
    }
}