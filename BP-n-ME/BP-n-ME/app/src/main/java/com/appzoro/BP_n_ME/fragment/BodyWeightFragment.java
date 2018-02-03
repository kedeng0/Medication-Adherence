package com.appzoro.BP_n_ME.fragment;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.appzoro.BP_n_ME.activity.MainActivity;
import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;
import com.appzoro.BP_n_ME.util.CurrentDateDecorator;
import com.appzoro.BP_n_ME.util.DisabledColorDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Appzoro_ 5 on 6/17/2017.
 */

public class BodyWeightFragment extends Fragment implements OnDateSelectedListener {
    View view;
    MaterialCalendarView calendarWeight;
    Calendar calendar;
    CalendarDay registerDate;
    int curYear, curMonth, curDay;
    String year, month, day;
    MedasolPrefs prefs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,
                              Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_bodyweight,container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getActivity().setTitle("Body Weight Record");
        ((MainActivity) getActivity()).getSupportActionBar().setSubtitle("");
        init();
        Listener();
        return view;
    }

    private void init() {
        prefs = new MedasolPrefs(getActivity().getApplicationContext());
        view.findViewById(R.id.bw_text).setVisibility(View.VISIBLE);
        calendarWeight = view.findViewById(R.id.mcalendar_bw);
        calendarWeight.setDateSelected(Calendar.getInstance().getTime(), true);
        calendarWeight.addDecorator(new CurrentDateDecorator(getActivity()));
        calendarWeight.addDecorator(new DisabledColorDecorator(getActivity()));

        String RegistrationDate = prefs.getEnrollmentDate();
        try{
            String [] dateParts = RegistrationDate.split("-");
            year = dateParts[0];
            month = dateParts[1];
            day = dateParts[2];
            registerDate = (CalendarDay.from(Integer.parseInt(year), Integer.parseInt(month)-1, Integer.parseInt(day)));
            calendarWeight.state().edit()
                    .setMinimumDate(CalendarDay.from(Integer.parseInt(year), Integer.parseInt(month)-1, Integer.parseInt(day)))
                    .commit();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void Listener() {
        calendarWeight.setOnDateChangedListener(this);
    }

    @Override
    public void onDateSelected(MaterialCalendarView widget,CalendarDay date, boolean selected) {
        String currDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        curYear = Integer.parseInt(currDate.substring(0, currDate.indexOf("-")));
        curMonth = Integer.parseInt(currDate.substring(currDate.indexOf("-") + 1, currDate.lastIndexOf("-")));
        curDay = Integer.parseInt(currDate.substring(currDate.lastIndexOf("-") + 1, currDate.length()));

        Date current = new Date(curYear,curMonth,curDay);// some Dat
        Date survey = new Date(date.getYear(),date.getMonth()+1,date.getDay());// some Date
        CalendarDay x=calendarWeight.getSelectedDate();
        Date x1 = new Date(x.getYear(),x.getMonth()+1,x.getDay());
        Date oldDateTime = new Date(1,1,2010);
        //Log.e("surveyMonth",Integer.toString(month));
        int one = (int)getDifferenceDays(oldDateTime,current);
        int two = (int)getDifferenceDays(oldDateTime,x1);

        String mon;
        String d;
        if (date.getMonth()+1<10) {
            mon = "0" + Integer.toString(date.getMonth()+1);
        }else
        {
            mon = Integer.toString(date.getMonth()+1);
        }

        if (date.getDay()<10) {
            d = "0" + Integer.toString(date.getDay());
        }else
        {
            d = Integer.toString(date.getDay());
        }
        int daysSince = one-two;

        if (daysSince >= 0) {
            WeightLogsRecordFragment fragment = new WeightLogsRecordFragment();
            Bundle bundle = new Bundle();
            bundle.putString("date", mon +"/"+ d +"/"+ date.getYear());
            //Log.e("date",mon +"/"+ d +"/"+ date.getYear());
            bundle.putInt("daysSince", daysSince);
            fragment.setArguments(bundle);

            getFragmentManager().beginTransaction().add
                        (R.id.Fragment_frame_main_activity, fragment).addToBackStack("WeightLogsRecordFragment").commit();
        }
        else{
            Toast.makeText(getActivity(), "Please only edit the current day.", Toast.LENGTH_SHORT).show();
        }
    }

    public static long getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }
}
