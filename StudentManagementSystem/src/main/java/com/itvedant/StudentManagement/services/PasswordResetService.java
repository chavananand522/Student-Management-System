package com.itvedant.StudentManagement.services;

public interface PasswordResetService {

    /**
     * Generate a reset token for the given email or username.
     * Returns the token string if the user exists, else null.
     */
    String createResetToken(String email);

    /**
     * Validate a token.
     * Returns the identifier (username/email) if valid, null otherwise.
     */
    String validateToken(String token);

    /**
     * Reset the user's password using a valid token.
     * Returns true on success, false otherwise.
     */
    boolean resetPassword(String token, String newPassword);
}