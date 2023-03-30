package com.flysolo.dmmsugradelevelapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Respond implements Parcelable {
    String id;
    String classroomID;
    String activityID;
    String studentID;
    List<Answer> answers;
    Long dateAnswered;
    public Respond(){}

    public Respond(String id, String classroomID, String activityID, String studentID, List<Answer> answers, Long dateAnswered) {
        this.id = id;
        this.classroomID = classroomID;
        this.activityID = activityID;
        this.studentID = studentID;
        this.answers = answers;
        this.dateAnswered = dateAnswered;
    }


    protected Respond(Parcel in) {
        id = in.readString();
        classroomID = in.readString();
        activityID = in.readString();
        studentID = in.readString();
        if (in.readByte() == 0) {
            dateAnswered = null;
        } else {
            dateAnswered = in.readLong();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(classroomID);
        dest.writeString(activityID);
        dest.writeString(studentID);
        if (dateAnswered == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(dateAnswered);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Respond> CREATOR = new Creator<Respond>() {
        @Override
        public Respond createFromParcel(Parcel in) {
            return new Respond(in);
        }

        @Override
        public Respond[] newArray(int size) {
            return new Respond[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassroomID() {
        return classroomID;
    }

    public void setClassroomID(String classroomID) {
        this.classroomID = classroomID;
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
