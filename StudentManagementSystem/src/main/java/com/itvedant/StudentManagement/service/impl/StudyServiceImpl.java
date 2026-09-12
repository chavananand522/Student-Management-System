package com.itvedant.StudentManagement.service.impl;

import com.itvedant.StudentManagement.model.Study;
import com.itvedant.StudentManagement.reposatory.StudyRepository;
import com.itvedant.StudentManagement.services.StudyService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudyServiceImpl implements StudyService {

    private final StudyRepository studyRepository;

    public StudyServiceImpl(StudyRepository studyRepository) {
        this.studyRepository = studyRepository;
    }

    @Override
    public List<Study> getAllSubjects() {
        return studyRepository.findAll();
    }

    @Override
    public Study getSubjectByName(String subject) {
        return studyRepository.findBySubjectIgnoreCase(subject)
                .orElse(null);
    }

    @Override
    public Study saveSubject(Study study) {
        return studyRepository.save(study);
    }

    @Override
    public void deleteSubject(Long id) {
        studyRepository.deleteById(id);
    }
}