package com.flysolo.dmmsugradelevelapp.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;


public class Classroom implements Parcelable{
    String id;
    String teacherID;
    String name;
    Boolean status;
    String code;
    String startTime;
    List<String> schedule;
    List<String> students;
    List<String> activeStudents;
    Long createdAt;
    public Classroom(){}

    public Classroom(String id, String teacherID, String name, Boolean status, String code, String startTime, List<String> schedule, List<String> students,List<String> activeStudents, Long createdAt) {
        this.id = id;
        this.teacherID = teacherID;
        this.name = name;
        this.status = status;
        this.code = code;
        this.startTime = startTime;
        this.schedule = schedule;
        this.students = students;
        this.activeStudents = activeStudents;
        this.createdAt = createdAt;
    }

    protected Classroom(Parcel in) {
        id = in.readString();
        teacherID = in.readString();
        name = in.readString();
        byte tmpStatus = in.readByte();
        status = tmpStatus == 0 ? null : tmpStatus == 1;
        code = in.readString();
        startTime = in.readString();
        schedule = in.createStringArrayList();
        students = in.createStringArrayList();
        if (in.readByte() == 0) {
            createdAt = null;
        } else {
            createdAt = in.readLong();
        }
    }

    public static final Creator<Classroom> CREATOR = new Creator<Classroom>() {
        @Override
        public Classroom createFromParcel(Parcel in) {
            return new Classroom(in);
        }

        @Override
        public Classroom[] newArray(int size) {
            return new Classroom[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public List<String> getSchedule() {
        return schedule;
    }

    public void setSchedule(List<String> schedule) {
        this.schedule = schedule;
    }

    public List<String> getStudents() {
        return students;
    }

    public void setStudents(List<String> students) {
        this.students = students;
    }

    public List<String> getActiveStudents() {
        return activeStudents;
    }

    public void setActiveStudents(List<String> activeStudents) {
        this.activeStudents = activeStudents;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(teacherID);
        parcel.writeString(name);
        parcel.writeByte((byte) (status == null ? 0 : status ? 1 : 2));
        parcel.writeString(code);
        parcel.writeString(startTime);
        parcel.writeStringList(schedule);
        parcel.writeStringList(students);
        if (createdAt == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(createdAt);
        }
    }
}
