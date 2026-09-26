package com.itvedant.StudentManagement.services;

import com.itvedant.StudentManagement.dto.StudentSignupDTO;

public interface StudentAuthService {

    void register(StudentSignupDTO dto);

    boolean emailExists(String email);
}