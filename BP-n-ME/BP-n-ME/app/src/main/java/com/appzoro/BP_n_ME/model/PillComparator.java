package com.appzoro.BP_n_ME.model;

import java.util.Comparator;

/**
 * Created by Ben on 4/16/2018.
 */

public class PillComparator implements Comparator<Pill> {
    public int compare(Pill early, Pill late) {
        if(early.hour == late.hour){
            return Integer.compare(early.minute, late.minute);
        } else {
            return Integer.compare(early.hour, late.hour);
        }
    }
}
