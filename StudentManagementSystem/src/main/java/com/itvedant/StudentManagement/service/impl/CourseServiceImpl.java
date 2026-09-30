package com.itvedant.StudentManagement.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itvedant.StudentManagement.dto.CourseDTO;
import com.itvedant.StudentManagement.model.Courses;
import com.itvedant.StudentManagement.reposatory.CourseRepository;
import com.itvedant.StudentManagement.reposatory.EnrollmentRepository;
import com.itvedant.StudentManagement.services.CourseService;

import jakarta.persistence.EntityManager;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

	private static final Logger log = LoggerFactory.getLogger(CourseServiceImpl.class);

	private final CourseRepository courseRepository;

	private final ModelMapper mapper;
	private final EntityManager entityManager;

	public CourseServiceImpl(CourseRepository courseRepository, EnrollmentRepository enrollmentRepository,
			ModelMapper mapper, EntityManager entityManager) {

		this.courseRepository = courseRepository;

		this.mapper = mapper;
		this.entityManager = entityManager;
	}

	@Override
	@Transactional(readOnly = true)
	public List<CourseDTO> getAllCourses() {

		return courseRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
				.map(course -> mapper.map(course, CourseDTO.class)).collect(Collectors.toList());
	}

	@Override
	public CourseDTO createCourse(CourseDTO courseDTO) {

		if (existsByCourseCode(courseDTO.getCourseCode())) {
			throw new RuntimeException("Course code already exists");
		}

		Courses course = mapper.map(courseDTO, Courses.class);

		course.setActive(true);

		Courses savedCourse = courseRepository.save(course);

		return mapper.map(savedCourse, CourseDTO.class);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByCourseCode(String courseCode) {

		return courseRepository.existsByCourseCodeIgnoreCase(courseCode);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByCourseCodeAndIdNot(String courseCode, Long id) {

		return courseRepository.existsByCourseCodeIgnoreCaseAndIdNot(courseCode, id);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<CourseDTO> getCourses(int page, int size) {

		PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));

		return courseRepository.findByActiveTrue(pageRequest).map(course -> mapper.map(course, CourseDTO.class));
	}

	@Override
	@Transactional(readOnly = true)
	public CourseDTO getCourseById(Long id) {

		Courses course = courseRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Course not found with id: " + id));

		return mapper.map(course, CourseDTO.class);
	}

	@Override
	public CourseDTO updateCourse(Long id, CourseDTO courseDTO) {

		Courses course = courseRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Course not found with id: " + id));

		if (courseRepository.existsByCourseCodeIgnoreCaseAndIdNot(courseDTO.getCourseCode(), id)) {
			throw new RuntimeException("Course code already exists");
		}

		course.setCourseName(courseDTO.getCourseName());
		course.setCourseCode(courseDTO.getCourseCode());
		course.setDuration(courseDTO.getDuration());
		course.setFee(courseDTO.getFee());
		course.setDescription(courseDTO.getDescription());
		course.setActive(courseDTO.isActive());

		Courses updatedCourse = courseRepository.save(course);

		return mapper.map(updatedCourse, CourseDTO.class);
	}

	@Override
	public void deleteCourse(Long id) {

		log.info("Deleting course permanently: {}", id);

		courseRepository.findById(id).orElseThrow(() -> new RuntimeException("Course not found with id: " + id));

		entityManager.flush();
		entityManager.clear();

		try {

			entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();

			entityManager.createNativeQuery("DELETE FROM enrollment WHERE course_id = :courseId")
					.setParameter("courseId", id).executeUpdate();

			entityManager.createNativeQuery("DELETE FROM courses WHERE id = :courseId").setParameter("courseId", id)
					.executeUpdate();

			entityManager.createNativeQuery("CREATE TEMPORARY TABLE course_id_map (" + "old_id BIGINT PRIMARY KEY, "
					+ "new_id BIGINT NOT NULL)").executeUpdate();

			entityManager.createNativeQuery("SET @new_id = 0").executeUpdate();

			entityManager.createNativeQuery("INSERT INTO course_id_map (old_id, new_id) "
					+ "SELECT id, (@new_id := @new_id + 1) " + "FROM courses " + "ORDER BY id").executeUpdate();

			entityManager.createNativeQuery("UPDATE courses " + "SET id = -id " + "WHERE id > 0").executeUpdate();

			entityManager
					.createNativeQuery("UPDATE enrollment " + "SET course_id = -course_id " + "WHERE course_id > 0")
					.executeUpdate();

			entityManager.createNativeQuery("UPDATE enrollment e " + "JOIN course_id_map m "
					+ "ON -e.course_id = m.old_id " + "SET e.course_id = m.new_id").executeUpdate();

			entityManager.createNativeQuery(
					"UPDATE courses c " + "JOIN course_id_map m " + "ON -c.id = m.old_id " + "SET c.id = m.new_id")
					.executeUpdate();

			entityManager.createNativeQuery("DROP TEMPORARY TABLE course_id_map").executeUpdate();

			Number countResult = (Number) entityManager.createNativeQuery("SELECT COUNT(*) FROM courses")
					.getSingleResult();

			long courseCount = countResult.longValue();

			entityManager.createNativeQuery("ALTER TABLE courses AUTO_INCREMENT = " + (courseCount + 1))
					.executeUpdate();

			entityManager.clear();

			log.info("Course {} deleted and IDs renumbered successfully", id);

		} finally {

			entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();

			entityManager.clear();
		}
	}
}