package com.appzoro.BP_n_ME.activity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.adapter.NavigationMenuAdapter;
import com.appzoro.BP_n_ME.fragment.BPFragment;
import com.appzoro.BP_n_ME.fragment.DiagnosisFragment;
import com.appzoro.BP_n_ME.fragment.MedicationFragment;
import com.appzoro.BP_n_ME.fragment.PatientInformationFragment;
import com.appzoro.BP_n_ME.fragment.SurveyRecordFragment;
import com.appzoro.BP_n_ME.fragment.TakenMedicationRecordFragment;
import com.appzoro.BP_n_ME.model.NavigationMenuItems;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;
import com.appzoro.BP_n_ME.util.Progress_dialog;
import com.appzoro.BP_n_ME.util.ReminderService;
import com.appzoro.BP_n_ME.util.util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.splunk.mint.Mint;

import org.apache.commons.lang3.StringUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PatientMonitor extends AppCompatActivity implements AdapterView.OnItemClickListener {
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    ListView menuList;
    NavigationMenuAdapter mlistAdapter;
    List<NavigationMenuItems> listDataItem;
    private Progress_dialog progress_d;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    MedasolPrefs prefs;
    String UID;
    String patientname;
    int i;
    int psize;
    private String MED_FILENAME = "med_file";
    private String FREQ_FILENAME = "freq_file";
    ArrayList<String> medArray = new ArrayList<String>();
    ArrayList<String> medFrequency = new ArrayList<String>();
    ArrayList<ArrayList<String>> diagAllPatients = new ArrayList<ArrayList<String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_monitor);
        Mint.initAndStartSession(this.getApplication(), "9bb93c94");
        init();
        setupNavigationDrawer();
        Listener();
        getPatientName();
        getRegisterDate();
        getEnrollmentDate();
        //getDischargeDate();
        //findLifestyleFeedback();
        //findLiteracyFeedback();
        getBPGoal();
        getBplog();
        getBwlog();
        getHeartRatelog();
        getClinicName();
        getPharmacy();
        getphysician();
        getDiag(i, UID, psize);
        getMeds();
    }

    private void init() {
        progress_d = Progress_dialog.getInstance();
        prefs = new MedasolPrefs(getApplicationContext());
        UID = prefs.getUID();
        patientname = prefs.getUsername();
        Log.e("UID", "Patient id : " + UID);

        /*getSupportFragmentManager().beginTransaction().replace
                (R.id.Fragment_frame1, new PatientInformationFragment()).commit();*/

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(PatientMonitor.this, SigninActivity.class));
                    finish();
                }
            }
        };

        Intent myIntent = new Intent(PatientMonitor.this, ReminderService.class);
        boolean isWorking = (PendingIntent.getBroadcast(PatientMonitor.this, 0, myIntent, PendingIntent.FLAG_NO_CREATE) != null);
        if (isWorking) {
            Log.d("alarm", "is working");
        } else {
            Log.d("alarm", "is not working");
        }
    }

    private void setupNavigationDrawer() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.menu25, null);
                toolbar.setNavigationIcon(d);
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            // Called when a drawer has settled in a completely closed state.
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                toolbar.post(new Runnable() {
                    @Override
                    public void run() {
                        Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.menu25, null);
                        toolbar.setNavigationIcon(d);
                    }
                });
            }

            // Called when a drawer has settled in a completely open state.
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                toolbar.post(new Runnable() {
                    @Override
                    public void run() {
                        Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_arrow_back, null);
                        toolbar.setNavigationIcon(d);
                    }
                });
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);
        menuList = (ListView) findViewById(R.id.navigationmenulist);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
        prepareListData();
        mlistAdapter = new NavigationMenuAdapter(this, listDataItem);
        menuList.setAdapter(mlistAdapter);

        //Nav_header
        View header_View = navigationView.getHeaderView(0);
        TextView Drname = header_View.findViewById(R.id.tv_username);
        Drname.setText(StringUtils.capitalize(prefs.getDrName().toLowerCase().trim()));
    }

    private void Listener() {
        menuList.setOnItemClickListener(this);
    }

    private void prepareListData() {
        listDataItem = new ArrayList<>();

        // Adding data Items
        NavigationMenuItems item1 = new NavigationMenuItems();
        item1.setItemName("Patient Information");
        item1.setItemImg(R.drawable.patient_registration);
        listDataItem.add(item1);

        NavigationMenuItems item2 = new NavigationMenuItems();
        item2.setItemName("Prescribed Medication");
        item2.setItemImg(R.drawable.meds);
        listDataItem.add(item2);

        NavigationMenuItems item3 = new NavigationMenuItems();
        item3.setItemName("Diagnosis");
        item3.setItemImg(R.drawable.diag);
        listDataItem.add(item3);

        NavigationMenuItems item4 = new NavigationMenuItems();
        item4.setItemName("Blood Presure Record");
        item4.setItemImg(R.drawable.bp);
        listDataItem.add(item4);

        /*NavigationMenuItems item5 = new NavigationMenuItems();
        item5.setItemName("Body Weight Record");
        item5.setItemImg(R.drawable.bw);
        listDataItem.add(item5);*/

        NavigationMenuItems item6 = new NavigationMenuItems();
        item6.setItemImg(R.drawable.meds);
        item6.setItemName("Medication Utilization Log");//Taken Medication Record
        listDataItem.add(item6);

        NavigationMenuItems item7 = new NavigationMenuItems();
        item7.setItemName("Survey Record");
        item7.setItemImg(R.drawable.survey);
        listDataItem.add(item7);

        NavigationMenuItems item8 = new NavigationMenuItems();
        item8.setItemImg(R.drawable.email1);
        item8.setItemName("Compile and Send");
        listDataItem.add(item8);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (position == 0) {
            getSupportFragmentManager().beginTransaction().replace
                    (R.id.Fragment_frame1, new PatientInformationFragment()).commit();
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if (position == 1) {
            getSupportFragmentManager().beginTransaction().replace
                    (R.id.Fragment_frame1, new MedicationFragment()).commit();
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if (position == 2) {
            getSupportFragmentManager().beginTransaction().replace
                    (R.id.Fragment_frame1, new DiagnosisFragment()).commit();
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if (position == 3) {
            getSupportFragmentManager().beginTransaction().replace
                    (R.id.Fragment_frame1, new BPFragment()).commit();
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        /*else if (position == 4) {
            getSupportFragmentManager().beginTransaction().replace
                    (R.id.Fragment_frame1, new BWFragment()).commit();
            drawerLayout.closeDrawer(GravityCompat.START);
        } */
        else if (position == 4) {
            getSupportFragmentManager().beginTransaction().replace
                    (R.id.Fragment_frame1, new TakenMedicationRecordFragment()).commit();
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if (position == 5) {
            getSupportFragmentManager().beginTransaction().replace
                    (R.id.Fragment_frame1, new SurveyRecordFragment()).commit();
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if (position == 6) {
               /*getSupportFragmentManager().beginTransaction().replace
                        (R.id.Fragment_frame1, new SendCompileEmailFragment()).commit();*/
            SendCompileEmail();
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                prefs.setBpValue("");
                prefs.setHeartRateValue("");
                prefs.setBwValue("");
                *//*prefs.setUID("");*//*
                Intent intent = new Intent(this, PatientSelection.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

    @SuppressLint("RestrictedApi")
    private void SendCompileEmail() {
        patientname = prefs.getUsername();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setBackgroundResource(R.drawable.edittext_border);
        editText.setPadding(20, 12, 12, 12);
        editText.setHint("e.g. '5'");
        editText.setHintTextColor(Color.GRAY);

        builder.setView(editText, 70, 40, 50, 0)
                .setTitle("Choose how many of the past days you'd like to send:")
                .setCancelable(true)
                .setPositiveButton("Create Email", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (!editText.getText().toString().trim().equals("") && !editText.getText().toString().trim().equals("0")) {
                            try {
                                prefs.changeDaystoSend(Integer.parseInt(editText.getText().toString()));
                                getSevenDays(UID, patientname, "murnane_ks@mercer.edu");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(PatientMonitor.this, "Please enter valid no. of days!", Toast.LENGTH_SHORT).show();
                        }
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void getSevenDays(String UID, final String patientname, final String sendTo) {
        //Log.e("in ","weekdata");
        mDatabase.child("app").child("users").child(UID).child("medicineLog").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int childCount = (int) dataSnapshot.getChildrenCount();
                        //Log.e("childCount", String.valueOf(childCount));
                        DatabaseReference ref = dataSnapshot.getRef();
                        Query x;
                        int days = prefs.getDaystoSend();
                        if (childCount > 0) {
                            if (childCount < days) {
                                x = ref.limitToLast(childCount);
                            } else {
                                x = ref.limitToLast(days);
                                //Log.e("days", Integer.toString(days));
                            }
                            x.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    ArrayList<String> dateArray = new ArrayList<String>();
                                    Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                                    ArrayList<ArrayList<String>> medications = new ArrayList<ArrayList<String>>();

                                    while (it.hasNext()) {
                                        DataSnapshot dates = it.next();
                                        String date = dates.getKey().toString();
                                        //TODO: Compare dates
                                        if (setDateDiff(date, prefs.getDaystoSend())) {
                                            dateArray.add(dates.getKey().toString());
                                            Iterator<DataSnapshot> ittwo = dates.getChildren().iterator();
                                            ArrayList<String> daysMeds = new ArrayList<String>();
                                            while (ittwo.hasNext()) {
                                                DataSnapshot medication = ittwo.next();
                                                daysMeds.add(medication.getValue().toString());
                                            }
                                            medications.add(daysMeds);
                                            //Log.e("medication", String.valueOf(daysMeds.size()));
                                        }
                                    }
                                    prefs.setSevenDay(dateArray, medications);
                                    //Log.e("medication", String.valueOf(medications.size()));
                                    createBody(patientname, sendTo);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        } else {
                            ArrayList<String> dates = new ArrayList<String>();
                            ArrayList<ArrayList<String>> medis = new ArrayList<ArrayList<String>>();
                            prefs.setSevenDay(dates, medis);
                            createBody(patientname, sendTo);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    public boolean setDateDiff(String date, int diff) {
        Calendar calCurrent = Calendar.getInstance();
        calCurrent.setTimeInMillis(System.currentTimeMillis());
        Date dateCurrent = calCurrent.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date data = dateFormat.parse(date);
            //Log.e("DATA",data.toString());
            // Log.e("Current Date",dateCurrent.toString());
            long differ = dateCurrent.getTime() - data.getTime();
            //Log.e("Long Differ",Long.toString(differ));
            int difference = (int) TimeUnit.DAYS.convert(differ, TimeUnit.MILLISECONDS);
            if (difference <= diff) {
                //Log.e("Days Diff",Integer.toString(difference));
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void createBody(String patientName, String sendTo) {
        Log.e("in ", "createBody");
        Log.e("patientName", patientName);

        Pair<ArrayList<String>, ArrayList<ArrayList<String>>> weeksData = prefs.getWeek();
        ArrayList<String> daysTaken = weeksData.first;
        ArrayList<ArrayList<String>> medsTaken = weeksData.second;
        ArrayList<ArrayList<Integer>> medsDailyCount = new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> medFreqList = new ArrayList<>();

        String medsThisWeek = "User: " + patientName + " has taken their medication " + Integer.toString(daysTaken.size()) + "/" + Integer.toString(prefs.getDaystoSend()) + " days in this time frame.\n\r\n\r";
        medsThisWeek += "Medications: \n\r";

        List<String> medsList = new ArrayList<String>(Arrays.asList(prefs.getMeds().replace("[", "").replace("]", "").replace(" ", "").split(",")));
        List<String> FreqList = new ArrayList<String>(Arrays.asList(prefs.getFreqList().replace("[", "").replace("]", "").split(",")));

        //Log.e("Body so far",medsThisWeek);
        //Log.e("mSize", String.valueOf(medsList.size()));
        //Log.e("fSize", String.valueOf(FreqList.size()));
        String medName = Arrays.asList(prefs.getMeds().replace("[", "").replace("]", "").replace(" ", "").split(",")).get(i);
        if (!medName.equals("")) {
            for (int i = 0; i < FreqList.size(); i++) {
                medsThisWeek += medsList.get(i) + ": " + FreqList.get(i) + "\n\r";
            }

            //Log.e("My Freq List", String.valueOf(FreqList));

            for (int i = 0; i < FreqList.size(); i++) {
                //Log.e("Size",Integer.toString(FreqList.size()));
                //Log.e("I",Integer.toString(i));

                if (FreqList.get(i).contains("Twice daily")) {
                    medFreqList.add(i, 2);
                } else if (FreqList.get(i).contains("Daily")) {
                    medFreqList.add(i, 1);
                } else if (FreqList.get(i).contains("Three times daily")) {
                    medFreqList.add(i, 3);
                } else if (FreqList.get(i).contains("Four times per day")) {
                    medFreqList.add(i, 4);
                }
            }

            int totalTaken = 0;
            for (int i = 0; i < medsList.size(); i++) {
                ArrayList<Integer> count = new ArrayList<>();
                for (int h = 0; h < daysTaken.size(); h++) {
                    int num = util.countMatches(medsTaken.get(h).toString(), (String) medsList.get(i));
                    count.add(num);
                    totalTaken += num;
                }
                medsDailyCount.add(count);
            }

            medsThisWeek += "\n\r\n\r";
            for (int i = 0; i < daysTaken.size(); i++) {
                medsThisWeek += daysTaken.get(i) + ":  \n\r";
                for (int h = 0; h < medsList.size(); h++) {
                    medsThisWeek += medsList.get(h) + ": " + medsDailyCount.get(h).get(i) + "/" + medFreqList.get(h) + "\n\r";
                }
                medsThisWeek += "\n\r";
            }
            int totalNeeded = 0;

            medsThisWeek += "Over this time period, " + patientName + " has taken \n\r";
            for (int h = 0; h < medsList.size(); h++) {
                int weekCount = 0;
                medsThisWeek += medsList.get(h) + " ";
                for (int i = 0; i < medsDailyCount.get(h).size(); i++) {
                    if (medsDailyCount.get(h).get(i) > 0) {
                        weekCount++;
                    }
                }
                medsThisWeek += Integer.toString(weekCount) + "/" + Integer.toString(prefs.getDaystoSend()) + " days\n\r";
                totalNeeded += prefs.getDaystoSend() * medFreqList.get(h);
            }

            //Log.e("Total Taken",Integer.toString(totalTaken));
            //Log.e("Total Needed",Integer.toString(totalNeeded));
            String adherence;
            if (((float) totalTaken / totalNeeded) >= .8) {
                adherence = "Medication Adherent.";
            } else {
                adherence = "Not Medication Adherent.";
            }

            medsThisWeek += "\n\r" + patientName + " medication adherence ratio is " + Integer.toString(totalTaken) + "/" + Integer.toString(totalNeeded) + " and is therefore " + adherence;
            medsThisWeek += "\n\r\n\rMedAdhereSolutions Team";
            //Log.e("Meds This Week",medsThisWeek);

            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Patient Update: " + patientName);
            intent.putExtra(Intent.EXTRA_TEXT, medsThisWeek);
            intent.setData(Uri.parse("mailto:" + sendTo)); // or just "mailto:" for blank
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
            startActivity(intent);
        } else {
            Toast.makeText(this, "Please Enter at least one medication!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    public void getPatientName() {
        mDatabase.child("storage").child("users").child(UID).child("email").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int emailIndex = dataSnapshot.getValue().toString().lastIndexOf("@");
                        Log.e("Patient name", dataSnapshot.getValue().toString().substring(0, emailIndex));
                        prefs.setUsername(dataSnapshot.getValue().toString().substring(0, emailIndex));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    public void getRegisterDate() {
        mDatabase.child("storage").child("users").child(UID).child("registrationdate").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String regdate = dataSnapshot.getValue().toString();
                        //Log.e("Register date", regdate);
                        prefs.setRegistrationDate(regdate);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    public void getEnrollmentDate() {
        mDatabase.child("storage").child("users").child(UID).child("enrollmentdate").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String endate = dataSnapshot.getValue().toString();
                        //Log.e("Register date", regdate);
                        prefs.setEnrollmentDate(endate);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    public void getDischargeDate() {
        mDatabase.child("storage").child("users").child(UID).child("dischargedate").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String disdate = dataSnapshot.getValue().toString();
                        //Log.e("Discharge date", disdate);
                        prefs.setDischargeDate(disdate);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    public void findLifestyleFeedback() {
        final ArrayList<String> responseArray = new ArrayList<>();
        mDatabase.child("app").child("users").child(UID).child("lifestylesurveyanswersRW").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        String lifedate = "";
                        String attempts = "";
                        while (it.hasNext()) {
                            DataSnapshot medicine = it.next();
                            lifedate = medicine.getKey().toString();
                            attempts = medicine.getValue().toString();
                            //responseArray.add(lifedate);
                            //Log.e("responseArray", ""+responseArray);
                            //Log.e("lifedate",lifedate);
                            //Log.e("repsonse",attempts);
                        }
                        //Log.e("responseArray complete", ""+responseArray);
                        prefs.setLifestyleDate(lifedate);
                        //Log.e("lifedate",lifedate);
                        //Log.e("repsonse/////",attempts);
                        try {
                            String[] stringArray = attempts.split(",");
                            int[] surveyResponse = new int[stringArray.length];
                            for (int i = 0; i < stringArray.length; i++) {
                                String numberAsString = stringArray[i];
                                surveyResponse[i] = Integer.parseInt(numberAsString.replace("[", "").replace("]", "").replace(" ", ""));
                            }
                            //Log.e("LifesurveyResponse", Arrays.toString(surveyResponse));
                            prefs.setLifestyleSurveyAnswersRW(surveyResponse);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    public void findLiteracyFeedback() {
        mDatabase.child("app").child("users").child(UID).child("literacysurveyanswersRW").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        String litdate = "";
                        String attempts1 = "";
                        while (it.hasNext()) {
                            DataSnapshot medicine = it.next();
                            litdate = medicine.getKey().toString();
                            attempts1 = medicine.getValue().toString();
                            //responseArray1.add(attempts1);
                        }
                        prefs.setLiteracyDate(litdate);
                        //Log.e("litdate",litdate);
                        //Log.e("litrepsonse/////",attempts1);
                        String[] stringArray1 = attempts1.split(",");
                        int[] litResponse = new int[stringArray1.length];
                        for (int i = 0; i < stringArray1.length; i++) {
                            String numberAsString = stringArray1[i];
                            litResponse[i] = Integer.parseInt(numberAsString.replace("[", "").replace("]", "").replace(" ", ""));
                        }
                        //Log.e("litResponse", Arrays.toString(litResponse));
                        prefs.setLiteracySurveyAnswers(litResponse);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    public void getBPGoal() {
        mDatabase.child("app").child("users").child(UID).child("bloodpressuregoal").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String bloodpressuregoal = (String) dataSnapshot.getValue();
                        //Log.e("BP Goal", bloodpressuregoal);
                        prefs.setBloodPressureGoal(bloodpressuregoal);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    public void getBplog() {
        mDatabase.child("app").child("users").child(UID).child("bloodPressureLog").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                while (it.hasNext()) {
                    DataSnapshot medicine = it.next();
                    String bpDate = medicine.getKey();
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
                    //Log.e("Blood Pressure Last", bpOnly);
                    prefs.setBpValue(bpOnly);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });
    }

    public void getBwlog() {
        mDatabase.child("app").child("users").child(UID).child("weightLog").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                while (it.hasNext()) {
                    DataSnapshot medicine = it.next();
                    String bwDate = medicine.getKey();
                    String bw = medicine.getValue().toString();
                    //Log.e("BP Date", bpDate);
                    //Log.e("Blood Pressure", bw);

                    List<String> dates = Arrays.asList(bw.replace("{", "").replace("}", "").split(", "));
                    //Log.e("Bw", dates.toString());

                    String bwOnly = "";
                    ArrayList<String> dates2 = new ArrayList<String>();
                    if (dates.size() > 1) {
                        for (int i = 0; i < dates.size(); i++) {
                            dates2.add(dates.get(i).substring(0, 5));
                        }
                        Collections.sort(dates2);
                        //bpOnly = dates2.get(dates.size()-1);
                        for (int i = 0; i < dates.size(); i++) {
                            if (dates.get(i).contains(dates2.get(dates2.size() - 1))) {
                                bwOnly = dates.get(i).substring(dates.get(i).indexOf("=") + 1, dates.get(i).length());
                            }
                        }
                        //Log.e("List", dates2.toString());
                    } else {
                        bwOnly = dates.get(0).substring(dates.get(0).indexOf("=") + 1, dates.get(0).length());
                    }
                    //Log.e("Body weight Last", bwOnly);
                    prefs.setBwValue(bwOnly + " pounds");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }

        });
    }

    public void getHeartRatelog() {
        mDatabase.child("app").child("users").child(UID).child("HeartRateLog").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                while (it.hasNext()) {
                    DataSnapshot medicine = it.next();
                    String HRDate = medicine.getKey();
                    String Hr = medicine.getValue().toString();
                    //Log.e("BP Date", bpDate);
                    //Log.e("Blood Pressure", Hr);

                    List<String> dates = Arrays.asList(Hr.replace("{", "").replace("}", "").split(", "));
                    //Log.e("HR", dates.toString());

                    String HrOnly = "";
                    ArrayList<String> dates2 = new ArrayList<String>();
                    if (dates.size() > 1) {
                        for (int i = 0; i < dates.size(); i++) {
                            dates2.add(dates.get(i).substring(0, 5));
                        }
                        Collections.sort(dates2);
                        //bpOnly = dates2.get(dates.size()-1);
                        for (int i = 0; i < dates.size(); i++) {
                            if (dates.get(i).contains(dates2.get(dates2.size() - 1))) {
                                HrOnly = dates.get(i).substring(dates.get(i).indexOf("=") + 1, dates.get(i).length());
                            }
                        }
                        //Log.e("List", dates2.toString());
                    } else {
                        HrOnly = dates.get(0).substring(dates.get(0).indexOf("=") + 1, dates.get(0).length());
                    }
                    //Log.e("Heart Rate Last", HrOnly);
                    prefs.setHeartRateValue(HrOnly);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }

        });
    }

    public void getClinicName() {
        mDatabase.child("storage").child("users").child(UID).child("clinicname").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String clinicname = (String) dataSnapshot.getValue();
                        //Log.e("Clinic name", clinicname);
                        prefs.setClinicName(clinicname);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    public void getPharmacy() {
        mDatabase.child("storage").child("users").child(UID).child("pharmaname").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String pharmacyname = (String) dataSnapshot.getValue();
                        //Log.e("pharmacy name", pharmacyname);
                        prefs.setPharmaName(pharmacyname);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });

        mDatabase.child("storage").child("users").child(UID).child("pharmanumber").addValueEventListener(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String Pharmacynumber = dataSnapshot.getValue().toString();
                        prefs.setPharmaNumber(Pharmacynumber);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    public void getphysician() {
        mDatabase.child("storage").child("users").child(UID).child("physicianname").addValueEventListener(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String PhysicianName = dataSnapshot.getValue().toString();
                        prefs.setPhysicianName(PhysicianName);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });

        mDatabase.child("storage").child("users").child(UID).child("physiciannumber").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String PhysicianNumber = dataSnapshot.getValue().toString();
                        prefs.setPhysicianNumber(PhysicianNumber);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    public void getDiag(final int i, String UID, final int psize) {
        final ArrayList<String> diagList = new ArrayList<>();
        mDatabase.child("storage").child("users").child(UID).child("medicalconditions").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String array = dataSnapshot.getValue().toString().substring(dataSnapshot.getValue().toString().indexOf("["), dataSnapshot.getValue().toString().length() - 1);
                        //Log.e("array", array);
                        List<String> arr = Arrays.asList(array.replace("[", "").replace("]", "").trim().split(","));
                        for (int i = 0; i < arr.size(); i++) {
                            if (!arr.get(i).trim().contains("null")) {
                                //Log.e("arr item", arr.get(i));
                                diagList.add(arr.get(i).trim());
                            }
                        }
                        //Log.e("position",Integer.toString(i)+ diagList);
                        diagAllPatients.add(i, diagList);
                        prefs.setDiagList(diagAllPatients);


                        getSupportFragmentManager().beginTransaction().replace
                                (R.id.Fragment_frame1, new PatientInformationFragment()).commit();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    public void getMeds() {
        mDatabase.child("app").child("users").child(UID).child("medicine").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //e("reading", dataSnapshot.toString());
                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        while (it.hasNext()) {
                            DataSnapshot medicine = it.next();
                            //e("reading2", medicine.toString());
                            //e("reading3", medicine.getKey());
                            medArray.add(medicine.getKey().toString());
                        }
                        //Log.e("meds", medArray.toString());
                        prefs.setMeds(medArray);
                        getMedFrequency(medArray);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    public void getMedFrequency(final ArrayList<String> medArray) {
        for (int i = 0; i < medArray.size(); i++) {
            final int finalI = i;
            mDatabase.child("app").child("users").child(UID).child("medicine").child(medArray.get(i)).child("frequency").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Log.e("currentMed", medArray.get(finalI));
                            //Log.e("reading", dataSnapshot.getValue().toString());
                            medFrequency.add(dataSnapshot.getValue().toString().trim());
                            //e("medFrequency", medFrequency.toString());
                            prefs.setFrequency(medFrequency);
                            /*if (finalI == medArray.size() - 1) {
                                //e("medFrequencyFinal", medFrequency.toString());
                                TODO: Add Alarm function here based on frequency array
                                fileIOMeds(medArray,medFrequency);//to add the name of the medicine as well
                            }*/
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        }
                    });
        }
    }

    public void fileIOMeds(ArrayList<String> medArray, ArrayList<String> medFrequency) {
        deleteFile(MED_FILENAME);
        try {
            FileOutputStream fos = openFileOutput(MED_FILENAME, Context.MODE_APPEND);
            String text = "";
            for (int i = 0; i < medArray.size(); i++) {
                text = text.concat(medArray.get(i));
                if (i != medArray.size() - 1) {
                    text = text.concat("\n");
                }
            }
            fos.write(text.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Frequency Array
        deleteFile(FREQ_FILENAME);
        try {
            FileOutputStream fos = openFileOutput(FREQ_FILENAME, Context.MODE_APPEND);
            String text = "";
            for (int i = 0; i < medFrequency.size(); i++) {
                text = text.concat(medFrequency.get(i));
                if (i != medFrequency.size() - 1) {
                    text = text.concat("\n");
                }
            }
            //Log.e("Freq",text);
            fos.write(text.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //startAlarm(this);
    }

  /*  public void startAlarm(Context context) {
        //first notification at 10 AM next day, this should always fire if they have prescribed medication
        Intent intent1 = new Intent(this, ReminderService.class);
        int type = intent1.getIntExtra("type", 0);
        intent1.putExtra("type", type);
        startService(intent1);

        *//*getSupportFragmentManager().beginTransaction().replace
                (R.id.Fragment_frame1, new PatientInformationFragment()).commit();*//*
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        init();
        //progress_d.hideProgress();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
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
            prefs.setBpValue("");
            prefs.setHeartRateValue("");
            prefs.setBwValue("");
            finish();
            //new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit App")
            /*new AlertDialog.Builder(this).setTitle("Exit App")
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
                    }).setNegativeButton("no", null).show();*/
        }
    }
}