package com.itvedant.StudentManagement.service.impl;

import com.itvedant.StudentManagement.model.Question;
import com.itvedant.StudentManagement.model.Test;
import com.itvedant.StudentManagement.reposatory.TestRepository;
import com.itvedant.StudentManagement.services.TestService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TestServiceImpl implements TestService {

	private final TestRepository testRepository;

	public TestServiceImpl(TestRepository testRepository) {

		this.testRepository = testRepository;
	}

	// =========================================================
	// SAVE TEST
	// =========================================================

	@Override
	@Transactional
	public Test saveTest(Test test) {

		/*
		 * Make sure every question belongs to this test.
		 */
		if (test.getQuestions() != null) {

			for (Question question : test.getQuestions()) {

				question.setTest(test);
			}
		}

		/*
		 * Save test and its questions.
		 */
		return testRepository.save(test);
	}

	// =========================================================
	// GET TEST BY ID
	// =========================================================

	@Override
	@Transactional(readOnly = true)
	public Test getTestById(Long id) {

		return testRepository.findById(id).orElse(null);
	}

	// =========================================================
	// GET ALL TESTS
	// =========================================================

	@Override
	@Transactional(readOnly = true)
	public List<Test> getAllTests() {

		return testRepository.findAll();
	}

	// =========================================================
	// DELETE TEST
	// =========================================================

	@Override
	@Transactional
	public void deleteTest(Long id) {

		testRepository.deleteById(id);
	}
}