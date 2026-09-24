package com.itvedant.StudentManagement.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.itvedant.StudentManagement.model.Module;
import com.itvedant.StudentManagement.model.Study;
import com.itvedant.StudentManagement.model.Users;
import com.itvedant.StudentManagement.reposatory.ModuleRepository;
import com.itvedant.StudentManagement.reposatory.StudyRepository;
import com.itvedant.StudentManagement.reposatory.UserRepository;

@Configuration
public class DataInitializer {

	@Bean
	CommandLineRunner loadSampleData(UserRepository usersRepository, PasswordEncoder passwordEncoder,
			StudyRepository studyRepository, ModuleRepository moduleRepository) {

		return args -> {

			// =====================================================
			// ADMIN USER
			// =====================================================

			if (!usersRepository.existsByUserName("Admin")) {

				Users users = new Users();

				users.setUserName("Admin");

				users.setPassword(passwordEncoder.encode("admin@123"));

				users.setActive(true);

				usersRepository.save(users);
			}

			// =====================================================
			// SUBJECTS
			// =====================================================

			createSubject(studyRepository, "Physics", "Study of matter, energy, motion, and natural laws.");

			createSubject(studyRepository, "Chemistry", "Study of substances, reactions, and their properties.");

			createSubject(studyRepository, "Biology", "Study of living organisms and their processes.");

			// =====================================================
			// PHYSICS
			// =====================================================

			// =====================================================
			// CHEMISTRY MODULES
			// =====================================================

			createModule(moduleRepository, "Chemistry", 1, "Physical Chemistry",
					"Study of chemical principles, calculations, and physical properties.");

			createModule(moduleRepository, "Chemistry", 2, "Inorganic Chemistry",
					"Study of elements, compounds, and their properties.");

			createModule(moduleRepository, "Chemistry", 3, "Organic Chemistry",
					"Study of carbon compounds, their structures, and reactions.");

			// =====================================================
			// BIOLOGY MODULES
			// =====================================================

			createModule(moduleRepository, "Biology", 1, "Botany",
					"Study of plants, their structure, growth, and functions.");

			createModule(moduleRepository, "Biology", 2, "Zoology",
					"Study of animals, their structure, behavior, and functions.");

			createModule(moduleRepository, "Biology", 3, "Cell Biology",
					"Study of cells, their structure, and cellular functions.");

			createModule(moduleRepository, "Biology", 4, "Genetics & Evolution",
					"Study of heredity, genes, variation, and evolution.");

			createModule(moduleRepository, "Biology", 5, "Ecology & Environment",
					"Study of organisms, ecosystems, and their environment.");

			createModule(moduleRepository, "Biology", 6, "Biotechnology & Applied Biology",
					"Study of biotechnology and its applications in biology.");
		};
	}

	// =========================================================
	// CREATE SUBJECT
	// =========================================================

	private void createSubject(StudyRepository studyRepository, String subject, String description) {

		if (studyRepository.findBySubjectIgnoreCase(subject).isEmpty()) {

			Study study = new Study();

			study.setSubject(subject);
			study.setDescription(description);

			studyRepository.save(study);
		}
	}

	// =========================================================
	// CREATE MODULE
	// =========================================================

	private Module createModule(ModuleRepository moduleRepository, String subject, int moduleNumber, String name,
			String description) {

		return moduleRepository.findBySubjectIgnoreCaseAndNameIgnoreCase(subject, name).orElseGet(() -> {

			Module module = new Module();

			module.setModuleNumber(moduleNumber);
			module.setName(name);
			module.setSubject(subject);
			module.setDescription(description);

			return moduleRepository.save(module);
		});
	}
}