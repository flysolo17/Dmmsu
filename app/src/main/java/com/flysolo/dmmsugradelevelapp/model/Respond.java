package com.flysolo.dmmsugradelevelapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Respond implements Parcelable {
    String id;
    String lessonID;
    String studentID;
    List<Answer> answers;
    Long dateAnswered;
    public Respond(){}

    public Respond(String id, String lessonID, String studentID, List<Answer> answers, Long dateAnswered) {
        this.id = id;
        this.lessonID = lessonID;
        this.studentID = studentID;
        this.answers = answers;
        this.dateAnswered = dateAnswered;
    }

    protected Respond(Parcel in) {
        id = in.readString();
        lessonID = in.readString();
        studentID = in.readString();
        if (in.readByte() == 0) {
            dateAnswered = null;
        } else {
            dateAnswered = in.readLong();
        }
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

    public String getLessonID() {
        return lessonID;
    }

    public void setLessonID(String lessonID) {
        this.lessonID = lessonID;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(lessonID);
        parcel.writeString(studentID);
        if (dateAnswered == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(dateAnswered);
        }
    }
}
