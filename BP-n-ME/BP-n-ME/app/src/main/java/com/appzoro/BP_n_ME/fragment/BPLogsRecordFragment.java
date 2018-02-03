package com.appzoro.BP_n_ME.fragment;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.appzoro.BP_n_ME.activity.MainActivity;
import com.appzoro.BP_n_ME.adapter.BPAdapter;
import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;
import com.appzoro.BP_n_ME.util.Progress_dialog;
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

public class BPLogsRecordFragment extends Fragment implements View.OnClickListener {
    View view;
    ImageView img_back;
    FloatingActionButton backToCal, addinput;
    ListView bpList;
    BPAdapter adapter;
    Boolean past = false;
    int daysSince;
    String date, ParseDate, UID;
    MedasolPrefs prefs;
    private Progress_dialog progress_d;
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_bplogsrecord, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
        Listener();
        return view;
    }

    private void init() {
        progress_d = Progress_dialog.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        prefs = new MedasolPrefs(getActivity().getApplicationContext());
        UID = prefs.getUID();

        Bundle bundle = this.getArguments();
        date = bundle.getString("date");
        daysSince = bundle.getInt("daysSince");

        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date Date = inputFormat.parse(date);
            ParseDate = outputFormat.format(Date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        img_back = view.findViewById(R.id.iv_back);
        bpList = view.findViewById(R.id.bpListView);
        backToCal = view.findViewById(R.id.backToCal);
        addinput = view.findViewById(R.id.inputLog);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        SimpleDateFormat formatter = new SimpleDateFormat("mm/dd/yyyy");
        try {
            Date clicked = formatter.parse(date);
            Date today = new Date();
            if ((int) getDifferenceDays(clicked, today) > 0) {
                past = true;
            } else {
                past = false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (daysSince == 0) {
            backToCal.setVisibility(View.GONE);
            addinput.setVisibility(View.VISIBLE);
        } else {
            backToCal.setVisibility(View.VISIBLE);
            addinput.setVisibility(View.GONE);
        }

        updateHeartRate();
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
                getFragmentManager().beginTransaction().replace
                        (R.id.Fragment_frame_main_activity, new BloodpressureFragment()).commit();
                break;
            case R.id.backToCal:
                getFragmentManager().beginTransaction().replace
                        (R.id.Fragment_frame_main_activity, new BloodpressureFragment()).commit();
                break;
            case R.id.inputLog:
                BPLogFragment fragment = new BPLogFragment();
                Bundle bundle = new Bundle();
                bundle.putString("date", date);
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace
                        (R.id.Fragment_frame_main_activity, fragment).addToBackStack("BPLogFragment").commit();
                break;
        }
    }

    public static long getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public void updateHeartRate() {
        //progress_d.showProgress(getActivity());
        final ArrayList<String> heartLogs = new ArrayList<>();
        mDatabase.child("app").child("users").child(UID).child("HeartRateLog").child(ParseDate).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        while (it.hasNext()) {
                            DataSnapshot heartrate = it.next();
                            String heartRate = heartrate.getValue().toString().trim();
                            heartLogs.add(heartRate);
                            prefs.setHeartRate(heartLogs);
                            update();
                        }
                        //progress_d.hideProgress();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );

    }

    public void update() {
        final List<String> HeartRates = new ArrayList<String>(Arrays.asList(prefs.getHeartRate().replace("[", "").replace("]", "").replace(" ", "").split(",")));

        mDatabase.child("app").child("users").child(UID).child("bloodPressureLog").child(ParseDate).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final ArrayList<String> records = new ArrayList<>();
                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        final ArrayList<String> medTimes = new ArrayList<String>();
                        while (it.hasNext()) {
                            DataSnapshot medicine = it.next();
                            String MedTime = medicine.getKey();
                            medTimes.add(MedTime);

                            int colonNdx = MedTime.indexOf(":");
                            String subKey = MedTime.substring(0, colonNdx);
                            int HR = Integer.parseInt(subKey);

                            if (HR >= 12) {
                                if (HR > 12) {
                                    HR = HR - 12;
                                }
                                if (HR < 10) {
                                    records.add("0" + HR + medicine.getKey().substring(colonNdx, colonNdx + 3) + "PM   " + medicine.getValue().toString());
                                } else {
                                    records.add(HR + medicine.getKey().substring(colonNdx, colonNdx + 3) + "PM   " + medicine.getValue().toString());
                                }
                            } else {
                                records.add(medicine.getKey() + "AM   " + medicine.getValue().toString());
                            }
                        }

                        String[] timeArray = new String[medTimes.size()];
                        timeArray = medTimes.toArray(timeArray);

                        String[] mArray = new String[records.size()];
                        mArray = records.toArray(mArray);

                        try {
                            adapter = new BPAdapter(getActivity(), mArray, date, HeartRates, daysSince);
                            bpList.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        final String[] finalMArray = mArray;
                        final String[] finalTimeArray = timeArray;

                        //if (past) {
                        if(daysSince == 0) {
                            bpList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, final View view, final int item, long id) {
                                    long viewId = view.getId();

                                    if (viewId == R.id.iv_delete) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                        builder.setTitle("Delete Blood Pressure Log");
                                        // set dialog message
                                        builder
                                                .setMessage("Would you like to delete?")
                                                .setCancelable(true)
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        Log.e("FinalmArray", finalMArray[item]);
                                                        Log.e("FinalmArray time", finalTimeArray[item]);

                                                        mDatabase.child("app").child("users").child(UID).child("bloodPressureLog").child(ParseDate).child(finalTimeArray[item]).removeValue();
                                                        mDatabase.child("app").child("users").child(UID).child("HeartRateLog").child(ParseDate).child(finalTimeArray[item]).removeValue();

                                                        bpList.setAdapter(null);
                                                        updateHeartRate();
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
        ((MainActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((MainActivity) getActivity()).getSupportActionBar().show();
    }
}
