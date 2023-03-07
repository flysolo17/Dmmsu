package com.flysolo.dmmsugradelevelapp.utils;

import android.text.TextUtils;
import android.util.Patterns;

public class Validation {
    public Validation() {

    }

    public  boolean isValidEmail(String target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    public boolean isValidPassword(String target) {
        return target.length() < 7;
    }
    public  boolean isPasswordMatch(String password , String confirmPassword) {
        return password.equals(confirmPassword);
    }
    public  boolean isValidName(String name) {
        String expression= "([a-zA-Z0-9]+)|(['()+,\\-.=]+)";
        return !name.matches(expression);
    }
}
