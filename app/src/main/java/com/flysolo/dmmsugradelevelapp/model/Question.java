package com.flysolo.dmmsugradelevelapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Question implements Parcelable {
    String id;
    String question;
    String image;
    String answer;
    List<String> choices;
    int points;

    public Question() {
    }

    public Question(String id, String question, String image, String answer, List<String> choices, int points) {
        this.id = id;
        this.question = question;
        this.image = image;
        this.answer = answer;
        this.choices = choices;
        this.points = points;
    }

    protected Question(Parcel in) {
        id = in.readString();
        question = in.readString();
        image = in.readString();
        answer = in.readString();
        choices = in.createStringArrayList();
        points = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(question);
        dest.writeString(image);
        dest.writeString(answer);
        dest.writeStringList(choices);
        dest.writeInt(points);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
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

    public List<String> getChoices() {
        return choices;
    }

    public void setChoices(List<String> choices) {
        this.choices = choices;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
