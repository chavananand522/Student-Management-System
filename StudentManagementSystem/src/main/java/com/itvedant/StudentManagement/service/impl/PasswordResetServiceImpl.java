package com.itvedant.StudentManagement.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itvedant.StudentManagement.model.PasswordResetToken;
import com.itvedant.StudentManagement.model.Users;
import com.itvedant.StudentManagement.reposatory.PasswordResetTokenRepository;
import com.itvedant.StudentManagement.reposatory.UserRepository;
import com.itvedant.StudentManagement.services.PasswordResetService;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    private static final int EXPIRY_MINUTES = 30;

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetServiceImpl(
            UserRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public String createResetToken(String email) {

        if (email == null || email.isBlank()) {
            return null;
        }

        String normalized = email.trim().toLowerCase();

        // ---- findFirstByEmail handles duplicates safely ----
        Optional<Users> userOpt = userRepository.findFirstByEmail(normalized);

        // Fall back to username lookup (students use email as username)
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findFirstByUserNameIgnoreCase(normalized);
        }

        if (userOpt.isEmpty()) {
            return null;
        }

        Users user = userOpt.get();

        // Invalidate old tokens for this user
        tokenRepository.deleteByUserEmail(user.getEmail());

        // Generate a new token
        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUserEmail(user.getEmail());
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(EXPIRY_MINUTES));
        resetToken.setUsed(false);

        tokenRepository.save(resetToken);

        return token;
    }

    @Override
    @Transactional(readOnly = true)
    public String validateToken(String token) {

        if (token == null || token.isBlank()) {
            return null;
        }

        Optional<PasswordResetToken> opt = tokenRepository.findByToken(token);

        if (opt.isEmpty()) {
            return null;
        }

        PasswordResetToken resetToken = opt.get();

        if (resetToken.isUsed()) {
            return null;
        }

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            return null;
        }

        return resetToken.getUserEmail();
    }

    @Override
    @Transactional
    public boolean resetPassword(String token, String newPassword) {

        String email = validateToken(token);

        if (email == null) {
            return false;
        }

        // ---- findFirstByEmail handles duplicates safely ----
        Optional<Users> userOpt = userRepository.findFirstByEmail(email);

        if (userOpt.isEmpty()) {
            userOpt = userRepository.findFirstByUserNameIgnoreCase(email);
        }

        if (userOpt.isEmpty()) {
            return false;
        }

        Users user = userOpt.get();

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setActive(true);

        userRepository.save(user);

        // Mark token as used
        PasswordResetToken resetToken = tokenRepository.findByToken(token).orElse(null);

        if (resetToken != null) {
            resetToken.setUsed(true);
            tokenRepository.save(resetToken);
        }

        return true;
    }
}