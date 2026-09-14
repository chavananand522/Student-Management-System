package com.itvedant.StudentManagement.config;

import java.util.List;

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
                    "Study of matter, energy, motion, and natural laws."
            );

            createSubject(
                    studyRepository,
                    "Chemistry",
                    "Study of substances, reactions, and their properties."
            );

            createSubject(
                    studyRepository,
                    "Biology",
                    "Study of living organisms and their processes."
            );


            // =====================================================
            // PHYSICS CHAPTERS
            // =====================================================

            createChapter(
                    chapterRepository,
                    1,
                    "Physics",
                    "Mechanics",
                    "Study of motion, forces, energy, and the behavior of objects."
            );

            createChapter(
                    chapterRepository,
                    2,
                    "Physics",
                    "Heat & Thermodynamics",
                    "Study of heat, temperature, and thermal energy."
            );

            createChapter(
                    chapterRepository,
                    3,
                    "Physics",
                    "Waves & Oscillations",
                    "Study of waves, vibrations, and oscillatory motion."
            );

            createChapter(
                    chapterRepository,
                    4,
                    "Physics",
                    "Electrodynamics",
                    "Study of electric charges, currents, and magnetic fields."
            );

            createChapter(
                    chapterRepository,
                    5,
                    "Physics",
                    "Optics",
                    "Study of light, reflection, refraction, and optical systems."
            );

            createChapter(
                    chapterRepository,
                    6,
                    "Physics",
                    "Modern Physics",
                    "Study of atoms, nuclei, quantum effects, and modern concepts."
            );


            // =====================================================
            // CHEMISTRY CHAPTERS
            // =====================================================

            createChapter(
                    chapterRepository,
                    1,
                    "Chemistry",
                    "Physical Chemistry",
                    "Study of chemical principles, calculations, and physical properties."
            );

            createChapter(
                    chapterRepository,
                    2,
                    "Chemistry",
                    "Inorganic Chemistry",
                    "Study of elements, compounds, and their properties."
            );

            createChapter(
                    chapterRepository,
                    3,
                    "Chemistry",
                    "Organic Chemistry",
                    "Study of carbon compounds, their structures, and reactions."
            );


            // =====================================================
            // BIOLOGY CHAPTERS
            // =====================================================

            createChapter(
                    chapterRepository,
                    1,
                    "Biology",
                    "Botany",
                    "Study of plants, their structure, growth, and functions."
            );

            createChapter(
                    chapterRepository,
                    2,
                    "Biology",
                    "Zoology",
                    "Study of animals, their structure, behavior, and functions."
            );

            createChapter(
                    chapterRepository,
                    3,
                    "Biology",
                    "Cell Biology",
                    "Study of cells, their structure, and cellular functions."
            );

            createChapter(
                    chapterRepository,
                    4,
                    "Biology",
                    "Genetics & Evolution",
                    "Study of heredity, genes, variation, and evolution."
            );

            createChapter(
                    chapterRepository,
                    5,
                    "Biology",
                    "Ecology & Environment",
                    "Study of organisms, ecosystems, and their environment."
            );

            createChapter(
                    chapterRepository,
                    6,
                    "Biology",
                    "Biotechnology & Applied Biology",
                    "Study of biotechnology and its applications in biology."
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

        List<Chapter> existingChapters =
                chapterRepository
                        .findBySubjectIgnoreCaseAndNameIgnoreCase(
                                subject,
                                name
                        );

        if (existingChapters.isEmpty()) {

            Chapter chapter = new Chapter();

            chapter.setChapterNumber(chapterNumber);

            chapter.setSubject(subject);

            chapter.setName(name);

            chapter.setDescription(description);

            chapterRepository.save(chapter);
        }
    }
}