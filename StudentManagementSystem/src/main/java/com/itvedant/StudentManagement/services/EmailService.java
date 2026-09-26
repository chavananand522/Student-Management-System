package com.itvedant.StudentManagement.services;

public interface EmailService {
	
	void sendPasswordResetEmail(String to, String resetUrl);
}