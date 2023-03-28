package com.flysolo.dmmsugradelevelapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Question implements Parcelable {
    String id;
    String activityID;
    String question;
    String description;
    String image;
    String answer;
    ArrayList<String> choices;
    int points;
    Long createdAt;
    public Question() {
    }

    public Question(String id, String activityID, String question, String description, String image, String answer, ArrayList<String> choices, int points, Long createdAt) {
        this.id = id;
        this.activityID = activityID;
        this.question = question;
        this.description = description;
        this.image = image;
        this.answer = answer;
        this.choices = choices;
        this.points = points;
        this.createdAt = createdAt;
    }

    protected Question(Parcel in) {
        id = in.readString();
        activityID = in.readString();
        question = in.readString();
        description = in.readString();
        image = in.readString();
        answer = in.readString();
        choices = in.createStringArrayList();
        points = in.readInt();
        if (in.readByte() == 0) {
            createdAt = null;
        } else {
            createdAt = in.readLong();
        }
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

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

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public ArrayList<String> getChoices() {
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
        parcel.writeString(activityID);
        parcel.writeString(question);
        parcel.writeString(description);
        parcel.writeString(image);
        parcel.writeString(answer);
        parcel.writeStringList(choices);
        parcel.writeInt(points);
        if (createdAt == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(createdAt);
        }
    }
}
