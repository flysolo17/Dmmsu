package com.flysolo.dmmsugradelevelapp.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

//    val id : String? = null,
//            val teacherID : String ? = null,
//            var background : String ?= null,
//            val name : String? = null,
//            val status : Boolean ? = null,
//            val code : String ? = null,
//            val students : List<String> ? = null,
//        val createdAt : Long ? = null
public class Classroom implements Parcelable {
    String id;
    String teacherID;
    String background;
    String name;
    Boolean status;
    String code;
    List<String> students;
    Long createdAt;
    public Classroom(){}
    public Classroom(String id, String teacherID, String background, String name, Boolean status, String code, List<String> students, Long createdAt) {
        this.id = id;
        this.teacherID = teacherID;
        this.background = background;
        this.name = name;
        this.status = status;
        this.code = code;
        this.students = students;
        this.createdAt = createdAt;
    }

    protected Classroom(Parcel in) {
        id = in.readString();
        teacherID = in.readString();
        background = in.readString();
        name = in.readString();
        byte tmpStatus = in.readByte();
        status = tmpStatus == 0 ? null : tmpStatus == 1;
        code = in.readString();
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

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
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

    public List<String> getStudents() {
        return students;
    }

    public void setStudents(List<String> students) {
        this.students = students;
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
        parcel.writeString(background);
        parcel.writeString(name);
        parcel.writeByte((byte) (status == null ? 0 : status ? 1 : 2));
        parcel.writeString(code);
        parcel.writeStringList(students);
        if (createdAt == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(createdAt);
        }
    }
}
