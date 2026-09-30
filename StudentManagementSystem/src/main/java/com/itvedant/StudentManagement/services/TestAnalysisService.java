package com.itvedant.StudentManagement.services;

import com.itvedant.StudentManagement.dto.TestAnalysisDTO;

public interface TestAnalysisService {

    TestAnalysisDTO getTestAnalysis(Long testId);

}