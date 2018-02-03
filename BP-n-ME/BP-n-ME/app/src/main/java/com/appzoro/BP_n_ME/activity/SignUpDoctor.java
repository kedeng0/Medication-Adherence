package com.appzoro.BP_n_ME.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class SignUpDoctor extends AppCompatActivity implements View.OnClickListener {

    EditText fName, lName, userName, contactNo, password, cnfPassword;
    TextView submit, signin;
    InputFilter filter;
    private static final Pattern USERNAME_PATTERN = Pattern
            .compile("[a-zA-Z0-9]{3,15}");
    private Progress_dialog progress_d;
    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    MedasolPrefs prefs;
    String firstName, lastName, email, ContactNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_doctor);
        getSupportActionBar().hide();

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
        progress_d = Progress_dialog.getInstance();
        prefs = new MedasolPrefs(getApplicationContext());

        fName = (EditText) findViewById(R.id.et_fname);
        lName = (EditText) findViewById(R.id.et_lname);
        userName = (EditText) findViewById(R.id.et_username);
        contactNo = (EditText) findViewById(R.id.et_contactNumber);
        password = (EditText) findViewById(R.id.et_password);
        cnfPassword = (EditText) findViewById(R.id.et_cnfpassword);
        submit = (TextView) findViewById(R.id.tv_submit);
        signin = (TextView) findViewById(R.id.tv_signin);
        String customColorText = getResources().getString(R.string.signin);
        signin.setText(Html.fromHtml(customColorText));

        filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.length() > 0) {
                    if (!Character.isDigit(source.charAt(0)))
                        return "";
                    else {
                        if (dstart == 3) {
                            return source + ") ";
                        } else if (dstart == 0) {
                            return "(" + source;
                        } else if ((dstart == 5) || (dstart == 9))
                            return "-" + source;
                        else if (dstart >= 14)
                            return "";
                    }
                }
                return null;
            }
        };
        contactNo.setFilters(new InputFilter[]{filter});
    }

    private void Listener() {
        submit.setOnClickListener(this);
        signin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_submit:
                util.hideSOFTKEYBOARD(submit);
                if (validation()) {
                    startRegistration(view);
                }
                break;
            case R.id.tv_signin:
                util.hideSOFTKEYBOARD(signin);
                Intent i = new Intent(this, SigninActivity.class);
                startActivity(i);
                finish();
                break;
        }
    }

    private boolean validation() {
        boolean isValid = true;
        if (fName.getText().toString().trim().equals("")) {
            Toast.makeText(SignUpDoctor.this, "Please enter First name!", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else if (lName.getText().toString().trim().equals("")) {
            Toast.makeText(SignUpDoctor.this, "Please enter Last name!", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else if (userName.getText().toString().trim().equals("")) {
            Toast.makeText(SignUpDoctor.this, "Please enter username!", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else if (!CheckUsername(userName.getText().toString().trim())) {
            Toast.makeText(SignUpDoctor.this, "ENTER VALID USERNAME!", Toast.LENGTH_LONG).show();
            isValid = false;
        } else if (contactNo.getText().toString().trim().equals("")) {
            Toast.makeText(SignUpDoctor.this, "Please enter Contact Number!", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else if (contactNo.length() > 0 && contactNo.length() < 14) {
            Toast.makeText(SignUpDoctor.this, "Please Enter Valid 10 Digit Contact Number!", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else if (password.getText().toString().equals("")) {
            Toast.makeText(SignUpDoctor.this, "Please enter password!", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else if (password.getText().toString().trim().length() < 6) {
            Toast.makeText(SignUpDoctor.this, "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else if (cnfPassword.getText().toString().equals("")) {
            Toast.makeText(SignUpDoctor.this, "Please enter Confirm password!", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        return isValid;
    }

    private boolean CheckUsername(String username) {

        return USERNAME_PATTERN.matcher(username).matches();
    }

    private void startRegistration(final View view) {
        progress_d.showProgress(this);

        mAuth.createUserWithEmailAndPassword(userName.getText().toString().trim() + "@medadheresolutions.com", password.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progress_d.hideProgress();
                        Log.d("registration", "createUserWithEmail:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {
                            finish();
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(SignUpDoctor.this, "Registration Failed. Try a different username or make sure you're connected to the Internet", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onAuthSuccess(FirebaseUser user) {
        int toggle_val = 1;
        // Write new user
        writeNewUser(user.getUid(), toggle_val, user.getEmail());

        startActivity(new Intent(SignUpDoctor.this, PatientSelection.class));
        finish();
    }

    private void writeNewUser(String userId, int type, String email) {
        firstName = fName.getText().toString().trim();
        lastName = lName.getText().toString().trim();
        ContactNo = contactNo.getText().toString().trim();
        String user = Integer.toString(type);


        if (type == 1) {
            mDatabase.child("app").child("users").child(userId).child("type").setValue(type);
            mDatabase.child("storage").child("doctor").child(userId).child("name").setValue(firstName + " " + lastName);
            mDatabase.child("storage").child("doctor").child(userId).child("email").setValue(email);
            mDatabase.child("storage").child("doctor").child(userId).child("contactnumber").setValue(ContactNo);
        } else/* if (type == 2)*/ {
            mDatabase.child("app").child("users").child(userId).child("type").setValue(type);
            mDatabase.child("storage").child("users").child(userId).child("name").setValue(firstName + " " + lastName);
            mDatabase.child("storage").child("users").child(userId).child("email").setValue(email);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
