package com.appzoro.BP_n_ME.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    InputFilter filter;
    LinearLayout userLayout;
    EditText userName, dischargedate, password,cnfPassword, pharmacyName, pharmacyNumber,physicianName,physicianNo;
    Spinner clinics;
    String clinicName;
    TextView submit;
    private Progress_dialog progress_d;
    private FirebaseAuth mAuth ;
    MedasolPrefs prefs;
    private static final Pattern USERNAME_PATTERN = Pattern
            .compile("[a-zA-Z0-9]{3,15}");
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();
        init();
        Listener();
    }

    private void init() {
        userLayout = (LinearLayout) findViewById(R.id.userLayout);
        userName = (EditText) findViewById(R.id.et_username);
        dischargedate = (EditText) findViewById(R.id.et_dischargedate);
        password = (EditText) findViewById(R.id.et_password);
        cnfPassword = (EditText) findViewById(R.id.et_cnfpassword);
        clinics = (Spinner) findViewById(R.id.et_clinicName);
        pharmacyName = (EditText) findViewById(R.id.et_pharmacyName);
        pharmacyNumber = (EditText) findViewById(R.id.et_pharmacyNumber);
        physicianName = (EditText) findViewById(R.id.et_physicianName);
        physicianNo = (EditText) findViewById(R.id.et_physicianNumber);
        submit = (TextView) findViewById(R.id.tv_submit);

        // Initializing a String Array
        String[] clinic = new String[]{
                "Select Clinic Name",
                "AMC Sheffield Clinic",
                "AMC VA Highlands Clinic",
                "Emory Clinic",
                "Grady Clinic"
        };
        final List<String> clinicNames = new ArrayList<>(Arrays.asList(clinic));
        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,android.R.layout.simple_spinner_item,clinicNames){
            @Override
            public boolean isEnabled(int position){
                return position != 0;
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clinics.setAdapter(spinnerArrayAdapter);

        mAuth = FirebaseAuth.getInstance();
        progress_d = Progress_dialog.getInstance();
        prefs = new MedasolPrefs(getApplicationContext());

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
        pharmacyNumber.setFilters(new InputFilter[]{filter});
        physicianNo.setFilters(new InputFilter[]{filter});

        final long today = System.currentTimeMillis() - 1000;
        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //If user tries to select date in past (or today)
                if (myCalendar.getTimeInMillis() < today) {
                    Toast.makeText(SignUpActivity.this, "Discharge date shouldn't be before current date!", Toast.LENGTH_SHORT).show();
                }
                else {
                    updateLabel();
                }
            }
        };
    }

    private void Listener() {
        clinics.setOnItemSelectedListener(this);
        submit.setOnClickListener(this);
        dischargedate.setOnClickListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
       clinicName = parent.getItemAtPosition(pos).toString();
        //((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
        //((TextView) parent.getChildAt(0)).setTextSize(14);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
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
            case R.id.et_dischargedate:
                new DatePickerDialog(SignUpActivity.this,R.style.DialogTheme, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
        }
    }

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dischargedate.setText(sdf.format(myCalendar.getTime()));
    }

    private boolean validation() {
        boolean isValid = true;
        if (userName.getText().toString().trim().equals("")) {
            Toast.makeText(SignUpActivity.this, "Please enter username!", Toast.LENGTH_SHORT).show();
            isValid = false;
        }else if (!CheckUsername(userName.getText().toString().trim())) {
            Toast.makeText(SignUpActivity.this, "ENTER VALID USERNAME!", Toast.LENGTH_LONG).show();
            isValid = false;
        } else if (dischargedate.getText().toString().trim().equals("")) {
            Toast.makeText(SignUpActivity.this, "Please enter Enrollment Date!", Toast.LENGTH_SHORT).show();
            isValid = false;
        }  else if (password.getText().toString().equals("")) {
            Toast.makeText(SignUpActivity.this, "Please enter password!", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else if (password.getText().toString().trim().length() < 6 ) {
            Toast.makeText(SignUpActivity.this, "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else if (cnfPassword.getText().toString().equals("")) {
            Toast.makeText(SignUpActivity.this, "Please enter Confirm password!", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else if (!password.getText().toString().trim().equals(cnfPassword.getText().toString().trim())) {
            Toast.makeText(SignUpActivity.this, "Password Not matching!", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else if (clinicName.equals("Select Clinic Name")) {
            Toast.makeText(SignUpActivity.this, "Please select clinic name!", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else if (pharmacyName.getText().toString().trim().equals("")) {
            Toast.makeText(SignUpActivity.this, "Please enter pharmacy name!", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else if (pharmacyNumber.getText().toString().trim().equals("")) {
            Toast.makeText(SignUpActivity.this, "Please enter pharmacy phone number!", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else if (pharmacyNumber.length()>0 && pharmacyNumber.length()<14){
            Toast.makeText(this, "Please Enter Valid 10 Digit Pharmacy phone Number!", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else if (physicianName.getText().toString().trim().equals("")) {
            Toast.makeText(SignUpActivity.this, "Please enter Physician name!", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else if (physicianNo.getText().toString().trim().equals("")) {
            Toast.makeText(SignUpActivity.this, "Please enter Physician phone number!", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else if (physicianNo.length()>0 && physicianNo.length()<14){
            Toast.makeText(this, "Please Enter Valid 10 Digit Physician phone Number!", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else if (pharmacyNumber.getText().toString().trim().equals(physicianNo.getText().toString().trim())) {
            Toast.makeText(SignUpActivity.this, "Pharmacist phone Number should be different form Pharmacy phone Number!", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        return isValid;
    }
    private boolean CheckUsername(String username) {
        return USERNAME_PATTERN.matcher(username).matches();
    }

    public void startRegistration(View view) {
        progress_d.showProgress(this);
        mAuth.signInWithEmailAndPassword(userName.getText().toString()+"@medadheresolutions.com",password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progress_d.hideProgress();
                try {
                    Exception x = task.getException();
                    String exception = x.toString();

                    if (exception.contains("FirebaseAuthInvalidCredentialsException") | task.isSuccessful()) {
                        Log.e("User", "Exists");
                        Toast.makeText(SignUpActivity.this, "Try a different username.", Toast.LENGTH_LONG).show();
                    } else if (exception.contains("FirebaseAuthInvalidUserException")) {
                        Log.e("No", "USER");
                        prefs.setUsername(userName.getText().toString().trim());
                        prefs.setEmail(userName.getText().toString().trim() + "@medadheresolutions.com");
                        //prefs.setDischargeDate(dischargedate.getText().toString().trim());
                        prefs.setEnrollmentDate(dischargedate.getText().toString().trim());
                        prefs.setPassword(password.getText().toString().trim());
                        prefs.setClinicName(clinicName);
                        prefs.setPharmaName(pharmacyName.getText().toString().trim());
                        prefs.setPharmaNumber(pharmacyNumber.getText().toString().trim());
                        prefs.setPhysicianName(physicianName.getText().toString().trim());
                        prefs.setPhysicianNumber(physicianNo.getText().toString().trim());
                        //Log.d("submitButton", "fields filled");

                        Intent i = new Intent(SignUpActivity.this, DemoSurvey.class);
                        startActivity(i);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                if (task.isSuccessful()) {
                    mAuth.signOut();
                    Toast.makeText(SignUpActivity.this,"Try a different username.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
