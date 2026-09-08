package com.itvedant.StudentManagement.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.itvedant.StudentManagement.dto.CourseDTO;
import com.itvedant.StudentManagement.model.Courses;
import com.itvedant.StudentManagement.reposatory.CourseRepository;
import com.itvedant.StudentManagement.services.CourseService;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

	private static final Logger log = LoggerFactory.getLogger(CourseServiceImpl.class);

	private final CourseRepository courseRepository;
	private final ModelMapper mapper;

	public CourseServiceImpl(CourseRepository courseRepository, ModelMapper mapper) {
		this.courseRepository = courseRepository;
		this.mapper = mapper;
	}

	@Override
	public CourseDTO createCourse(CourseDTO courseDTO) {
		log.info("Creating course with code: {}", courseDTO.getCourseCode());
		Courses courses = mapper.map(courseDTO, Courses.class);
		courseRepository.save(courses);
		return mapper.map(courses, CourseDTO.class);
	}

	@Override
	public boolean existsByCourseCode(String courseCode) {
		log.info("Checking if code exists: {}", courseCode);

		return courseRepository.existsByCourseCodeIgnoreCase(courseCode);
	}

	@Override
	public List<CourseDTO> getAllCourses() {
		return courseRepository.findByActiveTrue(Sort.by("courseName")).stream().map(course -> mapper.map(course, CourseDTO.class))
				.collect(Collectors.toList());
	}
	
	@Override
	@Transactional(readOnly = true)
	public Page<CourseDTO> getCourses(int page, int size) {
		log.info("List of courses from {}", page);
		PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Direction.DESC, "id"));
		return courseRepository.findByActiveTrue(pageRequest).map(course -> mapper.map(course, CourseDTO.class));
	}

	@Override
	@Transactional(readOnly = true)
	public CourseDTO getCourseById(Long id) {
		Courses course = courseRepository.findById(id).orElseThrow(() -> new RuntimeException("No Course Found"));
		return mapper.map(course, CourseDTO.class);
	}

	@Override
	public CourseDTO updateCourse(Long id, CourseDTO courseDTO) {
		Courses course = courseRepository.findById(id).orElseThrow(() -> new RuntimeException("No Course Found"));
		mapper.map(courseDTO, course);
		Courses updated = courseRepository.save(course);	
		return mapper.map(updated,CourseDTO.class);
	}

	@Override
	public boolean existsByCourseCodeAndIdNot(String courseCode, Long id) {
		log.info("Code from update page: {}",courseCode, id );
		return courseRepository.existsByCourseCodeIgnoreCaseAndIdNot(courseCode, id);
	}

}
