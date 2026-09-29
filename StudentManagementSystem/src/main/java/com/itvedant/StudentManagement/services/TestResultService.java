package com.itvedant.StudentManagement.services;

import java.util.List;
import java.util.Map;

import com.itvedant.StudentManagement.dto.QuestionResultStats;
import com.itvedant.StudentManagement.model.Question;

public interface TestResultService {

    Map<Long, QuestionResultStats> getQuestionResultStats(
            Long testId,
            List<Question> questions);
}