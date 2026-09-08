package com.itvedant.StudentManagement.service.impl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.itvedant.StudentManagement.dto.DashboardStatsDTO;
import com.itvedant.StudentManagement.reposatory.CourseRepository;
import com.itvedant.StudentManagement.reposatory.EnrollmentRepository;
import com.itvedant.StudentManagement.reposatory.StudentRepositiry;
import com.itvedant.StudentManagement.services.DashboardService;


@Service
public class DashboardServiceImpl implements DashboardService {

	private static final Logger log = LoggerFactory.getLogger(EnrollmentServiceImpl.class);

	private final EnrollmentRepository enrollmentRepository;

	private final StudentRepositiry studentRepository;

	private final CourseRepository courseRepository;

	DashboardServiceImpl(EnrollmentRepository enrollmentRepository, StudentRepositiry studentRepository,
			CourseRepository courseRepository) {

		this.enrollmentRepository = enrollmentRepository;
		this.studentRepository = studentRepository;
		this.courseRepository = courseRepository;
	}

	@Override
	public DashboardStatsDTO getDashboardStats() {

		long totalStudents = studentRepository.count();

		long totalCourse = courseRepository.count();

		String topPerformingCourse = getTopPetformingCourse();

		YearMonth currentMonth = YearMonth.now();

		LocalDateTime startDate = currentMonth.atDay(1).atStartOfDay();

		LocalDateTime endDate = currentMonth.atEndOfMonth().atTime(LocalTime.MAX);

		long studentEnrolledThisMonth = enrollmentRepository.countDistinctStudentByEnrolledDateBetween(startDate,
				endDate);

		DashboardStatsDTO dashboardStatsDTO = new DashboardStatsDTO();

		dashboardStatsDTO.setTotalStudents(totalStudents);

		dashboardStatsDTO.setTotalCourses(totalCourse);

		dashboardStatsDTO.setTopPerformingCourse(topPerformingCourse);

		dashboardStatsDTO.setStudentEntolledThisMonth(studentEnrolledThisMonth);

		return dashboardStatsDTO;
	}

	private String getTopPetformingCourse() {

		return enrollmentRepository.findAll().stream()

				.collect(Collectors.groupingBy(e -> e.getCourse().getCourseName(), Collectors.counting())).entrySet()

				.stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("N/A");
	}

}