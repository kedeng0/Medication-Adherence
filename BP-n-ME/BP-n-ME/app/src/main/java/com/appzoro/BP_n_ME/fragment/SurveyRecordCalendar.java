package com.appzoro.BP_n_ME.fragment;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.activity.PatientMonitor;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;
import com.appzoro.BP_n_ME.util.CurrentDateDecorator;
import com.appzoro.BP_n_ME.util.DisabledColorDecorator;
import com.appzoro.BP_n_ME.util.Progress_dialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Appzoro_ 5 on 9/20/2017.
 */

public class SurveyRecordCalendar extends Fragment implements View.OnClickListener, OnDateSelectedListener {
    View view;
    ImageView img_Back;
    TextView headerText;
    MaterialCalendarView sCalendar;
    Calendar calendar;
    CalendarDay registerDate;
    ArrayList<String> greenDateList, redDateList;
    String regyear, regmonth, regday, UID, medName;
    int curYear, curMonth, curDay, position;
    Date dat;
    MedasolPrefs prefs;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private Progress_dialog progress_d;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_surveyrecordcalendar, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
        return view;
    }

    private void init() {
        Bundle bundle = this.getArguments();
        position = bundle.getInt("position");
        medName = bundle.getString("medName");
        //Log.e("position", ""+ position);

        progress_d = Progress_dialog.getInstance();
        prefs = new MedasolPrefs(getActivity().getApplicationContext());
        UID = prefs.getUID();

        greenDateList = new ArrayList<>();
        redDateList = new ArrayList<>();

        img_Back = view.findViewById(R.id.iv_back);
        headerText = view.findViewById(R.id.text_heading);
        sCalendar = view.findViewById(R.id.mcalendarsurvey);
        sCalendar.setDateSelected(Calendar.getInstance().getTime(), true);
        sCalendar.addDecorator(new CurrentDateDecorator(getActivity()));
        sCalendar.addDecorator(new DisabledColorDecorator(getActivity()));

        String RegistrationDate = prefs.getEnrollmentDate();
        try {
            String[] dateParts = RegistrationDate.split("-");
            regyear = dateParts[0];
            regmonth = dateParts[1];
            regday = dateParts[2];
            registerDate = (CalendarDay.from(Integer.parseInt(regyear), Integer.parseInt(regmonth) - 1, Integer.parseInt(regday)));

            sCalendar.state().edit()
                    .setMinimumDate(CalendarDay.from(Integer.parseInt(regyear), Integer.parseInt(regmonth) - 1, Integer.parseInt(regday)))
                    .commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        /*if (position == 0){
            headerText.setText("Symptom Survey Record");
            getSymptomSurveyAns();
        }
        else*/
        if (position == 0){
            headerText.setText("ARMS-7 Survey Record");
            getArmsSurveyAns();
        }
        else if (position == 1){
            headerText.setText("LifeStyle Survey Record");
            getLifeStyleSurveyAns();
        }
        else if (position == 2){
            headerText.setText("STOFHLA Survey Record");
            getHealthSurveyAns();
        }
        else if (position == 3){
            headerText.setText("App Satisfaction Survey Record");
            getAppSatisfactionSurveyAns();
        }
        else if (position > 3){
            headerText.setText(medName+" Survey Record");
            getMedicationSurveyAns(medName);
        }

        img_Back.setOnClickListener(this);
        sCalendar.setOnDateChangedListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                getFragmentManager().popBackStack();
                break;
        }
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        String currDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        curYear = Integer.parseInt(currDate.substring(0, currDate.indexOf("-")));
        curMonth = Integer.parseInt(currDate.substring(currDate.indexOf("-") + 1, currDate.lastIndexOf("-")));
        curDay = Integer.parseInt(currDate.substring(currDate.lastIndexOf("-") + 1, currDate.length()));
        Date current = new Date(curYear, curMonth, curDay);// some Dat

        Date survey = new Date(date.getYear(), date.getMonth() + 1, date.getDay());// some Date
        CalendarDay x = sCalendar.getSelectedDate();
        Date x1 = new Date(x.getYear(), x.getMonth() + 1, x.getDay());

        Date oldDateTime = new Date(1, 1, 2010);
        int one = (int) getDifferenceDays(oldDateTime, current);
        int two = (int) getDifferenceDays(oldDateTime, x1);

        String mon, d;
        if (date.getMonth() + 1 < 10) {
            mon = "0" + Integer.toString(date.getMonth() + 1);
        } else {
            mon = Integer.toString(date.getMonth() + 1);
        }
        if (date.getDay() < 10) {
            d = "0" + Integer.toString(date.getDay());
        } else {
            d = Integer.toString(date.getDay());
        }
        int daysSince = one - two;
        //Log.e("Days Since", ""+ daysSince);

        if (daysSince >= 0) {
            SurveyRecordView fragment = new SurveyRecordView();
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            bundle.putString("date", date.getYear() + "-" + mon + "-" +d );
            bundle.putString("medName", medName);
            fragment.setArguments(bundle);

            getFragmentManager().beginTransaction().replace
                    (R.id.Fragment_frame1, fragment).addToBackStack("SurveyRecordView").commit();
        } else {
            Toast.makeText(getActivity(), "There are no future logs!", Toast.LENGTH_SHORT).show();
        }
    }

    public static long getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public void getSymptomSurveyAns() {
        progress_d.showProgress(getActivity());
        mDatabase.child("app").child("users").child(UID).child("symptomsurveyanswers").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        while (it.hasNext()) {
                            DataSnapshot medicine = it.next();
                            String lifedate = medicine.getKey();
                            String attempts = medicine.getValue().toString();
                            //Log.e("attempts", lifedate + attempts);
                            if (attempts.equals("[1, 1, 1, 1, 1, 1, 1]")){
                                greenDateList.add(lifedate);                   // All No
                            } else {
                                redDateList.add(lifedate);
                            }
                        }
                        if (!it.hasNext()) {
                            setGreenDecorator();
                            setRedDecorator();
                            progress_d.hideProgress();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public void getArmsSurveyAns() {
        progress_d.showProgress(getActivity());
        mDatabase.child("app").child("users").child(UID).child("arms7surveyanswers").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        while (it.hasNext()) {
                            DataSnapshot medicine = it.next();
                            String lifedate = medicine.getKey();
                            String attempts = medicine.getValue().toString();
                            greenDateList.add(lifedate);
                        }
                        if (!it.hasNext()) {
                            setGreenDecorator();
                            progress_d.hideProgress();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public void getLifeStyleSurveyAns() {
        progress_d.showProgress(getActivity());
        mDatabase.child("app").child("users").child(UID).child("lifestylesurveyanswersRW").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        while (it.hasNext()) {
                            DataSnapshot medicine = it.next();
                            String lifedate = medicine.getKey();
                            String attempts = medicine.getValue().toString();
                            //Log.e("attempts", attempts);
                            if (!attempts.contains("-1")){
                                greenDateList.add(lifedate);
                            }
                        }
                        if (!it.hasNext()) {
                            setGreenDecorator();
                            progress_d.hideProgress();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public void getHealthSurveyAns() {
        progress_d.showProgress(getActivity());
        mDatabase.child("app").child("users").child(UID).child("literacysurveyanswersRW").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        while (it.hasNext()) {
                            DataSnapshot medicine = it.next();
                            String lifedate = medicine.getKey();
                            String attempts = medicine.getValue().toString();
                            //Log.e("attempts", attempts);
                            if (!attempts.contains("-1")){
                                greenDateList.add(lifedate);
                            }
                        }
                        if (!it.hasNext()) {
                            setGreenDecorator();
                            progress_d.hideProgress();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public void getAppSatisfactionSurveyAns() {
        progress_d.showProgress(getActivity());
        mDatabase.child("app").child("users").child(UID).child("appsatisfactionanswers").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        while (it.hasNext()) {
                            DataSnapshot medicine = it.next();
                            String lifedate = medicine.getKey();
                            String attempts = medicine.getValue().toString();
                            greenDateList.add(lifedate);
                        }
                        if (!it.hasNext()) {
                            setGreenDecorator();
                            progress_d.hideProgress();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private void getMedicationSurveyAns(String medName) {
        progress_d.showProgress(getActivity());
        mDatabase.child("app").child("users").child(UID).child(medName +"medadherenceanswers").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        while (it.hasNext()) {
                            DataSnapshot medicine = it.next();
                            String lifedate = medicine.getKey();
                            String attempts = medicine.getValue().toString();
                            greenDateList.add(lifedate);
                        }
                        if (!it.hasNext()) {
                            setGreenDecorator();
                            progress_d.hideProgress();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private void setGreenDecorator() {
        List<CalendarDay> calendarDayList = new ArrayList<>();
        for (int i = 0; i < greenDateList.size(); i++) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                try {
                    dat = sdf.parse(greenDateList.get(i));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                CalendarDay calendarDay = CalendarDay.from(dat);
                calendarDayList.add(calendarDay);
            } catch (Exception e) {
                e.printStackTrace();
            }
            calendarDays = calendarDayList;
            sCalendar.addDecorators(new SurveyRecordCalendar.Decorator(calendarDays, "Yes"));
        }
    }

    private void setRedDecorator() {
        List<CalendarDay> calendarDayList = new ArrayList<>();
        for (int i = 0; i < redDateList.size(); i++) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                try {
                    dat = sdf.parse(redDateList.get(i));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                CalendarDay calendarDay = CalendarDay.from(dat);
                calendarDayList.add(calendarDay);
            } catch (Exception e) {
                e.printStackTrace();
            }
            calendarDays = calendarDayList;
            sCalendar.addDecorators(new SurveyRecordCalendar.Decorator(calendarDays, "AllNo"));
        }
    }

    private class Decorator implements DayViewDecorator {
        private final HashSet<CalendarDay> dates;
        String response;

        Decorator(Collection<CalendarDay> dates, String response) {
            this.dates = new HashSet<>(dates);
            this.response = response;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            if (!response.equals("Yes")) {
                view.setSelectionDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.date_selector_red));
                view.addSpan(new ForegroundColorSpan(Color.parseColor("#ffffff")));
                //view.addSpan(new ForegroundColorSpan(Color.parseColor("#ff0000")));
            } else {
                view.setSelectionDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.date_selector_green));
                view.addSpan(new ForegroundColorSpan(Color.parseColor("#ffffff")));
                //view.addSpan(new ForegroundColorSpan(Color.parseColor("#5ec639")));
            }
        }
    }

    private Collection<CalendarDay> calendarDays = new Collection<CalendarDay>() {
        @Override
        public boolean add(CalendarDay object) {
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends CalendarDay> collection) {
            return false;
        }

        @Override
        public void clear() {
        }

        @Override
        public boolean contains(Object object) {
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @NonNull
        @Override
        public Iterator<CalendarDay> iterator() {
            return null;
        }

        @Override
        public boolean remove(Object object) {
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            return false;
        }

        @Override
        public int size() {
            return 0;
        }

        @NonNull
        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @NonNull
        @Override
        public <T> T[] toArray(T[] array) {
            return null;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        ((PatientMonitor)getActivity()).getSupportActionBar().hide();
    }
    @Override
    public void onStop() {
        super.onStop();
        ((PatientMonitor)getActivity()).getSupportActionBar().show();
    }
}
