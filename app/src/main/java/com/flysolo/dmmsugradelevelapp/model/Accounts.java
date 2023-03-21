package com.flysolo.dmmsugradelevelapp.model;


import android.os.Parcel;
import android.os.Parcelable;

public class Accounts implements Parcelable {
    public String id;
    String profile;
    String name;
    UserType type;
    String email;
    public Accounts(){}
    public Accounts(String id, String profile, String name, UserType type, String email) {
        this.id = id;
        this.profile = profile;
        this.name = name;
        this.type = type;
        this.email = email;
    }

    protected Accounts(Parcel in) {
        id = in.readString();
        profile = in.readString();
        name = in.readString();
        email = in.readString();
    }

    public static final Creator<Accounts> CREATOR = new Creator<Accounts>() {
        @Override
        public Accounts createFromParcel(Parcel in) {
            return new Accounts(in);
        }

        @Override
        public Accounts[] newArray(int size) {
            return new Accounts[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(profile);
        parcel.writeString(name);
        parcel.writeString(email);
    }
}
