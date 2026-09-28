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

    private static final Logger log =
            LoggerFactory.getLogger(
                    DashBoardController.class);

    private final EnrollmentService enrollmentService;

    private final DashboardService dashboardService;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public DashBoardController(
            EnrollmentService enrollmentService,
            DashboardService dashboardService) {

        this.enrollmentService = enrollmentService;

        this.dashboardService = dashboardService;
    }

    // =========================================================
    // HOME
    // =========================================================

    @GetMapping("/")
    public String home() {

        log.info(
                "Redirecting user from / to /dashboard");

        return "redirect:/dashboard";
    }

    // =========================================================
    // ADMIN DASHBOARD
    // =========================================================

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        log.info(
                "Loading admin dashboard");

        // -----------------------------------------------------
        // DASHBOARD STATISTICS
        // -----------------------------------------------------

        model.addAttribute(
                "dashboardStats",
                dashboardService.getDashboardStats());

        // -----------------------------------------------------
        // RECENT STUDENT REGISTRATIONS
        // -----------------------------------------------------

        model.addAttribute(
                "students",
                enrollmentService
                        .getRecentlyEnrolledStudents(0, 5));

        // -----------------------------------------------------
        // ADMIN DASHBOARD TEMPLATE
        // -----------------------------------------------------

        return "admin/dashboard";
    }
}