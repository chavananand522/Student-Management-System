package com.itvedant.StudentManagement.reposatory;

import com.itvedant.StudentManagement.model.Chapter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    List<Chapter> findBySubjectIgnoreCaseOrderByChapterNumberAsc(String subject);

    Optional<Chapter> findBySubjectIgnoreCaseAndNameIgnoreCase(
            String subject,
            String name
    );
}