package com.flysolo.dmmsugradelevelapp.model;

public class Scores {
    String studentID;
    String studentProfile;
    String studentName;
    int studentScore;

    public Scores() {
    }

    public Scores(String studentID, String studentProfile, String studentName, int studentScore) {
        this.studentID = studentID;
        this.studentProfile = studentProfile;
        this.studentName = studentName;
        this.studentScore = studentScore;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getStudentProfile() {
        return studentProfile;
    }

    public void setStudentProfile(String studentProfile) {
        this.studentProfile = studentProfile;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public int getStudentScore() {
        return studentScore;
    }

    public void setStudentScore(int studentScore) {
        this.studentScore = studentScore;
    }
}
