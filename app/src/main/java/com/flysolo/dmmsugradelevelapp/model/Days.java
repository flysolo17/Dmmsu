package com.flysolo.dmmsugradelevelapp.model;

import static java.util.Calendar.SUNDAY;

import java.util.ArrayList;
import java.util.List;

public class Days {
    int id;
    String day;
    Boolean isClick;
    String MONDAY = "Mon";
    String TUESDAY = "Tue";
    String WEDNESDAY = "Wed";
    String THURSDAY= "Thu";
    String FRIDAY = "Fri";
    String SATURDAY = "Sat";
    String SUNDAY = "Sun";
    public Days(){}
    public Days(int id, String day, Boolean isClick) {
        this.id = id;
        this.day = day;
        this.isClick = isClick;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Boolean getClick() {
        return isClick;
    }

    public void setClick(Boolean click) {
        isClick = click;
    }
    public List<Days> getDays() {
        List<Days> days = new ArrayList<>();
        days.add(new Days(0, SUNDAY,false));
        days.add(new Days(1, MONDAY,false));
        days.add(new Days(2, TUESDAY,false));
        days.add(new Days(3, WEDNESDAY,false));
        days.add(new Days(4, THURSDAY,false));
        days.add(new Days(5, FRIDAY,false));
        days.add(new Days(6, SATURDAY,false));
        return days;
    }
}
