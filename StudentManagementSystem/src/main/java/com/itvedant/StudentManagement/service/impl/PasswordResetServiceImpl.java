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

    // =========================================================
    // Helper: resolve a user by email OR username (case-insensitive)
    // =========================================================
    private Optional<Users> findUserByIdentifier(String identifier) {

        if (identifier == null || identifier.isBlank()) {
            System.out.println(">>> findUserByIdentifier: identifier blank");
            return Optional.empty();
        }

        String value = identifier.trim().toLowerCase();
        System.out.println(">>> findUserByIdentifier looking for: " + value);

        Optional<Users> byEmail = userRepository.findFirstByEmailIgnoreCase(value);
        if (byEmail.isPresent()) {
            System.out.println(">>> Found by EMAIL: " + byEmail.get().getUserName());
            return byEmail;
        }

        Optional<Users> byUser = userRepository.findFirstByUserNameIgnoreCase(value);
        if (byUser.isPresent()) {
            System.out.println(">>> Found by USERNAME: " + byUser.get().getUserName());
        } else {
            System.out.println(">>> NOT FOUND by email or username");
        }
        return byUser;
    }

    // =========================================================
    // Create reset token
    // =========================================================
    @Override
    @Transactional
    public String createResetToken(String email) {

        System.out.println(">>> createResetToken called with: " + email);

        Optional<Users> userOpt = findUserByIdentifier(email);

        if (userOpt.isEmpty()) {
            System.out.println(">>> createResetToken: user NOT found → returning null");
            return null;
        }

        Users user = userOpt.get();

        String targetIdentifier =
                (user.getUserName() != null && !user.getUserName().isBlank())
                        ? user.getUserName()
                        : user.getEmail();

        System.out.println(">>> Storing identifier with token: " + targetIdentifier);

        if (targetIdentifier == null || targetIdentifier.isBlank()) {
            System.out.println(">>> createResetToken: targetIdentifier blank → returning null");
            return null;
        }

        tokenRepository.deleteByUserEmail(targetIdentifier);
        System.out.println(">>> Deleted old tokens for: " + targetIdentifier);

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUserEmail(targetIdentifier);
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(EXPIRY_MINUTES));
        resetToken.setUsed(false);

        tokenRepository.save(resetToken);
        System.out.println(">>> Token saved: " + token);

        return token;
    }

    // =========================================================
    // Validate token
    // =========================================================
    @Override
    @Transactional(readOnly = true)
    public String validateToken(String token) {

        System.out.println(">>> validateToken called with: " + token);

        if (token == null || token.isBlank()) {
            System.out.println(">>> validateToken: token blank");
            return null;
        }

        Optional<PasswordResetToken> opt = tokenRepository.findByToken(token);

        if (opt.isEmpty()) {
            System.out.println(">>> validateToken: token NOT FOUND in DB");
            return null;
        }

        PasswordResetToken resetToken = opt.get();
        System.out.println(">>> validateToken: found token row; used=" + resetToken.isUsed()
                + " expiresAt=" + resetToken.getExpiresAt()
                + " identifier=" + resetToken.getUserEmail());

        if (resetToken.isUsed()) {
            System.out.println(">>> validateToken: token ALREADY USED");
            return null;
        }

        if (resetToken.getExpiresAt() == null
                || resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            System.out.println(">>> validateToken: token EXPIRED");
            return null;
        }

        System.out.println(">>> validateToken: OK, returning " + resetToken.getUserEmail());
        return resetToken.getUserEmail();
    }

    // =========================================================
    // Reset password
    // =========================================================
    @Override
    @Transactional
    public boolean resetPassword(String token, String newPassword) {

        System.out.println(">>> resetPassword called");
        System.out.println(">>> token = " + token);
        System.out.println(">>> newPassword length = "
                + (newPassword == null ? "null" : newPassword.length()));

        if (newPassword == null || newPassword.isBlank()) {
            System.out.println(">>> FAIL: newPassword blank");
            return false;
        }

        String identifier = validateToken(token);
        System.out.println(">>> validateToken returned: " + identifier);

        if (identifier == null) {
            System.out.println(">>> FAIL: token invalid/expired/used");
            return false;
        }

        Optional<Users> userOpt = findUserByIdentifier(identifier);
        System.out.println(">>> findUserByIdentifier present: " + userOpt.isPresent());

        if (userOpt.isEmpty()) {
            System.out.println(">>> FAIL: user not found for identifier=" + identifier);
            return false;
        }

        Users user = userOpt.get();
        System.out.println(">>> User id=" + user.getId()
                + " userName=" + user.getUserName()
                + " active=" + user.isActive()
                + " role=" + user.getRole());

        String newHash = passwordEncoder.encode(newPassword);
        System.out.println(">>> New hash: " + newHash);
        System.out.println(">>> New hash length: " + newHash.length());

        user.setPassword(newHash);

        user.setActive(true);

        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("ADMIN");
            System.out.println(">>> Set default role=ADMIN");
        }

        userRepository.save(user);
        System.out.println(">>> User SAVED ✅");

        PasswordResetToken resetToken = tokenRepository
                .findByToken(token)
                .orElse(null);

        if (resetToken != null) {
            resetToken.setUsed(true);
            tokenRepository.save(resetToken);
            System.out.println(">>> Token marked as USED");
        } else {
            System.out.println(">>> WARN: token row disappeared before marking used");
        }

        System.out.println(">>> resetPassword returning TRUE");
        return true;
    }
}