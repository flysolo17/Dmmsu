package com.flysolo.dmmsugradelevelapp.model;


import java.util.List;
public class Quiz {
    String id;
    String lessonID;
    String name;
    String description;
    List<Question> questions;
    Long createdAt;
    public Quiz() {}

    public Quiz(String id, String lessonID, String name, String description, List<Question> questions, Long createdAt) {
        this.id = id;
        this.lessonID = lessonID;
        this.name = name;
        this.description = description;
        this.questions = questions;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLessonID() {
        return lessonID;
    }

    public void setLessonID(String lessonID) {
        this.lessonID = lessonID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
}
