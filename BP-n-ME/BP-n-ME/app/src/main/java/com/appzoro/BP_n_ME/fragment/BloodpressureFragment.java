package com.appzoro.BP_n_ME.fragment;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.appzoro.BP_n_ME.activity.MainActivity;
import com.appzoro.BP_n_ME.R;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Appzoro_ 5 on 6/17/2017.
 */

public class BloodpressureFragment extends Fragment implements OnDateSelectedListener {
    View view;
    TextView bp_goal;
    MaterialCalendarView calendarBlood;
    Calendar calendar;
    CalendarDay registerDate;
    ArrayList<String> highBPList, lowBPList;
    int curYear, curMonth, curDay;
    String regyear, regmonth, regday, UID, bpGoal;
    Date dat;
    MedasolPrefs prefs;
    private Progress_dialog progress_d;
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bloodpressure, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getActivity().setTitle("Blood Pressure Records");
        ((MainActivity) getActivity()).getSupportActionBar().setSubtitle("");
        init();
        Listener();
        getBplog();
        return view;
    }

    private void init() {
        progress_d = Progress_dialog.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        prefs = new MedasolPrefs(getActivity().getApplicationContext());
        prefs.setBPFirstTimeOpen(true);
        UID = prefs.getUID();

        highBPList = new ArrayList<>();
        lowBPList = new ArrayList<>();
        bp_goal = view.findViewById(R.id.tv_bpgoal);

        mDatabase.child("app").child("users").child(UID).child("bloodpressuregoal").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String goal = dataSnapshot.getValue().toString();
                        Log.e("bpgoal", goal);
                        bp_goal.setText(goal);
                        prefs.setBloodPressureGoal(goal);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        calendarBlood = view.findViewById(R.id.mcalendarBp);
        calendarBlood.setDateSelected(Calendar.getInstance().getTime(), true);
        calendarBlood.addDecorators(new CurrentDateDecorator(getActivity()), new DisabledColorDecorator(getActivity()));

        String RegistrationDate = prefs.getEnrollmentDate();
        try {
            String[] dateParts = RegistrationDate.split("-");
            regyear = dateParts[0];
            regmonth = dateParts[1];
            regday = dateParts[2];
            registerDate = (CalendarDay.from(Integer.parseInt(regyear), Integer.parseInt(regmonth) - 1, Integer.parseInt(regday)));

            calendarBlood.state().edit()
                    .setMinimumDate(CalendarDay.from(Integer.parseInt(regyear), Integer.parseInt(regmonth) - 1, Integer.parseInt(regday)))
                    .commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void Listener() {
        calendarBlood.setOnDateChangedListener(this);
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        String data = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        curYear = Integer.parseInt(data.substring(0, data.indexOf("-")));
        curMonth = Integer.parseInt(data.substring(data.indexOf("-") + 1, data.lastIndexOf("-")));
        curDay = Integer.parseInt(data.substring(data.lastIndexOf("-") + 1, data.length()));
        Date current = new Date(curYear, curMonth, curDay);// some Dat

        Date survey = new Date(date.getYear(), date.getMonth() + 1, date.getDay());// some Date
        CalendarDay x = calendarBlood.getSelectedDate();
        Date x1 = new Date(x.getYear(), x.getMonth() + 1, x.getDay());

        Date oldDateTime = new Date(1, 1, 2010);

        int one = (int) getDifferenceDays(oldDateTime, current);
        int two = (int) getDifferenceDays(oldDateTime, x1);

        String mon;
        String d;

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
        //Log.e("Days Since", Integer.toString(daysSince));

        if (daysSince >= 0) {
            BPLogsRecordFragment fragment = new BPLogsRecordFragment();
            Bundle bundle = new Bundle();
            bundle.putString("date", mon + "/" + d + "/" + date.getYear());
            //Log.e("record date", mon + "/" + d + "/" + date.getYear());
            bundle.putInt("daysSince", daysSince);
            fragment.setArguments(bundle);

            prefs.setBPFirstTimeOpen(false);
            getFragmentManager().beginTransaction().add
                    (R.id.Fragment_frame_main_activity, fragment).addToBackStack("BPLogsRecordFragment").commit();

        } else {
            Toast.makeText(getActivity(), "Please only edit the current day!", Toast.LENGTH_SHORT).show();
        }
    }

    public static long getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public void getBplog() {
        progress_d.showProgress(getActivity());
        mDatabase.child("app").child("users").child(UID).child("bloodPressureLog").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (prefs.isBPFirstTimeOpen()) {
                    Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                    String bpDate = "";
                    while (it.hasNext()) {
                        DataSnapshot medicine = it.next();
                        bpDate = medicine.getKey();
                        String bp = medicine.getValue().toString();
                        //Log.e("BP Date", bpDate);
                        //Log.e("Blood Pressure", bp);
                        List<String> dates = Arrays.asList(bp.replace("{", "").replace("}", "").split(", "));
                        //Log.e("BP", dates.toString());

                        String bpOnly = "";
                        ArrayList<String> dates2 = new ArrayList<String>();
                        if (dates.size() > 1) {
                            for (int i = 0; i < dates.size(); i++) {
                                dates2.add(dates.get(i).substring(0, 5));
                            }
                            Collections.sort(dates2);
                            //bpOnly = dates2.get(dates.size()-1);
                            for (int i = 0; i < dates.size(); i++) {
                                if (dates.get(i).contains(dates2.get(dates2.size() - 1))) {
                                    bpOnly = dates.get(i).substring(dates.get(i).indexOf("=") + 1, dates.get(i).length());
                                }
                            }
                            //Log.e("List", dates2.toString());
                        } else {
                            bpOnly = dates.get(0).substring(dates.get(0).indexOf("=") + 1, dates.get(0).length());
                        }

                        bpGoal = prefs.getBloodPressureGoal().replace("Less than ", "");

                        String systolicGoal = bpGoal.substring(0, 3);
                        String diastolicGoal = bpGoal.substring(4, 6);
                        String systolic = bpOnly.split("/")[0];
                        String diastolic = bpOnly.split("/")[1];

                        if (Integer.parseInt(systolic) > Integer.parseInt(systolicGoal) || Integer.parseInt(diastolic) > Integer.parseInt(diastolicGoal)) {
                            highBPList.add(bpDate);
                        } else {
                            lowBPList.add(bpDate);
                        }
                    }
                    if (!it.hasNext()) {
                        setHighBPDecorator();
                        setLowBPDecorator();
                        progress_d.hideProgress();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }

        });
    }

    private void setHighBPDecorator() {
        List<CalendarDay> calendarDayList = new ArrayList<>();
        for (int i = 0; i < highBPList.size(); i++) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                try {
                    dat = sdf.parse(highBPList.get(i));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                CalendarDay calendarDay = CalendarDay.from(dat);
                calendarDayList.add(calendarDay);
            } catch (Exception e) {
                e.printStackTrace();
            }
            calendarDays = calendarDayList;
            calendarBlood.addDecorators(new Decorator(calendarDays, "high"));
        }
    }

    private void setLowBPDecorator() {
        List<CalendarDay> calendarDayList = new ArrayList<>();
        for (int i = 0; i < lowBPList.size(); i++) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                try {
                    dat = sdf.parse(lowBPList.get(i));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                CalendarDay calendarDay = CalendarDay.from(dat);
                calendarDayList.add(calendarDay);
            } catch (Exception e) {
                e.printStackTrace();
            }
            calendarDays = calendarDayList;
            calendarBlood.addDecorators(new Decorator(calendarDays, "low"));
        }
    }

    private class Decorator implements DayViewDecorator {
        private final HashSet<CalendarDay> dates;
        String bp;

        Decorator(Collection<CalendarDay> dates, String bp) {
            this.dates = new HashSet<>(dates);
            this.bp = bp;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            if (bp.equals("high")) {
                Activity activity = getActivity();
                if (activity != null) {
                    view.setSelectionDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.date_selector_red));
                    view.addSpan(new ForegroundColorSpan(Color.parseColor("#ffffff")));
                    //view.addSpan(new ForegroundColorSpan(Color.parseColor("#ff0000")));
                }
            } else {
                Activity activity = getActivity();
                if (activity != null) {
                    view.setSelectionDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.date_selector_green));
                    view.addSpan(new ForegroundColorSpan(Color.parseColor("#ffffff")));
                    //view.addSpan(new ForegroundColorSpan(Color.parseColor("#5ec639")));
                }
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
}
