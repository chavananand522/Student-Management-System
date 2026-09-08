package com.itvedant.StudentManagement.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.itvedant.StudentManagement.model.Users;
import com.itvedant.StudentManagement.reposatory.UserRepository;

@Configuration
public class DataInitializer {

	@Bean
	CommandLineRunner loadSampleData(UserRepository usersRepository,PasswordEncoder passwordEncoder) {
		
		return args -> {
			if (!usersRepository.existsByUserName("Admin")) {
				Users users = new Users();
				users.setUserName("Admin");
				users.setPassword(passwordEncoder.encode("admin@123"));
				users.setActive(true);
				usersRepository.save(users);
			}
		};

	}
}
