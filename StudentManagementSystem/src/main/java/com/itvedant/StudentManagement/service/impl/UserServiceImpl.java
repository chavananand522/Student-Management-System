package com.itvedant.StudentManagement.service.impl;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.itvedant.StudentManagement.model.Users;
import com.itvedant.StudentManagement.reposatory.UserRepository;

@Service
public class UserServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Users user = userRepository.findByUserName(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Invalid username or password"
                        ));

        // `active` is a primitive boolean on Users, so no null-check needed.
        if (!user.isActive()) {
            throw new DisabledException("User account is disabled");
        }

        if (user.getRole() == null || user.getRole().isBlank()) {
            throw new UsernameNotFoundException(
                    "User role is not configured"
            );
        }

        return User
                .withUsername(user.getUserName())
                .password(user.getPassword())
                .roles(user.getRole().trim().toUpperCase())
                .build();
    }
}