package com.itvedant.StudentManagement.service.impl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.itvedant.StudentManagement.services.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendPasswordResetEmail(String to, String resetUrl) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("your-gmail@gmail.com");
            message.setTo(to);
            message.setSubject("Password Reset — Student Management System");
            message.setText(
                "Hello,\n\n" +
                "You requested to reset your password.\n\n" +
                "Click the link below to set a new password:\n" +
                resetUrl + "\n\n" +
                "The link expires in 30 minutes.\n\n" +
                "If you did not request this, ignore this email.\n\n" +
                "— Student Management System"
            );

            mailSender.send(message);

            System.out.println("Reset email sent to: " + to);

        } catch (Exception e) {
            System.err.println("Failed to send email to " + to);
            e.printStackTrace();
        }
    }
}