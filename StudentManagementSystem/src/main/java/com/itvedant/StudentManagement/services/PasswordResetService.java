package com.itvedant.StudentManagement.services;

public interface PasswordResetService {

    /**
     * Generate a reset token for the given email.
     * Returns the token string if the email exists, else null.
     */
    String createResetToken(String email);

    /**
     * Validate a token.
     * Returns the email if valid, null otherwise.
     */
    String validateToken(String token);

    /**
     * Reset the user's password using a valid token.
     * Returns true on success, false otherwise.
     */
    boolean resetPassword(String token, String newPassword);
}