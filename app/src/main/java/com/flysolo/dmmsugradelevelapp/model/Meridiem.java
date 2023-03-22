package com.flysolo.dmmsugradelevelapp.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Meridiem {
    int id;
    String meridiem;
    public Meridiem(){}
    public Meridiem(int id, String meridiem) {
        this.id = id;
        this.meridiem = meridiem;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMeridiem() {
        return meridiem;
    }

    public void setMeridiem(String meridiem) {
        this.meridiem = meridiem;
    }
    public List<Meridiem> getMeridiems() {
        List<Meridiem> meridiems = new ArrayList<>();
        meridiems.add(new Meridiem(0, "PM"));
        meridiems.add(new Meridiem(1, "AM"));
        return meridiems;
    }
    public List<String> getNames() {
       List<String> names = new ArrayList<>();
        for (Meridiem m: getMeridiems()) {
            names.add(m.getMeridiem());
        }
       return names;
    }
}
