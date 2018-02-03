package com.appzoro.BP_n_ME.fragment;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.appzoro.BP_n_ME.activity.PatientMonitor;
import com.appzoro.BP_n_ME.adapter.WeightAdapter;
import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;
import com.appzoro.BP_n_ME.util.CurrentDateDecorator;
import com.appzoro.BP_n_ME.util.DisabledColorDecorator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * Created by Appzoro_ 5 on 9/20/2017.
 */

public class BWFragment extends Fragment implements OnDateSelectedListener {
    View view;
    MaterialCalendarView calendarWeight;
    ListView weightList;
    WeightAdapter adapter;
    Calendar calendar;
    CalendarDay registerDate;
    int curYear, curMonth, curDay, daysSince;
    String year, month, day, Date, ParseDate, UID;
    Boolean past = false;
    MedasolPrefs prefs;
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bodyweight,container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getActivity().setTitle("Patient Monitor");
        ((PatientMonitor) getActivity()).getSupportActionBar().setSubtitle("Body Weight Record");
        init();
        Listener();
        return view;
    }

    private void init() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        prefs = new MedasolPrefs(getActivity().getApplicationContext());
        UID = prefs.getUID();

        weightList = view.findViewById(R.id.weightListView);
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

        String currentDate = new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime());
        Log.e("current date","date"+ currentDate);
        update(currentDate);
    }

    private void Listener() {
        calendarWeight.setOnDateChangedListener(this);
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
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

        String mon, d;
        if (date.getMonth()+1<10) {
            mon = "0" + Integer.toString(date.getMonth()+1);
        }else {
            mon = Integer.toString(date.getMonth()+1);
        }

        if (date.getDay()<10) {
            d = "0" + Integer.toString(date.getDay());
        }else {
            d = Integer.toString(date.getDay());
        }
        daysSince = one-two;

        if (daysSince >= 0) {
            Date = mon +"/"+ d +"/"+ date.getYear();
            update(Date);
        } else {
            Toast.makeText(getActivity(), "There are no future logs!", Toast.LENGTH_SHORT).show();
        }
    }

    public static long getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public void update(final String date) {
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

        final ArrayList<String> records = new ArrayList<>();
        final ArrayList<String> medTimes = new ArrayList<String>();
        mDatabase.child("app").child("users").child(UID).child("weightLog").child(ParseDate).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        while (it.hasNext()) {
                            DataSnapshot medicine = it.next();
                            String MedTime = medicine.getKey();
                            medTimes.add(MedTime);

                            int colonNdx = MedTime.indexOf(":");
                            String subKey = MedTime.substring(0,colonNdx);
                            int HR =Integer.parseInt(subKey);

                            if(HR >= 12){
                                if(HR > 12){
                                    HR = HR - 12;
                                }
                                if (HR < 10){
                                    records.add("0" + HR + medicine.getKey().substring(colonNdx, colonNdx + 3) + "PM   " + medicine.getValue().toString()+ " pounds");
                                } else {
                                    records.add(HR + medicine.getKey().substring(colonNdx, colonNdx + 3) + "PM   " + medicine.getValue().toString()+ " pounds");
                                }
                            }else{
                                records.add(medicine.getKey() + "AM   " + medicine.getValue().toString() + " pounds");
                            }
                        }
                        String [] timeArray = new String[medTimes.size()];
                        timeArray = medTimes.toArray(timeArray);

                        String [] mArray = new String[records.size()];
                        mArray = records.toArray(mArray);

                        if (mArray.length == 0){
                            Toast.makeText(getActivity(), "Patient didn’t record Body Weight!", Toast.LENGTH_SHORT).show();
                        }

                        try{
                            adapter = new WeightAdapter(getActivity(),mArray,date,daysSince);
                            weightList.setAdapter(adapter);
                        } catch (Exception ex){
                            ex.printStackTrace();
                        }

                        final String[] finalMArray = mArray;
                        final String[] finalTimeArray =timeArray;
                        if(past) {
                            weightList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, final View view, final int item, long id) {
                                    long viewId = view.getId();
                                    if (viewId == R.id.iv_delete) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                        builder.setTitle("Delete Weight Log");
                                        builder
                                                .setMessage("Would you like to delete?")
                                                .setCancelable(false)
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        mDatabase.child("app").child("users").child(UID).child("weightLog").child(ParseDate).child(finalTimeArray[item]).removeValue();
                                                        records.remove(item);
                                                        medTimes.remove(item);
                                                        update(date);
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
}
