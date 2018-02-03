package com.appzoro.BP_n_ME.fragment;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.appzoro.BP_n_ME.activity.MainActivity;
import com.appzoro.BP_n_ME.adapter.MedicationRecordAdapter;
import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Appzoro_ 5 on 7/31/2017.
 */

public class MedicationLogsRecordFragment extends Fragment implements View.OnClickListener {
    View view;
    ListView medsList;
    ImageView img_back;
    FloatingActionButton backToCal, addinput;
    MedicationRecordAdapter adapter;
    int daysSince;
    Boolean past = false;
    String date, ParseDate, UID;
    MedasolPrefs prefs;
    private DatabaseReference mDatabase;
    List<String> frequency;
    double doses;
    int goalFreq;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,
                              Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_medicationlogsrecord ,container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
        Listener();
        return view;
    }

    private void init() {
        Bundle bundle = this.getArguments();
        date = bundle.getString("date");
        //Log.e("Log date",date);
        daysSince = bundle.getInt("daysSince");
        //Log.e("daysSince", String.valueOf(daysSince));

        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date Date = inputFormat.parse(date);
            ParseDate = outputFormat.format(Date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Log.e("ParseDate",ParseDate);

        img_back = view.findViewById(R.id.iv_back);
        medsList = view.findViewById(R.id.medicationListView);
        backToCal =  view.findViewById(R.id.backToCal);
        addinput =  view.findViewById(R.id.inputLog);

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

        mDatabase = FirebaseDatabase.getInstance().getReference();
        prefs = new MedasolPrefs(getActivity().getApplicationContext());
        UID = prefs.getUID();

        frequency =  new ArrayList<>(Arrays.asList(prefs.getFreqList().trim().replace("[","").replace("]","").split(", ")));
        goalFreq = 0;
        for (int i = 0; i < frequency.size(); i++) {
            if (frequency.get(i).equals("Daily")) {
                goalFreq += 1;
            } if (frequency.get(i).equals("Twice daily")) {
                goalFreq += 2;
            } if (frequency.get(i).equals("Three times daily")) {
                goalFreq += 3;
            } if (frequency.get(i).equals("Four times per day")) {
                goalFreq += 4;
            }
        }
        //Log.e("totalfreq", String.valueOf(goalFreq));

        update();

        if (daysSince == 0){
            backToCal.setVisibility(View.GONE);
            addinput.setVisibility(View.VISIBLE);
        }
        else {
            backToCal.setVisibility(View.VISIBLE);
            addinput.setVisibility(View.GONE);
        }
    }

    private void Listener() {
        img_back.setOnClickListener(this);
        backToCal.setOnClickListener(this);
        addinput.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                //getFragmentManager().popBackStack();
                getFragmentManager().beginTransaction().replace
                        (R.id.Fragment_frame_main_activity, new MedicationUtilizationFragment()).commit();
                break;
            case R.id.backToCal:
                getFragmentManager().beginTransaction().replace
                        (R.id.Fragment_frame_main_activity, new MedicationUtilizationFragment()).commit();
                break;
            case R.id.inputLog:
                MedicationLogFragment fragment = new MedicationLogFragment();
                Bundle bundle = new Bundle();
                bundle.putString("date", date);
                //Log.e("date",date);
                fragment.setArguments(bundle);

                getFragmentManager().beginTransaction().replace
                        (R.id.Fragment_frame_main_activity, fragment).addToBackStack("MedicationLogFragment").commit();
                break;
        }
    }

    public static long getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public void update() {
        mDatabase.child("app").child("users").child(UID).child("medicineLog").child(ParseDate).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final ArrayList<String> records = new ArrayList<>();
                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        final ArrayList<String> medTimes = new ArrayList<>();
                        final ArrayList<String> MedsList = new ArrayList<>();
                        while (it.hasNext()) {
                            DataSnapshot medicine = it.next();
                            String MedTime = medicine.getKey();
                            medTimes.add(MedTime);
                            String Medication = medicine.getValue().toString();
                            MedsList.add(Medication);
                            //Log.e("MEDTIME",MedTime);
                            int colonNdx = MedTime.indexOf(":");
                            String subKey = MedTime.substring(0,colonNdx);
                            int HR =Integer.parseInt(subKey);

                            if(HR >= 12){
                                if(HR > 12){
                                    HR = HR - 12;
                                }
                                if (HR < 10){
                                    records.add("0" + HR + medicine.getKey().substring(colonNdx, colonNdx + 3) + "PM   " + medicine.getValue().toString());
                                } else {
                                    records.add(HR + medicine.getKey().substring(colonNdx, colonNdx + 3) + "PM   " + medicine.getValue().toString());
                                }
                            }else{
                                records.add(medicine.getKey().substring(0, colonNdx + 3)+ "AM   " + medicine.getValue().toString());
                            }

                        }
                        String [] timeArray = new String[medTimes.size()];
                        timeArray = medTimes.toArray(timeArray);

                        String [] mArray = new String[records.size()];
                        mArray = records.toArray(mArray);
                        try{
                            adapter = new MedicationRecordAdapter(getActivity(),mArray, date, daysSince);
                            medsList.setAdapter(adapter);
                        } catch ( Exception ex){
                            ex.printStackTrace();
                        }

                        final String[] finalMArray = mArray;
                        final String[] finalTimeArray =timeArray;

                        //if(past) {
                        if(daysSince == 0) {
                            medsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, final View view, final int item, long l) {
                                    long viewId = view.getId();
                                    if (viewId == R.id.iv_delete) {
                                        //Log.e("Delete", String.valueOf(item));
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                        builder.setTitle("Delete Medication Log");
                                        builder
                                                .setMessage("Would you like to delete?")
                                                .setCancelable(false)
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {

                                                        Log.e("FinalmArray", finalMArray[item]);
                                                        Log.e("FinalmArray time", finalTimeArray[item]);

                                                        mDatabase.child("app").child("users").child(UID).child("medicineLog").child(ParseDate).child(finalTimeArray[item]).removeValue();

                                                        // change medical conditon
                                                        Log.e("size", ""+ MedsList.size() +"   "+goalFreq );
                                                        doses = ((double) (MedsList.size()-1) / goalFreq) * 100;
                                                        Log.e("doses", ""+ doses );
                                                        if (doses >= 80){
                                                            mDatabase.child("app").child("users").child(UID).child("MedicalCondition").child(ParseDate).setValue("green");
                                                        } else {
                                                            mDatabase.child("app").child("users").child(UID).child("MedicalCondition").child(ParseDate).setValue("red");
                                                        }

                                                        medTimes.remove(item);
                                                        records.remove(item);
                                                        update();
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
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
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
