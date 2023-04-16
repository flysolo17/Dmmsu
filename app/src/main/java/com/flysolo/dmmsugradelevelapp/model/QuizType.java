package com.flysolo.dmmsugradelevelapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public enum QuizType implements Parcelable {
    WORD_HUNT("GUESS THE PICTURE + WORD"),FILL_IN_THE_BLANK("FILL IN THE BLANK");

    private String type;

    QuizType(String type) {
        this.type = type;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static final Parcelable.Creator<QuizType> CREATOR = new Parcelable.Creator<QuizType>() {

        public QuizType createFromParcel(Parcel in) {
            QuizType option = QuizType.values()[in.readInt()];
            option.setType(option.type);
            return option;
        }

        public QuizType[] newArray(int size) {
            return new QuizType[size];
        }

    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(ordinal());
        out.writeString(type);
    }

}
