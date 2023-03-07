package com.flysolo.dmmsugradelevelapp.model;


public class Accounts {
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
}
