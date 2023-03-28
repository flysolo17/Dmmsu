package com.flysolo.dmmsugradelevelapp.model;

public class Answer {
    String questionID;
    String answer;
    public Answer() {}
    public Answer(String questionID, String answer) {
        this.questionID = questionID;
        this.answer = answer;
    }

    public String getQuestionID() {
        return questionID;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
