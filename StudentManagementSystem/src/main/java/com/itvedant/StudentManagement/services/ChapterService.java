package com.itvedant.StudentManagement.services;

import com.itvedant.StudentManagement.model.Chapter;

import org.springframework.data.domain.Page;

import java.util.List;

public interface ChapterService {

    Page<Chapter> getChapters(int page, int size);

    List<Chapter> getAllChapters();

    List<Chapter> getChaptersBySubject(String subject);

    Chapter getChapterById(Long id);

    Chapter createChapter(Chapter chapter);

    Chapter updateChapter(Long id, Chapter chapter);

    void deleteChapter(Long id);
}