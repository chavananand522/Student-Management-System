package com.itvedant.StudentManagement.reposatory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itvedant.StudentManagement.model.Chapter;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    List<Chapter> findBySubjectIgnoreCaseOrderByChapterNumberAsc(
            String subject
    );

    List<Chapter> findBySubjectIgnoreCaseAndNameIgnoreCase(
            String subject,
            String name
    );
}