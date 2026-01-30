package com.quiz.AdaptiveQuiz.dto;

public class SubmissionResponse {
    private boolean complete;
    private String message;

    public SubmissionResponse(boolean complete, String message) {
        this.complete = complete;
        this.message = message;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
