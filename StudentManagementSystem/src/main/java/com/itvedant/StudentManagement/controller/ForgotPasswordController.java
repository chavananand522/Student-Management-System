package com.itvedant.StudentManagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itvedant.StudentManagement.services.EmailService;
import com.itvedant.StudentManagement.services.PasswordResetService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ForgotPasswordController {

    private final PasswordResetService passwordResetService;
    private final EmailService emailService;

    public ForgotPasswordController(
            PasswordResetService passwordResetService,
            EmailService emailService) {
        this.passwordResetService = passwordResetService;
        this.emailService = emailService;
    }

    // =========================================================
    // SHOW: /forgot-password
    // =========================================================
    @GetMapping("/forgot-password")
    public String showForgotForm() {
        return "forgot-password";
    }

    // =========================================================
    // POST: /forgot-password
    // =========================================================
    @PostMapping("/forgot-password")
    public String handleForgotPassword(
            @RequestParam String email,
            HttpServletRequest request,
            Model model) {

        String token = passwordResetService.createResetToken(email);

        // Always show the same message — don't reveal whether the
        // email exists (security best practice).
        model.addAttribute("message",
            "If your email is registered, a reset link has been sent.");

        if (token == null) {
            return "forgot-password";
        }

        // Build the reset URL
        String baseUrl = request.getScheme() + "://"
                + request.getServerName() + ":"
                + request.getServerPort();

        String resetUrl = baseUrl + "/reset-password?token=" + token;

        // Send the email
        try {
            emailService.sendPasswordResetEmail(email, resetUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Optionally print to console (dev)
        System.out.println("Reset URL: " + resetUrl);

        return "forgot-password";
    }

    // =========================================================
    // SHOW: /reset-password?token=...
    // =========================================================
    @GetMapping("/reset-password")
    public String showResetForm(
            @RequestParam(required = false) String token,
            Model model) {

        String email = passwordResetService.validateToken(token);

        if (email == null) {
            model.addAttribute("error",
                "Invalid or expired reset link. Please request a new one.");
            return "reset-password-error";
        }

        model.addAttribute("token", token);
        return "reset-password";
    }

    // =========================================================
    // POST: /reset-password
    // =========================================================
    @PostMapping("/reset-password")
    public String handleReset(
            @RequestParam String token,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("token", token);
            model.addAttribute("error", "Passwords do not match.");
            return "reset-password";
        }

        if (newPassword.length() < 6) {
            model.addAttribute("token", token);
            model.addAttribute("error",
                "Password must be at least 6 characters.");
            return "reset-password";
        }

        boolean success = passwordResetService.resetPassword(token, newPassword);

        if (!success) {
            model.addAttribute("error",
                "Reset link is invalid or has expired.");
            return "reset-password-error";
        }

        redirectAttributes.addFlashAttribute("message",
            "Password updated successfully. Please log in.");

        return "redirect:/login";
    }
}