package com.itvedant.StudentManagement.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.itvedant.StudentManagement.model.Chapter;
import com.itvedant.StudentManagement.model.Study;
import com.itvedant.StudentManagement.model.Users;
import com.itvedant.StudentManagement.reposatory.ChapterRepository;
import com.itvedant.StudentManagement.reposatory.StudyRepository;
import com.itvedant.StudentManagement.reposatory.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner loadSampleData(
            UserRepository usersRepository,
            PasswordEncoder passwordEncoder,
            StudyRepository studyRepository,
            ChapterRepository chapterRepository) {

        return args -> {

            // =====================================================
            // ADMIN USER
            // =====================================================

            if (!usersRepository.existsByUserName("Admin")) {

                Users users = new Users();

                users.setUserName("Admin");

                users.setPassword(
                        passwordEncoder.encode("admin@123")
                );

                users.setActive(true);

                usersRepository.save(users);
            }


            // =====================================================
            // SUBJECTS
            // =====================================================

            createSubject(
                    studyRepository,
                    "Physics",
                    "Physics concepts and chapters"
            );

            createSubject(
                    studyRepository,
                    "Chemistry",
                    "Chemistry concepts and chapters"
            );

            createSubject(
                    studyRepository,
                    "Biology",
                    "Biology concepts and chapters"
            );


            // =====================================================
            // PHYSICS CHAPTERS
            // =====================================================

            createChapter(
                    chapterRepository,
                    1,
                    "Physics",
                    "Mechanics",
                    "Motion, force, work, energy and related concepts"
            );

            createChapter(
                    chapterRepository,
                    2,
                    "Physics",
                    "Heat & Thermodynamics",
                    "Heat, temperature, thermodynamics and thermal properties"
            );

            createChapter(
                    chapterRepository,
                    3,
                    "Physics",
                    "Waves & Oscillations",
                    "Oscillations, waves and wave motion"
            );

            createChapter(
                    chapterRepository,
                    4,
                    "Physics",
                    "Electrodynamics",
                    "Electricity, current, magnetic fields and electromagnetic concepts"
            );

            createChapter(
                    chapterRepository,
                    5,
                    "Physics",
                    "Optics",
                    "Ray optics, wave optics and optical instruments"
            );

            createChapter(
                    chapterRepository,
                    6,
                    "Physics",
                    "Modern Physics",
                    "Atoms, nuclei, quantum physics and modern physics"
            );


            // =====================================================
            // CHEMISTRY CHAPTERS
            // =====================================================

            createChapter(
                    chapterRepository,
                    1,
                    "Chemistry",
                    "Physical Chemistry",
                    "Chemical calculations, thermodynamics, equilibrium and kinetics"
            );

            createChapter(
                    chapterRepository,
                    2,
                    "Chemistry",
                    "Inorganic Chemistry",
                    "Periodic table, chemical elements and inorganic compounds"
            );

            createChapter(
                    chapterRepository,
                    3,
                    "Chemistry",
                    "Organic Chemistry",
                    "Organic compounds, reactions and mechanisms"
            );


            // =====================================================
            // BIOLOGY CHAPTERS
            // =====================================================

            createChapter(
                    chapterRepository,
                    1,
                    "Biology",
                    "Botany",
                    "Study of plants and plant biology"
            );

            createChapter(
                    chapterRepository,
                    2,
                    "Biology",
                    "Zoology",
                    "Study of animals and animal biology"
            );

            createChapter(
                    chapterRepository,
                    3,
                    "Biology",
                    "Cell Biology",
                    "Cell structure, functions and cellular processes"
            );

            createChapter(
                    chapterRepository,
                    4,
                    "Biology",
                    "Genetics & Evolution",
                    "Genes, heredity, variation and evolution"
            );

            createChapter(
                    chapterRepository,
                    5,
                    "Biology",
                    "Ecology & Environment",
                    "Ecosystems, environment and biodiversity"
            );

            createChapter(
                    chapterRepository,
                    6,
                    "Biology",
                    "Biotechnology & Applied Biology",
                    "Biotechnology and applications of biological sciences"
            );
        };
    }


    // =========================================================
    // CREATE SUBJECT
    // =========================================================

    private void createSubject(
            StudyRepository studyRepository,
            String subject,
            String description) {

        if (!studyRepository
                .findBySubjectIgnoreCase(subject)
                .isPresent()) {

            Study study = new Study();

            study.setSubject(subject);

            study.setDescription(description);

            studyRepository.save(study);
        }
    }


    // =========================================================
    // CREATE CHAPTER
    // =========================================================

    private void createChapter(
            ChapterRepository chapterRepository,
            Integer chapterNumber,
            String subject,
            String name,
            String description) {

        if (!chapterRepository
                .findBySubjectIgnoreCaseAndNameIgnoreCase(
                        subject,
                        name
                )
                .isPresent()) {

            Chapter chapter = new Chapter();

            chapter.setChapterNumber(chapterNumber);

            chapter.setSubject(subject);

            chapter.setName(name);

            chapter.setDescription(description);

            chapterRepository.save(chapter);
        }
    }
}