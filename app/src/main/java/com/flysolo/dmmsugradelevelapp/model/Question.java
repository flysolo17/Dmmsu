package com.flysolo.dmmsugradelevelapp.model;

import java.util.ArrayList;
import java.util.List;

public class Question {
    String question;
    String answer;
    ArrayList<String> choices;
    int points;

    public Question() {
    }

    public Question(String question, String answer, ArrayList<String> choices, int points) {
        this.question =question;
        this.answer = answer;
        this.choices = choices;
        this.points = points;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<String> getChoices() {
        return choices;
    }

    public void setChoices(ArrayList<String> choices) {
        this.choices = choices;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
