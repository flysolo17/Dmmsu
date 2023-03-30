package com.flysolo.dmmsugradelevelapp.model;


import android.os.Parcel;
import android.os.Parcelable;




public class Quiz  implements Parcelable {
    String id;
    String lessonID;
    String name;
    String description;
    Long createdAt;
    public Quiz() {}

    public Quiz(String id, String lessonID, String name, String description, Long createdAt) {
        this.id = id;
        this.lessonID = lessonID;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }

    protected Quiz(Parcel in) {
        id = in.readString();
        lessonID = in.readString();
        name = in.readString();
        description = in.readString();
        if (in.readByte() == 0) {
            createdAt = null;
        } else {
            createdAt = in.readLong();
        }
    }

    public static final Creator<Quiz> CREATOR = new Creator<Quiz>() {
        @Override
        public Quiz createFromParcel(Parcel in) {
            return new Quiz(in);
        }

        @Override
        public Quiz[] newArray(int size) {
            return new Quiz[size];
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
        parcel.writeString(lessonID);
        parcel.writeString(name);
        parcel.writeString(description);
        if (createdAt == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(createdAt);
        }
    }
}
