package com.itvedant.StudentManagement.services;

import com.itvedant.StudentManagement.entities.Study;
import com.itvedant.StudentManagement.reposatory.StudyRepository;
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
                .orElseThrow(() -> new RuntimeException("Subject not found: " + subject));
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