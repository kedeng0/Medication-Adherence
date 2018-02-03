package com.appzoro.BP_n_ME.util;

import android.content.Context;
import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import com.appzoro.BP_n_ME.prefs.MedasolPrefs;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

/**
 * Created by Appzoro_ 5 on 8/24/2017.
 */

public class DisabledColorDecorator implements DayViewDecorator {
    private Context context;
    private CalendarDay registerDate;

    public DisabledColorDecorator(Context context) {
        this.context = context;
        MedasolPrefs prefs = new MedasolPrefs(context.getApplicationContext());
        String RegistrationDate = prefs.getEnrollmentDate();
        //Log.e("RegistrationDate", RegistrationDate);
        try{
            String [] dateParts = RegistrationDate.split("-");
            String regyear = dateParts[0];
            String regmonth = dateParts[1];
            String regday = dateParts[2];
            registerDate = (CalendarDay.from(Integer.parseInt(regyear), Integer.parseInt(regmonth)-1, Integer.parseInt(regday)));
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public boolean shouldDecorate(CalendarDay registerDay) {
        return registerDate != null && registerDay.isBefore(registerDate);
    }

    @Override
    public void decorate(DayViewFacade dayView) {
        dayView.addSpan(new ForegroundColorSpan(Color.parseColor("#696969")));
        //dayView.setDaysDisabled(true);
    }
} 

