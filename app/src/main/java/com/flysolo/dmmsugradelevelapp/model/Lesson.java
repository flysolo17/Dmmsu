package com.flysolo.dmmsugradelevelapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Lesson implements Parcelable {
    String id;
    String classroomID;
    String title;
    String description;
    List<Content> contents;
    Long createdAt;
    public Lesson(){}
    public Lesson(String id,String classroomID, String title, String description, List<Content> contents, Long createdAt) {
        this.id = id;
        this.classroomID = classroomID;
        this.title = title;
        this.description = description;
        this.contents = contents;
        this.createdAt = createdAt;
    }

    protected Lesson(Parcel in) {
        id = in.readString();
        classroomID = in.readString();
        title = in.readString();
        description = in.readString();
        contents = in.createTypedArrayList(Content.CREATOR);
        if (in.readByte() == 0) {
            createdAt = null;
        } else {
            createdAt = in.readLong();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(classroomID);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeTypedList(contents);
        if (createdAt == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(createdAt);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Lesson> CREATOR = new Creator<Lesson>() {
        @Override
        public Lesson createFromParcel(Parcel in) {
            return new Lesson(in);
        }

        @Override
        public Lesson[] newArray(int size) {
            return new Lesson[size];
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Content> getContents() {
        return contents;
    }

    public void setContents(List<Content> contents) {
        this.contents = contents;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
}
