package com.itvedant.StudentManagement.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.itvedant.StudentManagement.model.Chapter;
import com.itvedant.StudentManagement.reposatory.ChapterRepository;
import com.itvedant.StudentManagement.services.ChapterService;

@Service
public class ChapterServiceImpl implements ChapterService {

	private final ChapterRepository chapterRepository;

	public ChapterServiceImpl(ChapterRepository chapterRepository) {
		this.chapterRepository = chapterRepository;
	}

	@Override
	public Page<Chapter> getChapters(int page, int size) {
		return chapterRepository.findAll(PageRequest.of(page, size));
	}

	@Override
	public List<Chapter> getChaptersByModuleId(Long moduleId) {
		return chapterRepository.findByModuleIdOrderByIdAsc(moduleId);
	}

	@Override
	public Chapter getChapterById(Long id) {
		return chapterRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Chapter not found with id: " + id));
	}

	@Override
	public Chapter createChapter(Chapter chapter) {
		return chapterRepository.save(chapter);
	}

	@Override
	public Chapter updateChapter(Long id, Chapter chapter) {

		Chapter existingChapter = getChapterById(id);

		existingChapter.setName(chapter.getName());
		existingChapter.setDescription(chapter.getDescription());
		existingChapter.setSubject(chapter.getSubject());
		existingChapter.setModuleId(chapter.getModuleId());

		return chapterRepository.save(existingChapter);
	}

	@Override
	public void deleteChapter(Long id) {

		Chapter chapter = getChapterById(id);

		chapterRepository.delete(chapter);
	}
}