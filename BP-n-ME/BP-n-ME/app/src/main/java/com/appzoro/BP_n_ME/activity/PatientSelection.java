package com.appzoro.BP_n_ME.activity;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.adapter.PatientAdapter;
import com.appzoro.BP_n_ME.model.Patient;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;
import com.appzoro.BP_n_ME.util.Progress_dialog;
import com.appzoro.BP_n_ME.util.ReminderService;
import com.appzoro.BP_n_ME.util.util;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.splunk.mint.Mint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PatientSelection extends AppCompatActivity implements View.OnClickListener {
    EditText search;
    ListView pList;
    ImageView switch_List;
    PatientAdapter patientAdapter;
    FloatingActionMenu floatingActionMenu;
    FloatingActionButton newPatient;
    ArrayList<String> uidlist = new ArrayList<String>();
    ArrayList<String> timestampList = new ArrayList<String>();
    ArrayList<String> patientlist = new ArrayList<String>();
    final ArrayList<String> updateUidlist = new ArrayList<>();
    final ArrayList<String> surveyUidlist = new ArrayList<>();
    final ArrayList<String> BpUidlist = new ArrayList<>();
    final ArrayList<String> greenDateList = new ArrayList<>();
    final ArrayList<String> greenUidList = new ArrayList<>();
    final ArrayList<String> Bplist = new ArrayList<>();
    final ArrayList<String> BpGoallist = new ArrayList<>();
    ArrayList<Patient> patientArrayList = new ArrayList<>();
    MedasolPrefs prefs;
    private Progress_dialog progress_d;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    Date regDate, dischargedate;
    String bpOnly;
    private boolean switched = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_selection);
        getSupportActionBar().setTitle("Patient Selection");
        Mint.initAndStartSession(this.getApplication(), "9bb93c94");
        init();
        Listener();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(PatientSelection.this, SigninActivity.class));
                    finish();
                }
            }
        };
        progress_d = Progress_dialog.getInstance();
        prefs = new MedasolPrefs(getApplicationContext());
        if (util.isInternetOn(this)) {
            uidList();
        } else {
            Toast.makeText(this, "We can't detect network connectivity, connect to the internet and try again.", Toast.LENGTH_SHORT).show();
        }

    }

    private void init() {
        MainActivity.notification_lafda = 0;
        search = (EditText) findViewById(R.id.search);
        pList = (ListView) findViewById(R.id.patientList);
        //switch_List = findViewById(R.id.switch_List);
        floatingActionMenu = (FloatingActionMenu) findViewById(R.id.floatingactionmenu);
        newPatient = (FloatingActionButton) findViewById(R.id.registerpatient);

        Intent myIntent = new Intent(PatientSelection.this, ReminderService.class);
        boolean isWorking = (PendingIntent.getBroadcast(PatientSelection.this, 0, myIntent, PendingIntent.FLAG_NO_CREATE) != null);
        if (isWorking) {
            Log.d("alarm", "is working");
        } else {
            Log.d("alarm", "is not working");
        }
    }

    private void Listener() {
        newPatient.setOnClickListener(this);
        //switch_List.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.registerpatient:
                Intent i = new Intent(PatientSelection.this, SignUpActivity.class);
                startActivity(i);
                break;
            /*case R.id.switch_List:
                //Collections.reverse(patientArrayList);
                patientAdapter = new PatientAdapter(this, patientArrayList);
                pList.setAdapter(patientAdapter);
                if (switched){
                    switch_List.setImageResource(R.drawable.switch2);
                    switched = false;
                } else {
                    switch_List.setImageResource(R.drawable.switch1);
                    switched = true;
                }
                break;*/
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                new AlertDialog.Builder(this).setTitle("Logout")
                        .setMessage("Are you sure you want to Logout?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                progress_d.showProgress(PatientSelection.this);
                                prefs.clear();
                                mAuth.signOut();
                            }
                        }).setNegativeButton("no", null).show();
                return true;
            case R.id.sort:
                //Collections.reverse(patientArrayList);
                patientAdapter = new PatientAdapter(this, patientArrayList);
                pList.setAdapter(patientAdapter);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void uidList() {
        mDatabase.child("storage").child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                uidlist.clear();
                Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                while (it.hasNext()) {
                    DataSnapshot uid = it.next();
                    Log.e("uid", uid.getKey());
                    uidlist.add(uid.getKey());
                }
                progress_d.showProgress(PatientSelection.this);
                Log.e("uid List", "uid" + String.valueOf(uidlist.size()));
                //getDischargeDate(uidlist);
                //getSymptomSurveyAns(uidlist);
                //getPatientList(uidlist);
                getTimestamp(uidlist);
                prefs.setUIDList(uidlist);
                getPatientList(uidlist);

                if (uidlist.isEmpty()){
                    progress_d.hideProgress();
                    Toast.makeText(PatientSelection.this, "No Record Found!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getTimestamp(final ArrayList<String> uidList) {
        for (int i = 0; i < uidList.size(); i++) {
            mDatabase.child("storage").child("users").child(uidList.get(i)).child("timestamp").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String timestamp = dataSnapshot.getValue().toString();
                            Log.e("timestamp", timestamp);
                            timestampList.add(timestamp);
                            prefs.setTimestampList(timestampList);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
        }
    }

   /* public void getDischargeDate(final ArrayList<String> uidList) {
        for (int i = 0; i < uidList.size(); i++) {
            final int finalI = i;
            mDatabase.child("storage").child("users").child(uidList.get(i)).child("dischargedate").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String disdate = dataSnapshot.getValue().toString();
                            Log.e("Discharge date", "" + disdate);
                            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                            try {
                                dischargedate = format.parse(disdate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Date currentDate = new Date(System.currentTimeMillis());
                            //Log.e("currentDate ", ""+ currentDate);
                            Date oldDateTime = new Date(1, 1, 2010);
                            //Log.e("oldDate", ""+ oldDateTime);
                            //Log.e("Discharge date", ""+dischargedate);
                            int one = (int) getDifferenceDays(oldDateTime, currentDate);
                            int two = (int) getDifferenceDays(oldDateTime, dischargedate);
                            int daysSince = one - two;
                            //Log.e("daysSince", ""+ daysSince);
                            if (daysSince < 31) {
                                updateUidlist.add(uidList.get(finalI));
                            }
                            if (finalI == uidList.size()-1) {
                                //getSymptomSurveyAns(updateUidlist);
                                //getPatientList(updateUidlist);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
        }
    }*/

    public static long getDifferenceDays(Date d1, Date d2) {
        //Log.e("d1", "" + d1);
        //Log.e("d2", "" + d2);
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    /*public void getSymptomSurveyAns(final ArrayList<String> uidList) {
        for (int i = 0; i < uidList.size(); i++) {
            final int finalI = i;
            mDatabase.child("app").child("users").child(uidList.get(i)).child("symptomsurveyanswers").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String lifedate = "";
                            String attempts = "";
                            Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                            while (it.hasNext()) {
                                DataSnapshot medicine = it.next();
                                lifedate = medicine.getKey();
                                attempts = medicine.getValue().toString();
                                Log.e("attempts", lifedate + attempts);
                            }
                            if (attempts.equals("[1, 1, 1, 1, 1, 1, 1]")) {
                                Log.e("attempts", lifedate + attempts + " - " + uidList.get(finalI));
                                if (!surveyUidlist.contains(uidList.get(finalI)))
                                    surveyUidlist.add(uidList.get(finalI));       // All No
                            }
                            if (finalI == uidList.size() - 1) {
                                //Log.e("surveyUidlist size"," "+ surveyUidlist.size());
                                getBPGoal(surveyUidlist, uidList);
                            }
                            if (surveyUidlist.isEmpty()){
                                prefs.setUIDList(uidList);
                                getPatientList(uidList);
                                progress_d.hideProgress();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
        }
    }*/

    /*public void getBPGoal(final ArrayList<String> uidList, final ArrayList<String> updateUidlist) {
        for (int i = 0; i < uidList.size(); i++) {
            final int finalI = i;
            mDatabase.child("app").child("users").child(uidList.get(i)).child("bloodpressuregoal").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String bloodpressuregoal = (String) dataSnapshot.getValue();
                            BpGoallist.add(bloodpressuregoal);
                            if (finalI == uidList.size() - 1) {
                                getBplog(surveyUidlist, BpGoallist, updateUidlist);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        }
                    });
        }
    }*/

    /*public void getBplog(final ArrayList<String> uidList, final ArrayList<String> BpGoalList, final ArrayList<String> updateUidlist) {
        for (int i = 0; i < uidList.size(); i++) {
            final int finalI = i;
            mDatabase.child("app").child("users").child(uidList.get(i)).child("bloodPressureLog").addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                            while (it.hasNext()) {
                                DataSnapshot medicine = it.next();
                                String bpDate = medicine.getKey();
                                String bp = medicine.getValue().toString();
                                //Log.e("BP Date", bpDate);
                                //Log.e("Blood Pressure", "bp"+bp);
                                List<String> dates = Arrays.asList(bp.replace("{", "").replace("}", "").split(", "));
                                //Log.e("BP", dates.toString());
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
                                Bplist.add(bpOnly);
                            }
                            try {
                                String bpGoal = BpGoalList.get(finalI).replace("Less than ", "");
                                Log.e("BP", bpOnly + "  " + bpGoal);
                                String systolicGoal = bpGoal.substring(0, 3);
                                String diastolicGoal = bpGoal.substring(4, 6);

                                String systolic = bpOnly.split("/")[0];
                                String diastolic = bpOnly.split("/")[1];

                                if (Integer.parseInt(systolic) > Integer.parseInt(systolicGoal) || Integer.parseInt(diastolic) > Integer.parseInt(diastolicGoal)) {
                                } else {
                                    if (!BpUidlist.contains(uidList.get(finalI))) {
                                        BpUidlist.add(uidList.get(finalI));
                                    }
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            if (finalI == uidList.size() - 1) {
                                //getRegistrationDate(BpUidlist, updateUidlist);

                                Log.e("update uid size", "" + updateUidlist.size());
                                prefs.setUIDList(updateUidlist);
                                getPatientList(updateUidlist);
                                prefs.setOkUidList(BpUidlist);
                            }
                            if (BpUidlist.isEmpty()){
                                prefs.setUIDList(uidList);
                                getPatientList(uidList);
                                progress_d.hideProgress();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
        }
    }*/

    /*public void getRegistrationDate(final ArrayList<String> uidList, final ArrayList<String> updateUidlist) {
        final ArrayList<String> regDateList = new ArrayList<>();
        for (int i = 0; i < uidList.size(); i++) {
            final int finalI = i;
            mDatabase.child("storage").child("users").child(uidList.get(i)).child("registrationdate").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String regdate = dataSnapshot.getValue().toString();
                            //Log.e("Register date", regdate);
                            regDateList.add(regdate);
                            if (finalI == uidList.size() - 1) {
                                getDischargeDate1(uidList, regDateList, updateUidlist);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        }
                    });
        }
    }*/

    /*public void getDischargeDate1(final ArrayList<String> uidList, final ArrayList<String> regDateList, final ArrayList<String> updateUidlist) {
        final ArrayList<Integer> totalDaysList = new ArrayList<>();
        for (int i = 0; i < uidList.size(); i++) {
            final int finalI = i;
            mDatabase.child("storage").child("users").child(uidList.get(i)).child("dischargedate").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String disdate = dataSnapshot.getValue().toString();
                            //Log.e("Discharge date", ""+disdate);
                            //Log.e("register date", ""+regDateList.get(finalI));
                            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                dischargedate = format.parse(disdate);
                                regDate = format1.parse(regDateList.get(finalI));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            //Log.e("date", "dis: "+dischargedate+ " reg: "+regDate);
                            Date oldDateTime = new Date(1, 1, 2010);
                            //Log.e("oldDate", ""+ oldDateTime);
                            int one = (int) getDifferenceDays(oldDateTime, regDate);
                            int two = (int) getDifferenceDays(oldDateTime, dischargedate);
                            int daysSince = two - one;
                            Log.e("daysSince", "" + daysSince + " " + uidList.get(finalI));
                            totalDaysList.add(daysSince);
                            if (finalI == uidList.size() - 1) {
                                getMedicalCondition(BpUidlist, totalDaysList, updateUidlist);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
        }
    }*/

    /*public void getMedicalCondition(final ArrayList<String> uidList, final ArrayList<Integer> totalDaysList, final ArrayList<String> updateUidlist) {
        //Log.e("days size", ""+ totalDaysList.size());
        for (int i = 0; i < uidList.size(); i++) {
            final int finalI = i;
            mDatabase.child("app").child("users").child(uidList.get(i)).child("MedicalCondition").addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            greenDateList.clear();
                            Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                            while (it.hasNext()) {
                                DataSnapshot snapshot = it.next();
                                String date = snapshot.getKey();
                                String condition = snapshot.getValue().toString();
                                //Log.e("condition",date+" "+condition);
                                if (condition.equals("green")) {
                                    Log.e("Ok condition", date + " " + condition + "  " + uidList.get(finalI) + "  " + totalDaysList.get(finalI));
                                    greenDateList.add(date);
                                }
                            }
                            Log.e(">>>>>>", "size " + greenDateList.size() + " total days " + totalDaysList.get(finalI));
                            try {
                                double doses = ((double) greenDateList.size() / totalDaysList.get(finalI)) * 100;
                                Log.e("doses", "" + doses);
                                if (doses >= 80) {
                                    greenUidList.add(uidList.get(finalI));
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            if (finalI == uidList.size() - 1) {
                                Log.e("update uid size", "" + updateUidlist.size());
                                prefs.setUIDList(updateUidlist);
                                getPatientList(updateUidlist);
                                prefs.setOkUidList(greenUidList);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        }
                    });
        }
    }*/

    public void getPatientList(final ArrayList<String> uidList) {
        patientlist.clear();
        for (int i = 0; i < uidList.size(); i++) {
            final int finalI = i;
            mDatabase.child("storage").child("users").child(uidList.get(i)).child("email").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int emailIndex = dataSnapshot.getValue().toString().lastIndexOf("@");
                            String patientname = dataSnapshot.getValue().toString().substring(0, emailIndex);
                            patientlist.add(patientname);
                            prefs.setNameList(patientlist);
                            Log.e("patient name", patientname);
                            if (finalI == uidList.size() - 1) {
                                addPatient();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
        }
    }

    private void addPatient() {
        final List<String> patientList = new ArrayList<String>(Arrays.asList(prefs.getNameList().replace("[", "").replace("]", "").split(", ")));
        List<String> UIDList = prefs.getUIDList();
        List<String> timestampList = prefs.getTimestampList();
        Log.e("patientList size", "patient " + String.valueOf(patientList.size()) + "-" + UIDList.size() + "-" + timestampList.size());


        final ArrayList<String> okPatientList = new ArrayList<>(Arrays.asList(prefs.getOkUidList().replace("[", "").replace("]", "").split(", ")));
        Log.e("okPatientList size", "patient " + String.valueOf(okPatientList.size()));

        // ArrayList<Patient> patientArrayList = new ArrayList<>();
        patientArrayList.clear();
        for (int i = 0; i < patientlist.size(); i++) {
            Patient p = new Patient();
            p.setName(patientlist.get(i));
            p.setId(UIDList.get(i));
            p.setTimestamp(timestampList.get(i));
            patientArrayList.add(p);
        }

        Collections.sort(patientArrayList, new Comparator<Patient>() {
            @Override
            public int compare(Patient patient, Patient p1) {
                return patient.getTimestamp().compareToIgnoreCase(p1.getTimestamp());
            }
        });

        for (int k = 0; k < patientArrayList.size(); k++) {
            patientArrayList.get(k).setColor("red");
        }

        for (int i = 0; i < patientArrayList.size(); i++) {
            for (int j = 0; j < okPatientList.size(); j++) {
                if (patientArrayList.get(i).getId().trim().equals(okPatientList.get(j).trim())) {
                    patientArrayList.get(i).setColor("green");                                      // Green color(Patient Ok)
                    Log.e("Ok Patient uid ", patientArrayList.get(i).getId() + "  " + okPatientList.get(j));
                    break;
                }
            }
        }

        patientAdapter = new PatientAdapter(this, patientArrayList);
        pList.setAdapter(patientAdapter);
        progress_d.hideProgress();

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = search.getText().toString().toLowerCase(Locale.getDefault());
                patientAdapter.filter(text);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
        //progress_d.hideProgress();
        stopService(new Intent(getApplicationContext(), ReminderService.class));
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        if (floatingActionMenu.isOpened())
            floatingActionMenu.close(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            new AlertDialog.Builder(this).setTitle("Exit App")
                    .setMessage("Are you sure you want to quit this App?")
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }).setNegativeButton("no", null).show();
        }
    }
}
