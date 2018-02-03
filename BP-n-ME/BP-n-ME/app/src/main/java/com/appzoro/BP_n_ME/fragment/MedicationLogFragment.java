package com.appzoro.BP_n_ME.fragment;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.activity.MainActivity;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;
import com.appzoro.BP_n_ME.util.util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Appzoro_ 5 on 7/31/2017.
 */

public class MedicationLogFragment extends Fragment implements View.OnClickListener {
    View view;
    Bundle bundle;
    ImageView img_back;
    TextView submit;
    TimePicker timePicker;
    Spinner spinner;
    List<String> medsList = new ArrayList<>();
    List<String> medication, frequency;
    String medicine, date, ParseDate, UID;
    MedasolPrefs prefs;
    private DatabaseReference mDatabase;
    double doses;
    int goalFreq;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_medicationlog ,container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
        Listener();
        return view;
    }

    private void init() {
        img_back = view.findViewById(R.id.iv_back);
        spinner = view.findViewById(R.id.medicationSpinner);
        timePicker = view.findViewById(R.id.timePicker);
        submit = view.findViewById(R.id.tv_submit);

        bundle = this.getArguments();
        date = bundle.getString("date");
        //Log.e("Log date",date);
        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date Date = inputFormat.parse(date);
            ParseDate = outputFormat.format(Date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Log.e("ParseDate",ParseDate);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        prefs = new MedasolPrefs(getActivity().getApplicationContext());
        UID = prefs.getUID();

        medication =  new ArrayList<String>(Arrays.asList(prefs.getMeds().replace("[","").replace("]","").replace(" ","").split(",")));
        Log.e("meds", String.valueOf(medication));

        for(int ind = 0; ind<medication.size(); ind++){
            medication.set(ind, medication.get(ind));
        }
        setArray(medication);
        frequency =  new ArrayList<String>(Arrays.asList(prefs.getFreqList().trim().replace("[","").replace("]","").split(", ")));

        goalFreq = 0;
        for (int i = 0; i < frequency.size(); i++) {
            if (frequency.get(i).equals("Daily")) {
                goalFreq += 1;
            }
            if (frequency.get(i).equals("Twice daily")) {
                goalFreq += 2;
            }
            if (frequency.get(i).equals("Three times daily")) {
                goalFreq += 3;
            }
            if (frequency.get(i).equals("Four times per day")) {
                goalFreq += 4;
            }
        }
        //Log.e("totalfreq", String.valueOf(goalFreq));

        mDatabase.child("app").child("users").child(UID).child("medicineLog").child(ParseDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                medsList.clear();
                Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                while (it.hasNext()) {
                    DataSnapshot medicine = it.next();
                    String medsDate = medicine.getKey();
                    String Medication = medicine.getValue().toString();
                    //Log.e("Medication Date", medsDate);
                    //Log.e("Medication ", Medication);
                    medsList.add(Medication);
                }
                //Log.e("meds size", String.valueOf(medsList.size()));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });

    }

    private void Listener() {
        img_back.setOnClickListener(this);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                util.hideSOFTKEYBOARD(img_back);
                getFragmentManager().popBackStack();
                break;
            case R.id.tv_submit:
                util.hideSOFTKEYBOARD(submit);
                submit();
                break;
        }
    }

    public void setArray(List x){
        // Application of the Array to the Spinner
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, x);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinner.setAdapter(spinnerArrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                //Log.v("item", (String) parent.getItemAtPosition(position));
                medicine = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                //spinner.setPrompt(records[0]);
            }
        });
    }

    private void submit() {
        Calendar rightNow = Calendar.getInstance();
        int isecond = rightNow.get(Calendar.SECOND);
        String time;
        if (Build.VERSION.SDK_INT >= 23 ) {
            if (timePicker.getHour() < 10){
                time = "0"+timePicker.getHour() + ":";
            } else{
                time = timePicker.getHour() + ":";
            }
            int minute = timePicker.getMinute();
            if (minute <10){
                time+="0"+minute;
            }
            else{
                time+=minute;
            }

        } else {
            if (timePicker.getCurrentHour() < 10){
                time = "0" + timePicker.getCurrentHour() + ":";
            } else{
                time = timePicker.getCurrentHour() + ":";
            }
            int minute = timePicker.getCurrentMinute();
            if (minute <10){
                time+="0"+minute;
            }
            else{
                time+=minute;
            }
        }
        time+=":";
        if (isecond <10){
            time+="0"+isecond;
        }
        else{
            time+=isecond;
        }
        Log.e("time",time);

        Calendar calander = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String curtime = simpleDateFormat.format(calander.getTime());
        //Log.e("currentTime",curtime);

        if (!(curtime.compareTo(time)>=0)){
            Toast.makeText(getActivity(), "Please Enter Medication Logs on current time!", Toast.LENGTH_SHORT).show();
        } else {
            if (medicine !=" ") {
                int entryNo = 0;
                int medsSize = 1;
                for (int j = 0;j<medication.size();j++){
                    if (medication.get(j).contains(medicine)){
                        String freq = frequency.get(j);
                        Log.e("<<<<<<<<<<", medicine +" "+  freq);
                        if (frequency.get(j).equals("Daily")){
                            entryNo = 1;
                        } else if (frequency.get(j).equals("Twice daily")){
                            entryNo = 2;
                        } else if (frequency.get(j).equals("Three times daily")){
                            entryNo = 3;
                        } else if (frequency.get(j).equals("Four times per day")) {
                            entryNo = 4;
                        }
                    }
                }
                //Log.e("entryNo",""+entryNo );
                for (int i=0;i<medsList.size();i++){
                    if (medsList.get(i).contains(medicine)){
                        medsSize++;
                    }
                }
                //Log.e("medsSize", ""+medsSize);

                if (!(medsSize > entryNo)){
                    mDatabase.child("app").child("users").child(UID).child("medicineLog").child(ParseDate).child(time).setValue(medicine);

                    Log.e("size", ""+ medsList.size() +"   "+goalFreq );
                    doses = ((double) (medsList.size()+1) / goalFreq) * 100;
                    Log.e("doses", ""+ doses );
                    if (doses >= 80){
                        mDatabase.child("app").child("users").child(UID).child("MedicalCondition").child(ParseDate).setValue("green");
                    } else {
                        mDatabase.child("app").child("users").child(UID).child("MedicalCondition").child(ParseDate).setValue("red");
                    }

                    //TODO: add cancel of repeating alarm within a time frame

                    /*//10AM  limit to 5AM to 12PM
                    if((timePicker.getCurrentHour()<12)&(timePicker.getCurrentHour()>5)){
                        final Intent intent = getActivity().getIntent();
                        Log.e("Should Cancel", "sending 4");
                        *//*Intent i = new Intent(getActivity(), ReminderService.class);
                        i.putExtra("type", 4);
                        getActivity().startService(i);*//*
                    }
                    //2PM  limit to 12PM to 4PM
                    if((timePicker.getCurrentHour()<16)&(timePicker.getCurrentHour()>=12)){
                        Log.e("Alarm", "sending....");
                        Intent alarmIntent = new Intent(getActivity(), MyAlarmReceiver.class);
                        alarmIntent.setData(Uri.parse("custom://" + 1000002));
                        alarmIntent.setAction(String.valueOf(1000002));
                        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                        PendingIntent displayIntent = PendingIntent.getBroadcast(getActivity(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.cancel(displayIntent);
                    }
                    //8PM  limit to 4PM to 12AM
                    if((timePicker.getCurrentHour()<24)&(timePicker.getCurrentHour()>=14)){
                        Intent alarmIntent = new Intent(getActivity(), MyAlarmReceiver.class);
                        alarmIntent.setData(Uri.parse("custom://" + 1000003));
                        alarmIntent.setAction(String.valueOf(1000003));
                        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                        PendingIntent displayIntent = PendingIntent.getBroadcast(getActivity(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.cancel(displayIntent);
                    }*/
                    int colonNdx = time.indexOf(":");
                    String subKey = time.substring(0,colonNdx);
                    String subKey1 = time.substring(colonNdx,time.length());
                    int HR =Integer.parseInt(subKey);
                    //int MIN =Integer.parseInt(subKey1);

                    if(HR >= 12){
                        if(HR > 12){
                            HR = HR - 12;
                        }
                        if (HR < 10){
                            Toast.makeText(getActivity(), "Your medication '"+medicine+"' has been logged at " + "0" + HR + subKey1 + " PM ", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(getActivity(), "Your medication '"+medicine+"' has been logged at " + HR + subKey1 + " PM ", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(getActivity(), "Your medication '"+medicine+"' has been logged at " + time+ " AM ", Toast.LENGTH_LONG).show();
                    }
                    getFragmentManager().popBackStack();
                }
                else {
                    Toast.makeText(getActivity(), "This number of doses exceeds your prescribed frequency!", Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(getActivity(), "Please select a medication!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).getSupportActionBar().hide();
    }
    @Override
    public void onStop() {
        super.onStop();
        ((MainActivity)getActivity()).getSupportActionBar().show();
    }
}
