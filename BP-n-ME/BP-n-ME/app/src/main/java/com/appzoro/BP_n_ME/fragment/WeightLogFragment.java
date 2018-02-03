package com.appzoro.BP_n_ME.fragment;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.appzoro.BP_n_ME.activity.MainActivity;
import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;
import com.appzoro.BP_n_ME.util.util;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Appzoro_ 5 on 7/31/2017.
 */

public class WeightLogFragment extends Fragment implements View.OnClickListener {
    View view;
    Bundle bundle;
    ImageView img_back;
    EditText bodyweight;
    TextView submit;
    TimePicker timePicker;
    String weight, date, ParseDate, UID;
    MedasolPrefs prefs;
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_weightlog ,container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
        Listener();
        return view;
    }

    private void init() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        prefs = new MedasolPrefs(getActivity().getApplicationContext());
        UID = prefs.getUID();

        img_back = view.findViewById(R.id.iv_back);
        bodyweight = view.findViewById(R.id.weight);
        timePicker = view.findViewById(R.id.timePicker);
        submit = view.findViewById(R.id.tv_submit);

        bundle = this.getArguments();
        date = bundle.getString("date");
        //Log.e("date ",date);
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

    private void submit() {
        weight = bodyweight.getText().toString();

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
            } else{
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
            } else{
                time+=minute;
            }
        }
        //Log.e("time",time);

        Calendar calander = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String curtime = simpleDateFormat.format(calander.getTime());
        //Log.e("currentTime",curtime);

        if (!(curtime.compareTo(time)>=0)){
            Toast.makeText(getActivity(), "Please Enter your Weight Logs on current time!", Toast.LENGTH_SHORT).show();
        }
        else {
            if (bodyweight.length() > 0) {
                //Log.e("input date Log",date);
                SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date Date = inputFormat.parse(date);
                    ParseDate = outputFormat.format(Date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //Log.e("ParseDate",ParseDate);
                mDatabase.child("app").child("users").child(UID).child("weightLog").child(ParseDate).child(time).setValue(weight);

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
                        Toast.makeText(getActivity(), "Your weight '"+weight+"' pounds has been logged at " + "0" + HR + subKey1 + " PM ", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getActivity(), "Your weight '"+weight+"' pounds has been logged at " + HR + subKey1 + " PM ", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getActivity(), "Your weight '"+weight+"' pounds has been logged at " + time+ " AM ", Toast.LENGTH_LONG).show();
                }
                bodyweight.setText("");
                getFragmentManager().popBackStack();
            } else {
                Toast.makeText(getActivity(), "Please fill in the weight readings!", Toast.LENGTH_SHORT).show();
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
