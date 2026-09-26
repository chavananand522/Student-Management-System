package com.itvedant.StudentManagement.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.itvedant.StudentManagement.model.Module;
import com.itvedant.StudentManagement.model.Users;
import com.itvedant.StudentManagement.reposatory.ModuleRepository;
import com.itvedant.StudentManagement.reposatory.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            UserRepository usersRepository,
            ModuleRepository moduleRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            // =========================================================
            // ADMIN ACCOUNT
            // =========================================================

            if (usersRepository.findByUserName("Admin").isEmpty()) {

                Users admin = new Users();

                admin.setUserName("Admin");
                admin.setPassword(
                        passwordEncoder.encode("admin@123")
                );

                admin.setActive(true);
                admin.setRole("ADMIN");

                admin.setFullName("Administrator");
                admin.setEmail("admin@gmail.com");
                admin.setPhoneNumber("9999999999");

                usersRepository.save(admin);
            }


            // =========================================================
            // IMPORTANT
            // =========================================================
            //
            // DO NOT CREATE STUDENT USERS HERE.
            //
            // Students can login ONLY after they use the
            // student signup page.
            //
            // Therefore we intentionally do NOT have:
            //
            // studentRepository.findAll()
            //
            // and we do NOT create:
            //
            // student@123
            //
            // accounts here.
            //
            // =========================================================


            // =========================================================
            // CHEMISTRY MODULES
            // =========================================================

            createModuleIfNotExists(
                    moduleRepository,
                    "Chemistry",
                    "Physical Chemistry",
                    1
            );

            createModuleIfNotExists(
                    moduleRepository,
                    "Chemistry",
                    "Inorganic Chemistry",
                    2
            );

            createModuleIfNotExists(
                    moduleRepository,
                    "Chemistry",
                    "Organic Chemistry",
                    3
            );


            // =========================================================
            // BIOLOGY MODULES
            // =========================================================

            createModuleIfNotExists(
                    moduleRepository,
                    "Biology",
                    "Botany",
                    1
            );

            createModuleIfNotExists(
                    moduleRepository,
                    "Biology",
                    "Zoology",
                    2
            );

            createModuleIfNotExists(
                    moduleRepository,
                    "Biology",
                    "Cell Biology",
                    3
            );

            createModuleIfNotExists(
                    moduleRepository,
                    "Biology",
                    "Genetics & Evolution",
                    4
            );

            createModuleIfNotExists(
                    moduleRepository,
                    "Biology",
                    "Ecology & Environment",
                    5
            );

            createModuleIfNotExists(
                    moduleRepository,
                    "Biology",
                    "Biotechnology & Applied Biology",
                    6
            );


            // =========================================================
            // PHYSICS MODULES
            // =========================================================

            createModuleIfNotExists(
                    moduleRepository,
                    "Physics",
                    "Mechanics",
                    1
            );

            createModuleIfNotExists(
                    moduleRepository,
                    "Physics",
                    "Thermodynamics",
                    2
            );

            createModuleIfNotExists(
                    moduleRepository,
                    "Physics",
                    "Electromagnetism",
                    3
            );

            createModuleIfNotExists(
                    moduleRepository,
                    "Physics",
                    "Optics",
                    4
            );

            createModuleIfNotExists(
                    moduleRepository,
                    "Physics",
                    "Modern Physics",
                    5
            );
        };
    }


    // =============================================================
    // CREATE MODULE IF NOT EXISTS
    // =============================================================

    private void createModuleIfNotExists(
            ModuleRepository moduleRepository,
            String subject,
            String name,
            int moduleNumber) {

        if (moduleRepository
                .findBySubjectIgnoreCaseAndNameIgnoreCase(
                        subject,
                        name
                )
                .isEmpty()) {

            Module module = new Module();

            module.setSubject(subject);
            module.setName(name);
            module.setModuleNumber(moduleNumber);

            moduleRepository.save(module);
        }
    }
}