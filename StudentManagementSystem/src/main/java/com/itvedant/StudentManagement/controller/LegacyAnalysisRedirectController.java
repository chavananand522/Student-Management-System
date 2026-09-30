package com.itvedant.StudentManagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class LegacyAnalysisRedirectController {

	@GetMapping("/tests/analysis/{testId}")
	public String redirectOldAnalysisUrl(@PathVariable Long testId) {
		return "redirect:/admin/tests/" + testId + "/analysis";
	}
}