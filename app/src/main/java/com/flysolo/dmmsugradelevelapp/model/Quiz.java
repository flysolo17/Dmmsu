package com.flysolo.dmmsugradelevelapp.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;


public class Quiz  implements Parcelable {
    String id;
    String lessonID;
    String name;
    String description;
    int timer;
    QuizType quizType;
    List<Question> questions;
    Long createdAt;
    public Quiz(){}

    public Quiz(String id, String lessonID, String name, String description, int timer, QuizType quizType, List<Question> questions, Long createdAt) {
        this.id = id;
        this.lessonID = lessonID;
        this.name = name;
        this.description = description;
        this.timer = timer;
        this.quizType = quizType;
        this.questions = questions;
        this.createdAt = createdAt;
    }

    protected Quiz(Parcel in) {
        id = in.readString();
        lessonID = in.readString();
        name = in.readString();
        description = in.readString();
        timer = in.readInt();
        quizType = in.readParcelable(QuizType.class.getClassLoader());
        questions = in.createTypedArrayList(Question.CREATOR);
        if (in.readByte() == 0) {
            createdAt = null;
        } else {
            createdAt = in.readLong();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(lessonID);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(timer);
        dest.writeParcelable(quizType, flags);
        dest.writeTypedList(questions);
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

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public QuizType getQuizType() {
        return quizType;
    }

    public void setQuizType(QuizType quizType) {
        this.quizType = quizType;
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
