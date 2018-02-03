package com.appzoro.BP_n_ME.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appzoro.BP_n_ME.adapter.InformationAdapter;
import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;
import com.appzoro.BP_n_ME.util.Progress_dialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class DatabaseActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView img_Back;
    TextView submit;
    ListView infoListView;
    private Progress_dialog progress_d;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    String firstName;
    String lastName;
    String enrollmentdate;
    String email = "c";
    private String pass="";
    String bloodPressureGoal = "e";
    String clinicName = "f";
    String pharmaName = "g";
    String pharmaNumber = "h";
    String physicianName;
    String physicianNumber;
    String [] ailments  ={"","","","","","",""};
    int[] demographicsSurveyAnswers;// = {0,0,0,0,0,0,0};
    int[] lifestyleSurveyAnswersRW= {-1,-1,-1,-1,-1,-1,-1,-1};
    int[] literacySurveyAnswers = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
    ArrayList<String> informationNameList = new ArrayList<>();
    ArrayList<String> informationList = new ArrayList<>();
    ArrayList<String> medicineList = new ArrayList<>();
    ArrayList<String> medFrequencyList = new ArrayList<String>();
    MedasolPrefs prefs;
    String RegistrationDate, timestamp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        getSupportActionBar().hide();
        progress_d = Progress_dialog.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth.getInstance().signOut();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("Sign In", "onAuthStateChanged:signed_in:" + user.getUid());

                    user.getUid();
                    writeToDatabase(user);
                    Intent i = new Intent(DatabaseActivity.this, SignUpActivity.class);
                    startActivity(i);
                } else {
                    // User is signed out
                    Log.d("Sign In", "onAuthStateChanged:signed_out");
                }
            }
        };
        init();
        Listener();
    }

    private void init() {
        prefs = new MedasolPrefs(getApplicationContext());

        img_Back = (ImageView) findViewById(R.id.iv_back);
        submit = (TextView) findViewById(R.id.tv_submit);
        infoListView = (ListView) findViewById(R.id.informationListView);

        //firstName = prefs.getFristName();
        //lastName = prefs.getLastName();
        email = prefs.getEmail();
        enrollmentdate = prefs.getEnrollmentDate();
        pass = prefs.getPassword();
        bloodPressureGoal = prefs.getBloodPressureGoal();
        clinicName= prefs.getClinicName();
        pharmaName= prefs.getPharmaName();
        pharmaNumber = prefs.getPharmaNumber();
        physicianName = prefs.getPhysicianName();
        physicianNumber = prefs.getPhysicianNumber();

        String sString = prefs.getMedicalConditions(); //"101,203,405";
        String[] sArr = sString.split(",");
        ailments = new String[sArr.length];
            for (int i = 0; i < sArr.length; i++) {
            String numberStr = sArr[i].trim();
            Log.e("numberStr",":"+ numberStr);
            ailments[i] = (numberStr.replace("[","").replace("]","")/*.replace(" ","")*/);
        }
        Log.e("ailments", Arrays.toString(ailments));

        String sampleS = prefs.getDemographicsSurveyAnswers(); //"101,203,405";
        String[] stringArr = sampleS.split(",");
        demographicsSurveyAnswers = new int[stringArr.length];
        for (int i = 0; i < stringArr.length; i++) {
            String numberString = stringArr[i];
            demographicsSurveyAnswers[i] = Integer.parseInt(numberString.replace("[","").replace("]","").replace(" ",""));
        }
        Log.e("DemoSurvey", Arrays.toString(demographicsSurveyAnswers));

        medicineList =  new ArrayList<String>(Arrays.asList(prefs.getMeds().replace("[","").replace("]","").replace(" ","").split(",")));
        medFrequencyList =  new ArrayList<String>(Arrays.asList(prefs.getFreqList().replace("[","").replace("]","").split(",")));
        Log.e("medicationList size", String.valueOf(medicineList.size()));
        Log.e("FrequencyList size", String.valueOf(medFrequencyList.size()));

        //informationList.add(firstName + " " + lastName);
        informationList.add(email);
        informationList.add(enrollmentdate);
        informationList.add(bloodPressureGoal);
        informationList.add(clinicName);
        informationList.add(pharmaName);
        informationList.add(pharmaNumber);
        informationList.add(physicianName);
        informationList.add(physicianNumber);

        //informationNameList.add("Name: ");
        informationNameList.add("Email: ");
        informationNameList.add("Enrollment Date: ");
        informationNameList.add("Blood Pressure Goal: ");
        informationNameList.add("Clinic Name: ");
        informationNameList.add("Pharmacy Name: ");
        informationNameList.add("Pharmacy Number: ");
        informationNameList.add("Physician Name: ");
        informationNameList.add("Physician Number: ");

        initializeMedList();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar now = Calendar.getInstance();
        System.out.println(dateFormat.format(now.getTime()));
        String time = dateFormat.format(now.getTime());
        System.out.println(time);
    }

    private void Listener() {
        img_Back.setOnClickListener(this);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_submit:
                progress_d.showProgress(this);
                mAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progress_d.hideProgress();
                                Log.d("registration", "createUserWithEmail:onComplete:" + task.isSuccessful());

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (task.isSuccessful()) {
                                    Toast.makeText(DatabaseActivity.this, "The patient was successfully registered", Toast.LENGTH_LONG).show();
                                    //prefs.setUID(task.getResult().getUser().getUid());
                                    Intent intent = new Intent(DatabaseActivity.this,PatientSelection.class);
                                    startActivity(intent);
                                    finish();
                                } else{
                                    Toast.makeText(DatabaseActivity.this, "Registration Failed. Try a different username or make sure you're connected to the Internet", Toast.LENGTH_SHORT).show();
                                    /*Snackbar.make(view, "Registration Failed. Try a different username or make sure you're connected to the Internet",
                                            Snackbar.LENGTH_LONG).setAction("Action", null).show();*/
                                }
                            }
                        });
                break;
        }
    }

    public void writeToDatabase(FirebaseUser user) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar now = Calendar.getInstance();
        System.out.println(dateFormat.format(now.getTime()));
        String time = dateFormat.format(now.getTime());
        System.out.println(time);

        RegistrationDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        Log.e("RegistrationDate",RegistrationDate);
        prefs.setRegistrationDate(RegistrationDate);
        Long tsLong = System.currentTimeMillis();
        timestamp = tsLong.toString();
        Log.e("time Stamp", timestamp);

        String type = "2";

        mDatabase.child("app").child("users").child(user.getUid()).child("type").setValue(type);
        //mDatabase.child("storage").child("users").child(user.getUid()).child("name").setValue(firstName + " " + lastName);
        mDatabase.child("storage").child("users").child(user.getUid()).child("email").setValue(email);
        mDatabase.child("storage").child("users").child(user.getUid()).child("enrollmentdate").setValue(enrollmentdate);
        mDatabase.child("app").child("users").child(user.getUid()).child("bloodpressuregoal").setValue(bloodPressureGoal);
        mDatabase.child("storage").child("users").child(user.getUid()).child("clinicname").setValue(clinicName);
        mDatabase.child("storage").child("users").child(user.getUid()).child("pharmaname").setValue(pharmaName);
        mDatabase.child("storage").child("users").child(user.getUid()).child("pharmanumber").setValue(pharmaNumber);
        mDatabase.child("storage").child("users").child(user.getUid()).child("physicianname").setValue(physicianName);
        mDatabase.child("storage").child("users").child(user.getUid()).child("physiciannumber").setValue(physicianNumber);
        //Log.e("RegistrationDate",RegistrationDate);
        mDatabase.child("storage").child("users").child(user.getUid()).child("registrationdate").setValue(RegistrationDate);
        mDatabase.child("storage").child("users").child(user.getUid()).child("timestamp").setValue(timestamp);
        mDatabase.child("storage").child("users").child(user.getUid()).child("demographicssurveyanswers").child(time).setValue(Arrays.toString(demographicsSurveyAnswers));
        mDatabase.child("storage").child("users").child(user.getUid()).child("medicalconditions").child(time).setValue(Arrays.toString(ailments));

        prefs.setLifestyleSurveyAnswersRW(lifestyleSurveyAnswersRW);
        mDatabase.child("app").child("users").child(user.getUid()).child("lifestylesurveyanswersRW").child(time).setValue(Arrays.toString(lifestyleSurveyAnswersRW));
        //prefs.setLiteracySurveyAnswers(literacySurveyAnswers);
        //mDatabase.child("app").child("users").child(user.getUid()).child("literacysurveyanswersRW").child(time).setValue(Arrays.toString(literacySurveyAnswers));

        for (int i = 0; i < medicineList.size(); i++) {
            String userMeds = medicineList.get(i);
            Log.e("meds",medicineList.get(i) + medFrequencyList.get(i).trim());
            mDatabase.child("app").child("users").child(user.getUid()).child("medicine").child(userMeds).child("name").setValue(medicineList.get(i).trim());
            mDatabase.child("app").child("users").child(user.getUid()).child("medicine").child(userMeds).child("frequency").setValue(medFrequencyList.get(i).trim());
        }
    }

    public void initializeMedList() {
        final ListView lv = (ListView) findViewById(R.id.informationListView);
        lv.setAdapter(new InformationAdapter(DatabaseActivity.this, informationList, informationNameList));
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
        super.onBackPressed();
    }
}
