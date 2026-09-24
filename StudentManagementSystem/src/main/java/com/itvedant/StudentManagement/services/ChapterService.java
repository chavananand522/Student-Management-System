package com.itvedant.StudentManagement.services;

import java.util.List;

import org.springframework.data.domain.Page;

import com.itvedant.StudentManagement.model.Chapter;

public interface ChapterService {

    Page<Chapter> getChapters(int page, int size);

    List<Chapter> getChaptersByModuleId(Long moduleId);

    Chapter getChapterById(Long id);

    Chapter createChapter(Chapter chapter);

    Chapter updateChapter(Long id, Chapter chapter);

    void deleteChapter(Long id);
}