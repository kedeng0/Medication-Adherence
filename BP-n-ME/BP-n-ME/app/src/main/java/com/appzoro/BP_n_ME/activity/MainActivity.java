package com.appzoro.BP_n_ME.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.adapter.NavigationMenuAdapter;
import com.appzoro.BP_n_ME.fragment.BloodpressureFragment;
import com.appzoro.BP_n_ME.fragment.CallforClinicalCareFragment;
import com.appzoro.BP_n_ME.fragment.ClinicalContactFragment;
import com.appzoro.BP_n_ME.fragment.MedicationUtilizationFragment;
import com.appzoro.BP_n_ME.fragment.NotificationFragment;
import com.appzoro.BP_n_ME.fragment.SurveysFragment;
import com.appzoro.BP_n_ME.fragment.TipsFragment;
import com.appzoro.BP_n_ME.fragment.SMAPFragment;
import com.appzoro.BP_n_ME.model.NavigationMenuItems;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;
import com.appzoro.BP_n_ME.util.Progress_dialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.splunk.mint.Mint;

import org.apache.commons.lang3.StringUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "tag";
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    ListView menuList;
    NavigationMenuAdapter mlistAdapter;
    List<NavigationMenuItems> listDataItem;
    List<String> medsList;
    final ArrayList<String> mList = new ArrayList<>();
    ArrayList<String> medArray = new ArrayList<String>();
    ArrayList<String> medFrequency = new ArrayList<String>();
    ArrayList<ArrayList<String>> diagAllPatients = new ArrayList<ArrayList<String>>();
    int i;
    String UID;
    MedasolPrefs prefs;
    private Progress_dialog progress_d;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    public static  int notification_lafda=0;
    String notification;
    PendingIntent pi1, pi2,pi3,pi4,pi5,pi6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Mint.initAndStartSession(this.getApplication(), "9bb93c94");
        init();
        setupNavigationDrawer();
        Listener();
        //getDischargeDate();
        getEnrollmentDate();
        getRegisterDate();
        setSymptomSurvey();
        findLifestyleFeedback();
        findARMSFeedback();
        //findLiteracyFeedback();
        getDiag(i);
        getMeds();
        medsLog();
        getPharmacy();
        getphysician();
    }

    private void init() {
        progress_d = new Progress_dialog(this);
        prefs = new MedasolPrefs(getApplicationContext());
        UID = prefs.getUID();

        /*getSupportFragmentManager().beginTransaction().replace
                (R.id.Fragment_frame_main_activity, new TipsFragment()).commit();*/

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, SigninActivity.class));
                    finish();
                }
            }
        };

        //when running the app a second time.
        prefs.resetMedicationList();
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
        TextView username = header_View.findViewById(R.id.tv_username);
        username.setText(StringUtils.capitalize(prefs.getUsername().toLowerCase().trim()));
    }

    private void Listener() {
        menuList.setOnItemClickListener(this);
    }

    private void prepareListData() {
        listDataItem = new ArrayList<>();

        // Adding data Items
        NavigationMenuItems item1 = new NavigationMenuItems();
        item1.setItemName("Home");
        item1.setItemImg(R.drawable.home);
        listDataItem.add(item1);

        NavigationMenuItems item2 = new NavigationMenuItems();
        item2.setItemName("Blood Pressure Log");
        item2.setItemImg(R.drawable.bp);
        listDataItem.add(item2);

        /*NavigationMenuItems item3 = new NavigationMenuItems();
        item3.setItemImg(R.drawable.bw);
        item3.setItemName("Body Weight Log");
        listDataItem.add(item3);*/

        NavigationMenuItems item4 = new NavigationMenuItems();
        item4.setItemImg(R.drawable.meds);
        item4.setItemName("Medication Utilization Log");
        listDataItem.add(item4);


        NavigationMenuItems item5 = new NavigationMenuItems();
        item5.setItemImg(R.drawable.survey);
        item5.setItemName("Surveys");
        listDataItem.add(item5);

        NavigationMenuItems item6 = new NavigationMenuItems();
        item6.setItemImg(R.drawable.clinical_contact);
        item6.setItemName("Study Contact");
        listDataItem.add(item6);

        NavigationMenuItems item7 = new NavigationMenuItems();
        item7.setItemImg(R.drawable.call_pharmacist);
        item7.setItemName("Call for Clinical Care");
        listDataItem.add(item7);

        NavigationMenuItems item8 = new NavigationMenuItems();
        item8.setItemImg(R.drawable.notifications);
        item8.setItemName("Notifications");
        listDataItem.add(item8);

        NavigationMenuItems item9 = new NavigationMenuItems();
        item9.setItemImg(R.drawable.phone);
        item9.setItemName("Hardware Status");
        listDataItem.add(item9);

        /*NavigationMenuItems item9 = new NavigationMenuItems();
        item9.setItemImg(R.drawable.t_c);
        item9.setItemName("Informed Consent");
        listDataItem.add(item9);*/

        /*NavigationMenuItems item10 = new NavigationMenuItems();
        item10.setItemImg(R.drawable.logout);
        item10.setItemName("Logout");
        listDataItem.add(item10);*/
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (position == 0) {
            getSupportFragmentManager().beginTransaction().replace
                    (R.id.Fragment_frame_main_activity, new TipsFragment()).commit();
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if (position == 1) {
            getSupportFragmentManager().beginTransaction().replace
                    (R.id.Fragment_frame_main_activity, new BloodpressureFragment()).commit();
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        /*else if (position == 2) {
            getSupportFragmentManager().beginTransaction().replace
                    (R.id.Fragment_frame_main_activity, new BodyWeightFragment()).commit();
            drawerLayout.closeDrawer(GravityCompat.START);
        }*/
        else if (position == 2) {
            getSupportFragmentManager().beginTransaction().replace
                    (R.id.Fragment_frame_main_activity, new MedicationUtilizationFragment()).commit();
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if (position == 3) {
            getSupportFragmentManager().beginTransaction().replace
                    (R.id.Fragment_frame_main_activity, new SurveysFragment()).commit();
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if (position == 4) {
            getSupportFragmentManager().beginTransaction().replace
                    (R.id.Fragment_frame_main_activity, new ClinicalContactFragment()).commit();
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if (position == 5) {
            getSupportFragmentManager().beginTransaction().replace
                    (R.id.Fragment_frame_main_activity, new CallforClinicalCareFragment()).commit();
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if (position == 6) {
            getSupportFragmentManager().beginTransaction().replace
                    (R.id.Fragment_frame_main_activity, new NotificationFragment()).commit();
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (position == 7) {
            getSupportFragmentManager().beginTransaction().replace
                    (R.id.Fragment_frame_main_activity, new SMAPFragment()).commit();
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        /*else if (position == 7) {
            getSupportFragmentManager().beginTransaction().replace
                    (R.id.Fragment_frame_main_activity, new TermsFragment()).commit();
            drawerLayout.closeDrawer(GravityCompat.START);
        }*/
        /*else if (position == 9) {
            new AlertDialog.Builder(this).setTitle("Logout")
                    .setMessage("Are you sure you want to Logout?")
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progress_d.showProgress(MainActivity.this);

                            Intent myIntent = new Intent(MainActivity.this, MyAlarmReceiver.class);
                            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                            pi1 = PendingIntent.getBroadcast(MainActivity.this, 1, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            alarmManager.cancel(pi1);
                            pi2 = PendingIntent.getBroadcast(MainActivity.this, 2, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            alarmManager.cancel(pi2);
                            pi3 = PendingIntent.getBroadcast(MainActivity.this, 3, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            alarmManager.cancel(pi3);
                            pi4 = PendingIntent.getBroadcast(MainActivity.this, 4, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            alarmManager.cancel(pi4);
                            pi5 = PendingIntent.getBroadcast(MainActivity.this, 5, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            alarmManager.cancel(pi5);
                            pi6 = PendingIntent.getBroadcast(MainActivity.this, 6, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            alarmManager.cancel(pi6);

                            stopService(myIntent);
                            mAuth.signOut();
                            prefs.clear();
                        }
                    }).setNegativeButton("no", null).show();
            drawerLayout.closeDrawer(GravityCompat.START);
        }*/
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

    public void getDischargeDate() {
        mDatabase.child("storage").child("users").child(UID).child("dischargedate").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String disdate = dataSnapshot.getValue().toString();
                        Log.e("Discharge date", disdate);
                        prefs.setDischargeDate(disdate);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    public void getEnrollmentDate() {
        mDatabase.child("storage").child("users").child(UID).child("enrollmentdate").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String endate = dataSnapshot.getValue().toString();
                        Log.e("Enrollment date", endate);
                        prefs.setEnrollmentDate(endate);
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

    public void setSymptomSurvey() {
        mDatabase.child("app").child("users").child(UID).child("symptomsurveyanswers").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        String lifedate = "";
                        while (it.hasNext()) {
                            DataSnapshot medicine = it.next();
                            lifedate = medicine.getKey();
                        }
                        prefs.setSymptomSurveyDate(lifedate);
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
                            lifedate = medicine.getKey();
                            attempts = medicine.getValue().toString();
                            //responseArray.add(attempts);
                            //Log.e("repsonse",attempts);
                        }
                        prefs.setLifestyleDate(lifedate);
                        //Log.e("lifedate",lifedate);
                        //Log.e("repsonse/////",attempts);
                        try {
                            String[] stringArray = attempts.split(",");
                            int[] surveyResponse = new int[stringArray.length];
                            for (int i = 0; i < stringArray.length; i++) {
                                String numberAsString = stringArray[i];
                                surveyResponse[i] = Integer.parseInt(numberAsString.replace("[", "").replace("]", "").replace(" ", ""));
                                //Log.e("LifesurveyResponse", Arrays.toString(surveyResponse));
                                prefs.setLifestyleSurveyAnswersRW(surveyResponse);
                            }
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

    public void findARMSFeedback() {
        mDatabase.child("app").child("users").child(UID).child("arms7surveyanswers").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        String lifedate = "";
                        String attempts = "";
                        while (it.hasNext()) {
                            DataSnapshot medicine = it.next();
                            lifedate = medicine.getKey();
                            attempts = medicine.getValue().toString();
                            //responseArray.add(attempts);
                            //Log.e("repsonse",attempts);
                        }
                        prefs.setArmsDate(lifedate);
                        //Log.e("lifedate",lifedate);
                        //Log.e("repsonse/////",attempts);
                        try {
                            String[] stringArray = attempts.split(",");
                            int[] surveyResponse = new int[stringArray.length];
                            for (int i = 0; i < stringArray.length; i++) {
                                String numberAsString = stringArray[i];
                                surveyResponse[i] = Integer.parseInt(numberAsString.replace("[", "").replace("]", "").replace(" ", ""));
                                //Log.e("LifesurveyResponse", Arrays.toString(surveyResponse));
                                prefs.setArmsSurveyAnswers(surveyResponse);
                            }
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

    public void getDiag(final int i) {
        final ArrayList<String> diagList = new ArrayList<>();
        mDatabase.child("storage").child("users").child(UID).child("medicalconditions").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String array = dataSnapshot.getValue().toString().substring(dataSnapshot.getValue().toString().indexOf("["), dataSnapshot.getValue().toString().length() - 3);
                        //Log.e("array", array);
                        while (array.indexOf(",") != -1) {
                            if (!array.substring(1, array.indexOf(",")).contains("null")) {
                                diagList.add(array.substring(1, array.indexOf(",")).trim());
                                //Log.e("Next", array.substring(1, array.indexOf(",")));
                            }
                            array = array.substring(array.indexOf(",") + 1);
                        }
                        //Log.e("position",Integer.toString(i));
                        diagAllPatients.add(i, diagList);
                        prefs.setDiagList(diagAllPatients);
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
                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        while (it.hasNext()) {
                            DataSnapshot medicine = it.next();
                            medArray.add(medicine.getKey());
                        }
                        prefs.setMeds(medArray);
                        getMedFrequency(medArray);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
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
                            medFrequency.add(dataSnapshot.getValue().toString().trim());
                            prefs.setFrequency(medFrequency);
                            if (finalI == medArray.size() - 1) {
                                //TODO: Add Alarm function here based on frequency array
                                fileIOMeds(medArray, medFrequency);//to add the name of the medicine as well
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
        }
    }

    public void fileIOMeds(ArrayList<String> medArray, ArrayList<String> medFrequency) {
        String MED_FILENAME = "med_file";
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
        String FREQ_FILENAME = "freq_file";
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
        notification_lafda=1;
        startAlarm(this);
    }

    public void startAlarm(Context context) {


        notification = getIntent().getStringExtra("notification");
        if (notification !=null && notification.equals("1")){
            getSupportFragmentManager().beginTransaction().replace
                    (R.id.Fragment_frame_main_activity, new NotificationFragment()).commit();
            //Log.e("notification", "noti");
        } else {
            //Log.e("notification", "tips");
            getSupportFragmentManager().beginTransaction().replace
                    (R.id.Fragment_frame_main_activity, new TipsFragment()).commit();

            //first notification at 10 AM next day, this should always fire if they have prescribed medication
            // AND notification at 8:15PM every night describing How Many doses were taken or Not
            // AND notification at 10AM after 7 days & 28 days of discharge date to remind to take survey and thanking for using app

            /*Intent intent1 = new Intent(this, ReminderService.class);
            int type = intent1.getIntExtra("type", 0);
            intent1.putExtra("type", type);
            startService(intent1);*/
        }
    }

    public void medsLog() {
        final String curDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        Log.e("curDate", "curDate" + curDate);
        mDatabase.child("app").child("users").child(UID).child("medicineLog").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                while (it.hasNext()) {
                    DataSnapshot medicine = it.next();
                    String medsDate = medicine.getKey();
                    String Medication = medicine.getValue().toString();
                    mList.clear();
                    if (medsDate.equals(curDate)) {
                        medsList = Arrays.asList(Medication.replace("{", "").replace("}", "").split(", "));
                        for (int i = 0; i < medsList.size(); i++) {
                            String meds = medsList.get(i).split("=")[1];
                            mList.add(meds);
                        }
                    }
                }
                prefs.setMedsLog(mList);
                Log.e("final mList", String.valueOf(mList));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
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

    /*private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.", Toast.LENGTH_SHORT).show();
            exit = true;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }

            }, 3 * 1000);
        }
    }*/

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            //new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit App")
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
