package com.appzoro.BP_n_ME.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;
import com.appzoro.BP_n_ME.util.Progress_dialog;
import com.appzoro.BP_n_ME.util.util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SigninActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    TextView signin;
    EditText username, password;
    RadioGroup radioGroup;
    CheckBox showPassword;
    private Progress_dialog progress_d;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    MedasolPrefs prefs;
    int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        getSupportActionBar().hide();
        init();
        Listener();
    }

    private void init() {
        prefs = new MedasolPrefs(getApplicationContext());
        progress_d = new Progress_dialog(this);
        //progress_d = Progress_dialog.getInstance();
        mAuth = FirebaseAuth.getInstance();

        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        username = (EditText) findViewById(R.id.et_username);
        password = (EditText) findViewById(R.id.et_password);
        signin = (TextView) findViewById(R.id.tv_signin);
        showPassword = (CheckBox) findViewById(R.id.ch_showpassword);

        /*username.setText("bptest");
        password.setText("qwerty");*/
    }

    private void Listener() {
        signin.setOnClickListener(this);
        showPassword.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_signin:
                if ( util.isInternetOn(this)) {
                    if (validation()) {
                        login();
                    }
                } else {
                    Toast.makeText(this, "We can't detect network connectivity, connect to the internet and try again.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ch_showpassword:
                if (showPassword.isChecked()) {
                    password.setTransformationMethod(null);
                } else {
                    password.setTransformationMethod(new PasswordTransformationMethod());
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        switch (checkedId) {
            case R.id.patient:
                type = 2;//Type 2 define patient
                break;
            case R.id.doctor:
                type = 1;//Type 1 define doctor
                break;
        }
    }

    private boolean validation() {
        boolean isValid = true;
        if (username.getText().toString().trim().equals("")) {
            Toast.makeText(SigninActivity.this, "Please enter username!", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else if (password.getText().toString().trim().equals("")) {
            Toast.makeText(SigninActivity.this, "Please enter password!", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else if (type == 0){
            Toast.makeText(SigninActivity.this, "Please select either patient or pharmacist!", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        return isValid;
    }

    private void login() {
        //Toast.makeText(this, "type: " +type, Toast.LENGTH_SHORT).show();
        progress_d.showProgress(this);
        mAuth.signInWithEmailAndPassword(username.getText().toString().trim() + "@medadheresolutions.com",
                password.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    // there was an error
                    progress_d.hideProgress();
                    Toast.makeText(SigninActivity.this, "Invalid username and/or password!", Toast.LENGTH_LONG).show(); /*getString(R.string.mAuth_failed)*/
                } else {
                    if (type == 2) {
                        PatientOnAuthSuccessLogin(task.getResult().getUser());
                    } else if (type == 1) {
                        DoctorOnAuthSuccessLogin(task.getResult().getUser());
                    }
                }
            }
        });
    }

    private void PatientOnAuthSuccessLogin(final FirebaseUser user) {
        if (user != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("app").child("users").child(user.getUid()).child("type");
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String value = dataSnapshot.getValue(String.class);
                    Log.e("value", "value  " + value);
                    if (Integer.parseInt(value) == 2) {
                        progress_d.hideProgress();
                        prefs.setType(2);
                        prefs.setUID(user.getUid());
                        Log.e("uid", user.getUid());
                        prefs.setUsername(username.getText().toString().trim());
                        Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        progress_d.hideProgress();
                        prefs.setType(2);
                        Toast.makeText(SigninActivity.this, "You’re not a Patient, please login as Pharmacist!", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void DoctorOnAuthSuccessLogin(final FirebaseUser user) {
        if (user != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("app").child("users").child(user.getUid()).child("type");
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String value = dataSnapshot.getValue(String.class);
                    Log.e("value", "value  " + value);
                    if (Integer.parseInt(value) == 1) {
                        progress_d.hideProgress();
                        prefs.setType(1);
                        prefs.setDrName(username.getText().toString().trim());
                        Intent intent = new Intent(SigninActivity.this, PatientSelection.class);
                        startActivity(intent);
                        finish();
                    } else {
                        progress_d.hideProgress();
                        prefs.setType(1);
                        Toast.makeText(SigninActivity.this, "You’re not a Pharmacist, please login as Patient!", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
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
