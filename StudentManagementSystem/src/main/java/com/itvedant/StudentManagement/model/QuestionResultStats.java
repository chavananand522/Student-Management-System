package com.itvedant.StudentManagement.model;

public class QuestionResultStats {

    private final long correctCount;
    private final long wrongCount;
    private final long neverOpenedCount;

    public QuestionResultStats(
            long correctCount,
            long wrongCount,
            long neverOpenedCount) {

        this.correctCount = correctCount;
        this.wrongCount = wrongCount;
        this.neverOpenedCount = neverOpenedCount;
    }

    public long getCorrectCount() {
        return correctCount;
    }

    public long getWrongCount() {
        return wrongCount;
    }

    public long getNeverOpenedCount() {
        return neverOpenedCount;
    }
}