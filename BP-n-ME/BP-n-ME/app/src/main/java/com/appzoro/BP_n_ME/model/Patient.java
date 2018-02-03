package com.appzoro.BP_n_ME.model;

/**
 * Created by Appzoro_ 5 on 10/5/2017.
 */

public class Patient {
    public String name;
    public String id;
    public String color;
    public String timestamp;

    public Patient() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
