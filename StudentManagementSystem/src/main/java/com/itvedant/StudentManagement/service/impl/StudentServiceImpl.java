package com.itvedant.StudentManagement.service.impl;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.itvedant.StudentManagement.dto.CourseDTO;
import com.itvedant.StudentManagement.dto.StudentDTO;
import com.itvedant.StudentManagement.model.Courses;
import com.itvedant.StudentManagement.model.Students;
import com.itvedant.StudentManagement.reposatory.StudentRepositiry;
import com.itvedant.StudentManagement.services.StudentService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

	private static final Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);

	private final StudentRepositiry studentRepository;
	private final ModelMapper mapper;

	public StudentServiceImpl(StudentRepositiry studentRepository, ModelMapper mapper) {
		this.studentRepository = studentRepository;
		this.mapper = mapper;
	}

	public boolean existsByEmailIgnoreCase(String email) {
		log.info("Email from create student");
		return studentRepository.existsByEmailIgnoreCase(email);
	}

	@Override
	public StudentDTO createStudent(StudentDTO studentDTO) {
		log.info("Saving student data");
		Students students = mapper.map(studentDTO, Students.class);
		Students saved = studentRepository.save(students);
		return mapper.map(saved, StudentDTO.class);
	}

	@Override
	public Page<StudentDTO> getStudents(int page, int size) {
		log.info("List of Students from {}", page);
		PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Direction.DESC, "id"));
		return studentRepository.findByActiveTrue(pageRequest).map(student -> mapper.map(student, StudentDTO.class));
	}

	@Override
	@Transactional(readOnly = true)
	public StudentDTO getStudentById(Long id) {
		Students student = studentRepository.findById(id).orElseThrow(() -> new RuntimeException("No Student Found"));
		return mapper.map(student, StudentDTO.class);
		
	}

	@Override
	public boolean existsByEmailIgnoreCaseAndIdNot(String email,Long id) {
		log.info("Email from Update student");
		return studentRepository.existsByEmailIgnoreCaseAndIdNot(email,id);
	}

	@Override
	public StudentDTO updateStudent(Long id, StudentDTO studentDTO) {
		Students student = studentRepository.findById(id).orElseThrow(() -> new RuntimeException("No Student Found"));

		mapper.map(studentDTO, student);
		Students updated = studentRepository.save(student);
		
		return mapper.map(updated,StudentDTO .class);
	}

}
