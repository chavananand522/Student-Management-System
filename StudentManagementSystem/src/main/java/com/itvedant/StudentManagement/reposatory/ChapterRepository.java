package com.itvedant.StudentManagement.reposatory;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itvedant.StudentManagement.model.Chapter;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    List<Chapter> findByModuleIdOrderByIdAsc(Long moduleId);

    Optional<Chapter> findByModuleIdAndNameIgnoreCase(
            Long moduleId,
            String name
    );
}