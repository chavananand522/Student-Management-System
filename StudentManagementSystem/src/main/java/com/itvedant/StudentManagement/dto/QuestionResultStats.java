package com.itvedant.StudentManagement.dto;

public class QuestionResultStats {

    private long correctCount;
    private long wrongCount;
    private long neverOpenedCount;

    public QuestionResultStats() {
    }

    public QuestionResultStats(long correctCount, long wrongCount, long neverOpenedCount) {
        this.correctCount = correctCount;
        this.wrongCount = wrongCount;
        this.neverOpenedCount = neverOpenedCount;
    }

    public long getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(long correctCount) {
        this.correctCount = correctCount;
    }

    public long getWrongCount() {
        return wrongCount;
    }

    public void setWrongCount(long wrongCount) {
        this.wrongCount = wrongCount;
    }

    public long getNeverOpenedCount() {
        return neverOpenedCount;
    }

    public void setNeverOpenedCount(long neverOpenedCount) {
        this.neverOpenedCount = neverOpenedCount;
    }
}