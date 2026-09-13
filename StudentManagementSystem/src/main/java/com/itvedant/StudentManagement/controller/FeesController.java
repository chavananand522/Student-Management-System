package com.itvedant.StudentManagement.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.itvedant.StudentManagement.dto.StudentCourseFeeDTO;
import com.itvedant.StudentManagement.model.Enrollment;
import com.itvedant.StudentManagement.model.Students;
import com.itvedant.StudentManagement.reposatory.FeePaymentRepository;
import com.itvedant.StudentManagement.reposatory.StudentRepositiry;

@Controller
@RequestMapping("/fees-details")
public class FeesController {

	private final StudentRepositiry studentRepositiry;
	private final FeePaymentRepository feePaymentRepository;

	public FeesController(StudentRepositiry studentRepositiry, FeePaymentRepository feePaymentRepository) {

		this.studentRepositiry = studentRepositiry;
		this.feePaymentRepository = feePaymentRepository;
	}

	@GetMapping
	public String feesDetails(Model model) {

		List<Students> students = studentRepositiry.findAllEnrolledStudents();

		List<StudentCourseFeeDTO> feeDetails = new ArrayList<>();

		double wholeFees = 0;
		double wholePaid = 0;
		double wholePending = 0;

		for (Students student : students) {

			List<Enrollment> enrollments = student.getEnrollments();

			if (enrollments == null || enrollments.isEmpty()) {
				continue;
			}

			List<String> courseIds = new ArrayList<>();

			List<String> courseNames = new ArrayList<>();

			List<String> courseFees = new ArrayList<>();

			double studentPaid = 0;
			double studentFees = 0;

			for (Enrollment enrollment : enrollments) {

				if (enrollment.getCourse() == null) {
					continue;
				}

				long courseId = enrollment.getCourse().getId();

				String courseName = enrollment.getCourse().getCourseName();

				double courseFee = enrollment.getCourse().getFee().doubleValue();

				double paid = feePaymentRepository.getPaidByStudentAndCourse(student.getId(), courseId);

				courseIds.add(String.valueOf(courseId));

				courseNames.add(courseName);

				courseFees.add(String.format("%.2f", courseFee));

				studentFees += courseFee;
				studentPaid += paid;
			}

			double studentPending = studentFees - studentPaid;

			if (studentPending < 0) {
				studentPending = 0;
			}

			StudentCourseFeeDTO dto = new StudentCourseFeeDTO(student.getId(),
					student.getFirstName() + " " + student.getLastName(), String.join(", ", courseIds),
					String.join(", ", courseNames), String.join(", ", courseFees), studentPaid, studentPending);

			feeDetails.add(dto);

			wholeFees += studentFees;
			wholePaid += studentPaid;
			wholePending += studentPending;
		}

		model.addAttribute("feeDetails", feeDetails);

		model.addAttribute("wholeFees", wholeFees);

		model.addAttribute("wholePaid", wholePaid);

		model.addAttribute("wholePending", wholePending);

		return "fees-details";
	}
}