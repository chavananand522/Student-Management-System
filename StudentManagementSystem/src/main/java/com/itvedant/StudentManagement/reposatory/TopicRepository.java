package com.itvedant.StudentManagement.reposatory;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itvedant.StudentManagement.model.Topic;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    List<Topic> findByChapterIdOrderByIdAsc(Long chapterId);

    Optional<Topic> findByChapterIdAndNameIgnoreCase(Long chapterId, String name);
}