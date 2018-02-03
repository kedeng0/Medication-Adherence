package com.appzoro.BP_n_ME.fragment;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.appzoro.BP_n_ME.activity.PatientMonitor;
import com.appzoro.BP_n_ME.adapter.BPAdapter;
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
 * Created by Appzoro_ 5 on 9/19/2017.
 */

public class BPFragment extends Fragment implements OnDateSelectedListener {
    View view;
    MaterialCalendarView calendarBlood;
    ListView bpList;
    BPAdapter adapter;
    ArrayList<String> highBPList, lowBPList;
    Calendar calendar;
    CalendarDay registerDate;
    Date dat;
    int curYear, curMonth, curDay, daysSince;
    String regyear, regmonth, regday, bpGoal, ParseDate, UID;
    Boolean past = false;
    MedasolPrefs prefs;
    private Progress_dialog progress_d;
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bloodpressure, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getActivity().setTitle("Patient Monitor");
        ((PatientMonitor) getActivity()).getSupportActionBar().setSubtitle("Blood Pressure Record");
        init();
        Listener();
        getBplog();
        return view;
    }

    private void init() {
        progress_d = Progress_dialog.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        prefs = new MedasolPrefs(getActivity().getApplicationContext());
        UID = prefs.getUID();
        highBPList = new ArrayList<>();
        lowBPList = new ArrayList<>();

        prefs.setBPTimeOpen(true);
        view.findViewById(R.id.tv_bpgoal).setVisibility(View.GONE);
        view.findViewById(R.id.text_bpgoal).setVisibility(View.GONE);
        view.findViewById(R.id.bp_text).setVisibility(View.GONE);
        bpList =  view.findViewById(R.id.bpListView);
        calendarBlood = view.findViewById(R.id.mcalendarBp);
        calendarBlood.setDateSelected(Calendar.getInstance().getTime(), true);
        calendarBlood.addDecorator(new CurrentDateDecorator(getActivity()));
        calendarBlood.addDecorator(new DisabledColorDecorator(getActivity()));

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

        String currentDate = new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime());
        Log.e("current date","date"+ currentDate);
        updateHeartRate(currentDate);

    }

    private void Listener() {
        calendarBlood.setOnDateChangedListener(this);
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        String currDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        curYear = Integer.parseInt(currDate.substring(0, currDate.indexOf("-")));
        curMonth = Integer.parseInt(currDate.substring(currDate.indexOf("-") + 1, currDate.lastIndexOf("-")));
        curDay = Integer.parseInt(currDate.substring(currDate.lastIndexOf("-") + 1, currDate.length()));
        Date current = new Date(curYear, curMonth, curDay);// some Dat

        Date survey = new Date(date.getYear(), date.getMonth() + 1, date.getDay());// some Date
        CalendarDay x = calendarBlood.getSelectedDate();
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
        daysSince = one - two;
        //Log.e("Days Since", "" +daysSince);

        if (daysSince >= 0) {
            String Date =  mon + "/" + d + "/" + date.getYear();
            updateHeartRate(Date);
        } else {
            Toast.makeText(getActivity(), "There are no future logs!", Toast.LENGTH_SHORT).show();
        }
    }

    public static long getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public void updateHeartRate(final String date){
        SimpleDateFormat formatter = new SimpleDateFormat("mm/dd/yyyy");
        try {
            Date clicked = formatter.parse(date);
            Date today = new Date();
            if((int)getDifferenceDays(clicked,today)>0){
                past = true;
            }
            else{
                past = false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date Date = inputFormat.parse(date);
            ParseDate = outputFormat.format(Date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Log.e("ParseDate",ParseDate);

        final ArrayList<String> heartLogs = new ArrayList<>();
        mDatabase.child("app").child("users").child(UID).child("HeartRateLog").child(ParseDate).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        while (it.hasNext()) {
                            DataSnapshot heartrate = it.next();
                            String heartRate = heartrate.getValue().toString().trim();
                            //Log.e("heartRate",heartRate);
                            heartLogs.add(heartRate);
                            prefs.setHeartRate(heartLogs);
                            update(date);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );

        final ArrayList<String> datelist = new ArrayList<>();
        mDatabase.child("app").child("users").child(UID).child("HeartRateLog").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                while (it.hasNext()) {
                    DataSnapshot medicine = it.next();
                    String Date = medicine.getKey();
                    datelist.add(Date);
                    if (!datelist.contains(ParseDate)){
                        update(date);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void update(final String date) {
        final List<String> HeartRates =  new ArrayList<String>(Arrays.asList(prefs.getHeartRate().replace("[","").replace("]","").replace(" ","").split(",")));
        Log.e("heartRate size>>>>>", String.valueOf(HeartRates.size()));

        final ArrayList<String> records = new ArrayList<>();
        final ArrayList<String> medTimes = new ArrayList<String>();
        mDatabase.child("app").child("users").child(UID).child("bloodPressureLog").child(ParseDate).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        while (it.hasNext()) {
                            DataSnapshot medicine = it.next();
                            String MedTime = medicine.getKey().toString();
                            medTimes.add(MedTime);

                            int colonNdx = MedTime.indexOf(":");
                            String subKey = MedTime.substring(0,colonNdx);
                            int HR =Integer.parseInt(subKey);
                            if(HR >= 12){
                                if(HR > 12){
                                    HR = HR - 12;
                                }
                                if (HR < 10){
                                    records.add("0" + HR + medicine.getKey().substring(colonNdx, colonNdx + 3) + "PM   " + medicine.getValue().toString()/*+ " " + HeartRates*/);
                                }
                                else {
                                    records.add(HR + medicine.getKey().substring(colonNdx, colonNdx + 3) + "PM   " + medicine.getValue().toString()/*+ " " + HeartRates*/);
                                }
                            }else{
                                records.add(medicine.getKey().toString()+ "AM   " + medicine.getValue().toString()/*+ " " + HeartRates*/);
                            }
                        }
                        String [] timeArray = new String[medTimes.size()];
                        timeArray = medTimes.toArray(timeArray);

                        String [] mArray = new String[records.size()];
                        mArray = records.toArray(mArray);

                        if (mArray.length == 0){
                            Toast.makeText(getActivity(), "Patient didn’t record Blood Pressure!", Toast.LENGTH_SHORT).show();
                        }

                        try{
                            adapter = new BPAdapter(getActivity(),mArray,date,HeartRates, daysSince);
                            bpList.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } catch (Exception ex){
                            ex.printStackTrace();
                        }

                        final String[] finalMArray = mArray;
                        final String[] finalTimeArray =timeArray;

                        //if(past) {
                        if(daysSince == 0) {
                            bpList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, final View view, final int item, long id) {
                                    long viewId = view.getId();

                                    if (viewId == R.id.iv_delete) {
                                        Log.e("Delete", String.valueOf(item));
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                        builder.setTitle("Delete Blood Pressure Log");
                                        builder
                                                .setMessage("Would you like to delete?")
                                                .setCancelable(true)
                                                .setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        Log.e("FinalmArray", finalMArray[item]);
                                                        Log.e("FinalmArray time", finalTimeArray[item]);

                                                        mDatabase.child("app").child("users").child(UID).child("bloodPressureLog").child(ParseDate).child(finalTimeArray[item]).removeValue();
                                                        mDatabase.child("app").child("users").child(UID).child("HeartRateLog").child(ParseDate).child(finalTimeArray[item]).removeValue();
                                                        bpList.setAdapter(null);
                                                        updateHeartRate(date);
                                                    }
                                                })
                                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                    }
                                                });
                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public void getBplog() {
        progress_d.showProgress(getActivity());
        mDatabase.child("app").child("users").child(UID).child("bloodPressureLog").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (prefs.isBPTimeOpen()) {
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
            calendarBlood.addDecorators(new BPFragment.Decorator(calendarDays, "high"));
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
            calendarBlood.addDecorators(new BPFragment.Decorator(calendarDays, "low"));
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
            try{
                if (bp.equals("high")) {
                    view.setSelectionDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.date_selector_red));
                    view.addSpan(new ForegroundColorSpan(Color.parseColor("#ffffff")));
                    //view.addSpan(new ForegroundColorSpan(Color.parseColor("#ff0000")));
                } else {
                    view.setSelectionDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.date_selector_green));
                    view.addSpan(new ForegroundColorSpan(Color.parseColor("#ffffff")));
                    //view.addSpan(new ForegroundColorSpan(Color.parseColor("#5ec639")));
                }
            }catch (Exception e){
                e.printStackTrace();
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
