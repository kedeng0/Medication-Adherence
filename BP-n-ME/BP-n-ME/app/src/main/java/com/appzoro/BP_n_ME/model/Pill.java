package com.appzoro.BP_n_ME.model;

import java.util.Comparator;

/**
 * Created by Ben on 4/14/2018.
 */

public class Pill{
    String name;
    int hour;
    int minute;
    int amount;

    public Pill(String name, int hour, int minute, int amount) {
        this.name = name;
        this.hour = hour;
        this.minute = minute;
        this.amount = amount;
    }
    public String getName() {
        return name;
    }
    public int getHour() {
        return hour;
    }
    public int getMinute() {
        return minute;
    }
    public int getAmount() {
        return amount;
    }

}

