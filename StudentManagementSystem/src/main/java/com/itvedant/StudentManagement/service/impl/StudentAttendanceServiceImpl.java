package com.itvedant.StudentManagement.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itvedant.StudentManagement.dto.StudentAttendanceDTO;
import com.itvedant.StudentManagement.model.Attendance;
import com.itvedant.StudentManagement.reposatory.AttendanceRepository;
import com.itvedant.StudentManagement.services.StudentAttendanceService;

@Service
@Transactional(readOnly = true)
public class StudentAttendanceServiceImpl implements StudentAttendanceService {

	private static final Logger log = LoggerFactory.getLogger(StudentAttendanceServiceImpl.class);

	private final AttendanceRepository attendanceRepository;

	public StudentAttendanceServiceImpl(AttendanceRepository attendanceRepository) {
		this.attendanceRepository = attendanceRepository;
	}

	@Override
	public Page<Attendance> getStudentAttendance(Long studentId, Long courseId, LocalDate startDate, LocalDate endDate,
			int page, int size) {

		log.info("Fetching attendance for student {} (course={}, from={}, to={})", studentId, courseId, startDate,
				endDate);

		PageRequest pageRequest = PageRequest.of(page, size);

		// If no filters, use the simple query
		if (courseId == null && startDate == null && endDate == null) {
			return attendanceRepository.findByStudentIdOrderByAttendanceDateDesc(studentId, pageRequest);
		}

		return attendanceRepository.findStudentAttendanceFiltered(studentId, courseId, startDate, endDate, pageRequest);
	}

	@Override
	public List<StudentAttendanceDTO> getSubjectWiseSummary(Long studentId) {
		return attendanceRepository.findSubjectWiseSummary(studentId);
	}

	@Override
	public double getOverallPercentage(Long studentId) {
		long total = attendanceRepository.countByStudentId(studentId);
		if (total == 0)
			return 0.0;

		long present = attendanceRepository.countByStudentIdAndStatus(studentId, "PRESENT");
		long late = attendanceRepository.countByStudentIdAndStatus(studentId, "LATE");

		// Count LATE as present for the percentage
		return ((double) (present + late) / total) * 100.0;
	}

	@Override
	public long getTotalCount(Long studentId) {
		return attendanceRepository.countByStudentId(studentId);
	}

	@Override
	public long getPresentCount(Long studentId) {
		return attendanceRepository.countByStudentIdAndStatus(studentId, "PRESENT");
	}

	@Override
	public long getAbsentCount(Long studentId) {
		return attendanceRepository.countByStudentIdAndStatus(studentId, "ABSENT");
	}

	@Override
	public long getLateCount(Long studentId) {
		return attendanceRepository.countByStudentIdAndStatus(studentId, "LATE");
	}
}