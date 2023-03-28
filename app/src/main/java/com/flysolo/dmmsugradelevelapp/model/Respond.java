package com.flysolo.dmmsugradelevelapp.model;

import java.util.List;

public class Respond {
    String id;
    String activityID;
    String studentID;
    List<Answer> answers;
    Long dateAnswered;
    public Respond(){}

    public Respond(String id, String activityID, String studentID, List<Answer> answers, Long dateAnswered) {
        this.id = id;
        this.activityID = activityID;
        this.studentID = studentID;
        this.answers = answers;
        this.dateAnswered = dateAnswered;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActivityID() {
        return activityID;
    }

    public void setActivityID(String activityID) {
        this.activityID = activityID;
    }

    public String getStudentID() {
        return studentID;
    }
    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }
    public List<Answer> getAnswers() {
        return answers;
    }
    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }
    public Long getDateAnswered() {
        return dateAnswered;
    }
    public void setDateAnswered(Long dateAnswered) {
        this.dateAnswered = dateAnswered;
    }
}
