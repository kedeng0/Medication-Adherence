package com.appzoro.BP_n_ME.fragment;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

import com.appzoro.BP_n_ME.activity.PatientMonitor;
import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

/**
 * Created by Appzoro_ 5 on 7/20/2017.
 */

public class PatientInformationFragment extends Fragment implements View.OnClickListener, View.OnTouchListener {
    View view;
    LinearLayout layout;
    TextView username, dischargedate, bpgoal, bpvalue, heartrate, bwvalue, clinicname, pharmacyname, pharmacyno, pharmacistname, pharmacistno;
    FloatingActionMenu floatingActionMenu;
    FloatingActionButton addMedication, changePharmacy, dischargeDate;
    EditText editName, editNumber;
    InputFilter filter;
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    ArrayList<String> medArray = new ArrayList<String>();
    ArrayList<String> medFrequency = new ArrayList<String>();
    String frequency = "";
    private String UID, patientname, enDate;
    MedasolPrefs prefs;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_patientinformation, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getActivity().setTitle("Patient Monitor");
        ((PatientMonitor) getActivity()).getSupportActionBar().setSubtitle("Patient Information");
        init();
        getMeds();
        return view;
    }

    private void init() {
        prefs = new MedasolPrefs(getActivity().getApplicationContext());
        UID = prefs.getUID();
        patientname = prefs.getUsername();

        Log.d(">>>>USERNAME", "" + prefs.getUsername());


        layout = view.findViewById(R.id.layout);
        username = view.findViewById(R.id.tv_username);
        dischargedate = view.findViewById(R.id.dischargedate);
        bpgoal = view.findViewById(R.id.bpgoal);
        bpvalue = view.findViewById(R.id.bpvalue);
        //heartrate = view.findViewById(R.id.heartrate);
        //bwvalue = view.findViewById(R.id.bwvalue);
        clinicname = view.findViewById(R.id.clinicname);
        pharmacyname = view.findViewById(R.id.pharmacyname);
        pharmacyno = view.findViewById(R.id.pharmacyno);
        pharmacistname = view.findViewById(R.id.pharmacistname);
        pharmacistno = view.findViewById(R.id.pharmacistno);
        floatingActionMenu = view.findViewById(R.id.floatingactionmenu);
        addMedication = view.findViewById(R.id.addMeds);
        changePharmacy = view.findViewById(R.id.changepharmacy);
        dischargeDate = view.findViewById(R.id.change_dischargedate);


        username.setText(StringUtils.capitalize(prefs.getUsername().toLowerCase().trim()));

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy");
        try {
            Date Date = inputFormat.parse(prefs.getEnrollmentDate());
            enDate = outputFormat.format(Date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dischargedate.setText(enDate);
        bpgoal.setText(prefs.getBloodPressureGoal());
        if (!prefs.getBpValue().equals("")){
            bpvalue.setText(prefs.getBpValue());
        } else {
            bpvalue.setText("N/A");
        }

        /*if (!prefs.getHeartRateValue().equals("")){
            heartrate.setText(prefs.getHeartRateValue());
        } else {
            heartrate.setText("N/A");
        }

        if (!prefs.getBwValue().equals("")){
            bwvalue.setText(prefs.getBwValue());
        } else {
            bwvalue.setText("N/A");
        }*/

        clinicname.setText(prefs.getClinicName());
        pharmacyname.setText(prefs.getPharmaName());
        pharmacyno.setText(prefs.getPharmaNumber());
        pharmacistname.setText(prefs.getPhysicianName());
        pharmacistno.setText(prefs.getPhysicianNumber());

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
                    Toast.makeText(getActivity(), "Discharge date shouldn't be before current date!", Toast.LENGTH_SHORT).show();
                } else {
                    updateDischargeDate();
                }
            }
        };
        addMedication.setOnClickListener(this);
        changePharmacy.setOnClickListener(this);
        dischargeDate.setOnClickListener(this);
        layout.setOnTouchListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addMeds:
                addMeds(view);
                break;
            case R.id.changepharmacy:
                changePharm(view);
                break;
            case R.id.change_dischargedate:
                new DatePickerDialog(getActivity(), R.style.DialogTheme, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

                if (floatingActionMenu.isOpened())
                    floatingActionMenu.close(true);
                break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (floatingActionMenu.isOpened())
            floatingActionMenu.close(true);
        return true;
    }

    private void updateDischargeDate() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        String newFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        SimpleDateFormat newsdf = new SimpleDateFormat(newFormat, Locale.US);
        dischargedate.setText(newsdf.format(myCalendar.getTime()));
        prefs.setDischargeDate(newsdf.format(myCalendar.getTime()));

        mDatabase.child("storage").child("users").child(UID).child("enrollmentdate").removeValue();
        mDatabase.child("storage").child("users").child(UID).child("enrollmentdate").setValue(sdf.format(myCalendar.getTime()));
    }

    public void addMeds(View view) {
        LinearLayout layout = new LinearLayout(view.getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.VERTICAL);

        //Create Spinner
        final Spinner spinner = new Spinner(view.getContext());
        String[] string_list = getResources().getStringArray(R.array.frequency);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, string_list);
        spinner.setAdapter(adapter);
        spinner.setGravity(Gravity.CENTER);
        spinner.setPadding(20, 8, 12, 8);
        spinner.setBackgroundResource(R.drawable.spinner_border);
        spinner.setPopupBackgroundResource(R.color.white);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        param.setMargins(75, 0, 75, 0);//pass int values for left,top,right,bottom
        spinner.setLayoutParams(param);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
                frequency = parent.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Create button
        final EditText edit = new EditText(view.getContext());
        edit.setHint("e.g. 'Tylenol'");
        edit.setHintTextColor(Color.GRAY);
        edit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        edit.setBackgroundResource(R.drawable.edit_text_dynamic_border2);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.setMargins(75, 20, 75, 50);//pass int values for left,top,right,bottom
        edit.setLayoutParams(p);
        //Add Views to the layout
        layout.addView(edit);
        layout.addView(spinner);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Medication");
        builder.setView(layout);
        // set dialog message
        builder
                .setMessage("Would you like to add medication with frequency below?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (frequency.length() > 0 & edit.getText().toString().length() > 0) {
                            String meds = edit.getText().toString().trim();
                            if (meds.contains(".") | meds.contains("/") | meds.contains("[") | meds.contains("]") | meds.contains("#") | meds.contains("$")){
                                Toast.makeText(getActivity(), "Please Enter Valid Information, can't accept '.$#[]/'!", Toast.LENGTH_LONG).show();
                            } else {
                                mDatabase.child("app").child("users").child(UID).child("medicine").child(edit.getText().toString().trim()).child("name").setValue(edit.getText().toString().trim());
                                mDatabase.child("app").child("users").child(UID).child("medicine").child(edit.getText().toString()).child("frequency").setValue(frequency);
                                Toast.makeText(getActivity(), "Medication '" + edit.getText().toString().trim() + "' has been added", Toast.LENGTH_SHORT).show();
                                getMeds();
                                if (floatingActionMenu.isOpened())
                                    floatingActionMenu.close(true);
                            }
                        } else {
                            Toast.makeText(getActivity(), "Please Enter Valid Information!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        if (floatingActionMenu.isOpened())
                            floatingActionMenu.close(true);
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void changePharm(View view) {
        //Create LinearLayout Dynamically
        LinearLayout layout = new LinearLayout(view.getContext());
        //Setup Layout Attributes
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView name = new TextView(view.getContext());
        name.setText("Pharmacy Name");
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ll.setMargins(110, 0, 0, 0);//pass int values for left,top,right,bottom
        name.setLayoutParams(ll);

        TextView number = new TextView(view.getContext());
        number.setText("Pharmacy Number");
        LinearLayout.LayoutParams ll1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ll1.setMargins(110, 0, 0, 0);//pass int values for left,top,right,bottom
        number.setLayoutParams(ll1);

        //Create button
        editName = new EditText(view.getContext());
        editName.setGravity(Gravity.LEFT);
        editName.setBackgroundResource(R.drawable.edit_text_dynamic_border2);
        LinearLayout.LayoutParams pl = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pl.setMargins(75, 20, 75, 20);//pass int values for left,top,right,bottom
        editName.setLayoutParams(pl);

        editNumber = new EditText(view.getContext());
        editNumber.setGravity(Gravity.LEFT);
        editNumber.setInputType(InputType.TYPE_CLASS_PHONE);
        editNumber.setBackgroundResource(R.drawable.edit_text_dynamic_border2);
        LinearLayout.LayoutParams pal = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pal.setMargins(75, 20, 75, 20);//pass int values for left,top,right,bottom
        editNumber.setLayoutParams(pal);

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
                } else {
                }
                return null;
            }
        };
        editNumber.setFilters(new InputFilter[]{filter});

        //Add Views to the layout
        layout.addView(name);
        layout.addView(editName);
        layout.addView(number);
        layout.addView(editNumber);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Change Pharmacy");
        builder.setView(layout);
        builder
                .setMessage("Would you like to change pharmacy name & phone number?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (validation()) {
                            String pharmaname = editName.getText().toString();
                            String pharmanumber = editNumber.getText().toString();

                            prefs.setPharmaName(pharmaname);
                            prefs.setPharmaNumber(pharmanumber);
                            pharmacyname.setText(pharmaname);
                            pharmacyno.setText(pharmanumber);

                            mDatabase.child("storage").child("users").child(UID).child("pharmaname").removeValue();
                            mDatabase.child("storage").child("users").child(UID).child("pharmaname").setValue(pharmaname);
                            mDatabase.child("storage").child("users").child(UID).child("pharmanumber");
                            mDatabase.child("storage").child("users").child(UID).child("pharmanumber").setValue(pharmanumber);
                            if (floatingActionMenu.isOpened())
                                floatingActionMenu.close(true);

                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        if (floatingActionMenu.isOpened())
                            floatingActionMenu.close(true);
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private boolean validation() {
        boolean isValid = true;
        if (editName.getText().toString().trim().equals("")) {
            Toast.makeText(getActivity(), "Please enter Pharmacy Name!", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else if (editNumber.getText().toString().equals("")) {
            Toast.makeText(getActivity(), "Please enter Pharmacy Number!", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else if (editNumber.length() > 0 && editNumber.length() < 14) {
            Toast.makeText(getActivity(), "Please Enter Valid 10 Digit Pharmacy Number!", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        return isValid;
    }

    public void getMeds() {
        medArray.clear();
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
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    public void getMedFrequency(final ArrayList<String> medArray) {
        for (int i = 0; i < medArray.size(); i++) {
            final int finalI = i;
            medFrequency.clear();
            mDatabase.child("app").child("users").child(UID).child("medicine").child(medArray.get(i)).child("frequency").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            medFrequency.add(dataSnapshot.getValue().toString().trim());
                            //Log.e("medFrequency", medFrequency.toString());
                            prefs.setFrequency(medFrequency);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        }
                    });
        }
    }
}
