package com.appzoro.BP_n_ME.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.appzoro.BP_n_ME.activity.PatientMonitor;
import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;
import com.appzoro.BP_n_ME.util.util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Appzoro_ 5 on 7/20/2017.
 */

public class SendCompileEmailFragment extends Fragment implements View.OnClickListener {
    View view;
    TextView sendEmail;
    String patientname, UID;
    MedasolPrefs prefs;
    private DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_sendcompileemail,container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getActivity().setTitle("Patient Monitor");
        ((PatientMonitor) getActivity()).getSupportActionBar().setSubtitle("Send Compile Email");
        init();
        Listener();
        return view;
    }

    private void init() {
        prefs = new MedasolPrefs(getActivity().getApplicationContext());
        UID = prefs.getUID();
        patientname = prefs.getUsername();
        sendEmail = view.findViewById(R.id.tv_sendEmail);
    }

    private void Listener() {
        sendEmail.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_sendEmail:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                final EditText editText = new EditText(getActivity());
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setBackgroundResource(R.drawable.edittext_border);
                editText.setPadding(20,12,12,12);
                editText.setHint("e.g. 5");

                builder.setView(editText,70,40,50,0)
                        .setTitle("Choose how many of the past days you'd like to see:")
                        .setCancelable(true)
                        .setPositiveButton("Create Email",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                try {
                                    prefs.changeDaystoSend(Integer.parseInt(editText.getText().toString()));
                                    getSevenDays(UID, patientname, "murnane_ks@mercer.edu");
                                }
                                catch (Exception e){}
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
        }
    }

    public void getSevenDays(String UID, final String patientname, final String sendTo){
        Log.e("in ","weekdata");

        mDatabase.child("app").child("users").child(UID).child("medicineLog").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int childCount = (int)dataSnapshot.getChildrenCount();
                        Log.e("childCount", String.valueOf(childCount));
                        DatabaseReference ref = dataSnapshot.getRef();
                        Query x;
                        int days = prefs.getDaystoSend();
                        if (childCount > 0) {
                            if (childCount < days) {
                                x = ref.limitToLast(childCount);
                            } else {
                                x = ref.limitToLast(days);
                                Log.e("days", Integer.toString(days));
                            }
                            x.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    ArrayList<String> dateArray = new ArrayList<String>();
                                    Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                                    ArrayList<ArrayList<String>> medications = new ArrayList<ArrayList<String>>();

                                    while (it.hasNext()) {
                                        DataSnapshot dates = it.next();
                                        String date = dates.getKey();
                                        //TODO: Compare dates
                                        if( setDateDiff(date,prefs.getDaystoSend())) {
                                            dateArray.add(dates.getKey());
                                            Iterator<DataSnapshot> ittwo = dates.getChildren().iterator();
                                            ArrayList<String> daysMeds = new ArrayList<String>();
                                            while (ittwo.hasNext()) {
                                                DataSnapshot medication = ittwo.next();
                                                daysMeds.add(medication.getValue().toString());
                                            }
                                            medications.add(daysMeds);
                                            Log.e("medication", String.valueOf(daysMeds.size()));
                                        }
                                    }
                                    prefs.setSevenDay(dateArray, medications);
                                    Log.e("medication", String.valueOf(medications.size()));
                                    createBody(patientname,sendTo);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }
                        else {
                            ArrayList<String> dates = new ArrayList<String>();
                            ArrayList<ArrayList<String>> medis = new ArrayList<ArrayList<String>>();
                            prefs.setSevenDay(dates, medis);
                            createBody(patientname,sendTo);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    public boolean setDateDiff(String date, int diff){
        Calendar calCurrent = Calendar.getInstance();
        calCurrent.setTimeInMillis(System.currentTimeMillis());
        Date dateCurrent = calCurrent.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date data = dateFormat.parse(date);
            Log.e("DATA",data.toString());
            Log.e("Current Date",dateCurrent.toString());
            long differ = dateCurrent.getTime() - data.getTime();
            Log.e("Long Differ",Long.toString(differ));
            int difference = (int) TimeUnit.DAYS.convert(differ, TimeUnit.MILLISECONDS);
            if(difference<=diff){Log.e("Days Diff",Integer.toString(difference));return true;}
            else{return false;}
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void createBody(String patientName, String sendTo){
        Log.e("in ","createBody");
        Pair<ArrayList<String>,ArrayList<ArrayList<String>>> weeksData = prefs.getWeek();
        ArrayList<String> daysTaken = weeksData.first;
        ArrayList<ArrayList<String>> medsTaken = weeksData.second;
        ArrayList<ArrayList<Integer>> medsDailyCount = new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> medFreqList = new ArrayList<>();

        String medsThisWeek = "User: "+patientName+" has taken their medication "+Integer.toString(daysTaken.size())+"/"+Integer.toString(prefs.getDaystoSend())+" days in this time frame.\n\r\n\r";
        medsThisWeek+= "Medications: \n\r";

        List<String> medsList =  new ArrayList<String>(Arrays.asList(prefs.getMeds().replace("[","").replace("]","").replace(" ","").split(",")));
        List<String> FreqList =  new ArrayList<String>(Arrays.asList(prefs.getFreqList().replace("[","").replace("]","").split(",")));

        Log.e("Body so far",medsThisWeek);

        for(int i=0; i < FreqList.size();i++){
            medsThisWeek+= medsList.get(i)+": " + FreqList.get(i)+"\n\r";
        }

        Log.e("My Freq List", String.valueOf(FreqList));

        for(int i=0;i<FreqList.size();i++) {
            Log.e("Size",Integer.toString(FreqList.size()));
            Log.e("I",Integer.toString(i));

            if(FreqList.get(i).contains("Twice daily")){
                medFreqList.add(i,2);
            }else if(FreqList.get(i).contains("Daily")){
                medFreqList.add(i,1);
            }else if(FreqList.get(i).contains("Three times daily")){
                medFreqList.add(i,3);
            }
        }

        int totalTaken=0;
        for(int i=0; i<medsList.size();i++){
            ArrayList<Integer> count = new ArrayList<>();
            for(int h = 0; h<daysTaken.size();h++){
                int num = util.countMatches(medsTaken.get(h).toString(), (String) medsList.get(i));
                count.add(num);
                totalTaken+=num;
            }
            medsDailyCount.add(count);
        }

        medsThisWeek+="\n\r\n\r";
        for(int i=0;i<daysTaken.size();i++){
            medsThisWeek+= daysTaken.get(i) + ":  \n\r";
            for(int h=0; h < medsList.size(); h++) {
                medsThisWeek += medsList.get(h) + ": " + medsDailyCount.get(h).get(i)+"/"+medFreqList.get(h) + "\n\r";
            }
            medsThisWeek+="\n\r";
        }
        int totalNeeded=0;

        medsThisWeek += "Over this time period, "+patientName+" has taken \n\r";
        for(int h=0; h < medsList.size();h++) {
            int weekCount =0;
            medsThisWeek += medsList.get(h)+" ";
            for(int i=0;i<medsDailyCount.get(h).size();i++) {
                if(medsDailyCount.get(h).get(i)>0){
                    weekCount++;
                }
            }
            medsThisWeek+= Integer.toString(weekCount)+"/"+Integer.toString(prefs.getDaystoSend())+" days\n\r";
            totalNeeded+=prefs.getDaystoSend()*medFreqList.get(h);
        }

        Log.e("Total Taken",Integer.toString(totalTaken));
        Log.e("Total Needed",Integer.toString(totalNeeded));
        String adherence;
        if(((float)totalTaken/totalNeeded)>=.8){
            adherence = "Medication Adherent.";
        }
        else{
            adherence = "Not Medication Adherent.";
        }

        medsThisWeek+="\n\r"+patientName+" medication adherence ratio is "+Integer.toString(totalTaken)+"/"+Integer.toString(totalNeeded)+" and is therefore "+adherence;
        medsThisWeek+="\n\r\n\rMedAdhereSolutions Team";
        Log.e("Meds This Week",medsThisWeek);

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Patient Update: "+patientName);
        intent.putExtra(Intent.EXTRA_TEXT, medsThisWeek);
        intent.setData(Uri.parse("mailto:"+sendTo)); // or just "mailto:" for blank
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
        startActivity(intent);
    }
}
