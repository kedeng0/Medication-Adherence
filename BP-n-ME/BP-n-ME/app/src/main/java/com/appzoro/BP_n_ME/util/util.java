package com.appzoro.BP_n_ME.util;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.Calendar;

/**
 * Created by Appzoro_ 5 on 8/3/2017.
 */

public class util {

    public static String TimeStamp;
    public String DateStamp;
    public static InputFilter filter;

    public util() {
        //default constructor because why not
    }

    public static int countMatches(String findIn, String string){
        int lenFindIn = findIn.length();
        String ne = findIn.replace(string, "");
        int count = (lenFindIn- ne.length())/string.length();
        if(count < 0){
            count = 0;
        }
        return count;
    }

    public static boolean isInternetOn(Activity activity) {
        ConnectivityManager cm = (ConnectivityManager) activity
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            Log.v("Android", "Internet is working");
            return true;
        } else {
            Log.v("Android", "Check Your Internet Connection");
            return false;
        }
    }

    public static String getTimeStamp() {
        Calendar rightNow = Calendar.getInstance();
        int ihour = rightNow.get(Calendar.HOUR_OF_DAY);
        int iminute = rightNow.get(Calendar.MINUTE);
        int isecond = rightNow.get(Calendar.SECOND);
        String hour;
        if (ihour<10) {
            hour = "0" + Integer.toString(ihour);
        }else
        {
            hour = Integer.toString(ihour);
        }
        String minute;
        if (iminute<10) {
            minute = "0" + Integer.toString(iminute);
        }else
        {
            minute = Integer.toString(iminute);
        }

        String second;
        if (iminute<10) {
            second = "0" + Integer.toString(isecond);
        }else
        {
            second = Integer.toString(isecond);
        }
        TimeStamp = hour + ":" + minute +":"+second;

        return TimeStamp;
    }

    public static String getTime() {
        Calendar rightNow = Calendar.getInstance();
        int ihour = rightNow.get(Calendar.HOUR_OF_DAY);
        int iminute = rightNow.get(Calendar.MINUTE);
        int isecond = rightNow.get(Calendar.SECOND);
        String hour;
        if (ihour<10) {
            hour = "0" + Integer.toString(ihour);
        }else
        {
            hour = Integer.toString(ihour);
        }
        String minute;
        if (iminute<10) {
            minute = "0" + Integer.toString(iminute);
        }else
        {
            minute = Integer.toString(iminute);
        }

        String second;
        if (iminute<10) {
            second = "0" + Integer.toString(isecond);
        }else
        {
            second = Integer.toString(isecond);
        }
        TimeStamp = hour + ":" + minute;

        return TimeStamp;
    }

    public static  void print_phone(String phone)
    {
        filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.length() > 0) {
                    if (!Character.isDigit(source.charAt(0)))
                        return "";
                    else {
                        if (dstart == 3) {
                            return source + ") ";
                        } else if (dstart == 0) {
                            return "(" + source;
                        } else if ((dstart == 5) || (dstart == 9))
                            return "-" + source;
                        else if (dstart >= 14)
                            return "";
                    }
                }
                return null;
            }
        };
    }



    public static void hideSOFTKEYBOARD(View view) {
        try {
            ((InputMethodManager) view.getContext().getSystemService(Service.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (NullPointerException e) {
        }
    }

}
