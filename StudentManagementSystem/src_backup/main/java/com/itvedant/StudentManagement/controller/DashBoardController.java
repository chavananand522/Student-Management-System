package com.itvedant.StudentManagement.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.itvedant.StudentManagement.services.DashboardService;
import com.itvedant.StudentManagement.services.EnrollmentService;

@Controller
public class DashBoardController {
	
	private static final Logger log = LoggerFactory.getLogger(EnrollmentController.class);

	
	private final EnrollmentService enrollmentService;
	private final DashboardService dashboardService;
	
	
	public DashBoardController (EnrollmentService enrollmentService, DashboardService dashboardService) {
		this.enrollmentService= enrollmentService;
		this.dashboardService= dashboardService;
	}

	@GetMapping("/dashboard")
	public String dashboard(Model model) {
		model.addAttribute("dashboardStats", dashboardService.getDashboardStats());
		model.addAttribute("students",enrollmentService.getRecentlyEnrolledStudents(0, 5));
		return"dashboard";
	}
}
