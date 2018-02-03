package com.appzoro.BP_n_ME.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.model.Medication;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;
import com.appzoro.BP_n_ME.util.util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class NFCTag extends AppCompatActivity {
    Tag detectedTag;
    TextView txtType,txtSize,txtWrite,txtRead,txtID;
    NfcAdapter nfcAdapter;
    IntentFilter[] readTagFilters;
    PendingIntent pendingIntent;
    String UID;
    String medicine;
    Boolean didLog = false;
    private DatabaseReference mDatabase;
    MedasolPrefs prefs;
    List<String> medsList = new ArrayList<>();
    List<String> medication, frequency;
    String date;
    int entryNo, medsSize;
    double doses;
    int goalFreq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .48));
        setContentView(R.layout.activity_nfctag);

        prefs = new MedasolPrefs(getApplicationContext());
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        detectedTag =getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);

        txtType  = (TextView) findViewById(R.id.txtType);
        txtSize  = (TextView) findViewById(R.id.txtsize);
        txtWrite = (TextView) findViewById(R.id.txtwrite);
        txtRead  = (TextView) findViewById(R.id.txtread);
        txtID  = (TextView) findViewById(R.id.txtid);

        pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(this,getClass()).
                        addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter filter2     = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        readTagFilters = new IntentFilter[]{tagDetected,filter2};

        final Intent i = getIntent();
        String action = i.getAction();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //setContentView(R.layout.activity_medication_log);///////////////////////////////////////////////////////////////
        setContentView(R.layout.activity_nfctag);

        /*final String [] medNames = new String[prefs.getMeds().size()];
        for(int ind=0; ind < prefs.getMeds().size(); ind++){
            medNames[ind] = prefs.getMeds().get(ind);
        }
        setArray(medNames);*/

        medication =  new ArrayList<String>(Arrays.asList(prefs.getMeds().replace("[","").replace("]","").replace(" ","").split(",")));
        Log.e("meds", String.valueOf(medication));

        for(int ind = 0; ind<medication.size(); ind++){
            medication.set(ind, medication.get(ind));
        }
        setArray(medication);
        frequency =  new ArrayList<String>(Arrays.asList(prefs.getFreqList().trim().replace("[","").replace("]","").split(", ")));

        mDatabase = FirebaseDatabase.getInstance().getReference();
        UID = prefs.getUID();

        Calendar rightNow = Calendar.getInstance();
        int iyear = rightNow.get(Calendar.YEAR);
        int imonth = rightNow.get(Calendar.MONTH) + 1;
        int iday = rightNow.get(Calendar.DAY_OF_MONTH);
        int ihour = rightNow.get(Calendar.HOUR_OF_DAY);
        int iminute = rightNow.get(Calendar.MINUTE);

        String year = Integer.toString(iyear);
        String month;
        if (imonth<10) {
            month = "0" + Integer.toString(imonth);
        }else
        {
            month = Integer.toString(imonth);
        }
        String day;
        if (iday<10) {
            day = "0" + Integer.toString(iday);
        }else
        {
            day = Integer.toString(iday);
        }
        date = year + "-" + month + "-" + day;
        Log.e("DATE:",date);


        mDatabase.child("app").child("users").child(UID).child("medicineLog").child(date).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                medsList.clear();
                Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                while (it.hasNext()) {
                    DataSnapshot medicine = it.next();
                    String medsDate = medicine.getKey();
                    String Medication = medicine.getValue().toString();
                    //Log.e("Medication Date", medsDate);
                    //Log.e("Medication ", Medication);
                    medsList.add(Medication);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });

        goalFreq = 0;
        for (int j = 0; j < frequency.size(); j++) {
            if (frequency.get(j).equals("Daily")) {
                goalFreq += 1;
            }
            if (frequency.get(j).equals("Twice daily")) {
                goalFreq += 2;
            }
            if (frequency.get(j).equals("Three times daily")) {
                goalFreq += 3;
            }
            if (frequency.get(j).equals("Four times per day")) {
                goalFreq += 4;
            }
        }
        //Log.e("totalfreq", String.valueOf(goalFreq));
    }

    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        if(getIntent().getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)){
           if (prefs.getType() == 2){
               detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
               readFromTag(getIntent());
           } else {
               Toast.makeText(this, "Please Login as a Patient!", Toast.LENGTH_SHORT).show();
           }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, readTagFilters, null);
    }

    public void readFromTag(Intent intent){
        Ndef ndef = Ndef.get(detectedTag);

        try{
            ndef.connect();
            ndef.getNdefMessage();
            String value = new String(ndef.getTag().getId(), "UTF-8");
            txtID.setText(value);

            mDatabase = FirebaseDatabase.getInstance().getReference();
            UID = prefs.getUID();

            Calendar rightNow = Calendar.getInstance();
            int iyear = rightNow.get(Calendar.YEAR);
            int imonth = rightNow.get(Calendar.MONTH) + 1;
            int iday = rightNow.get(Calendar.DAY_OF_MONTH);
            int ihour = rightNow.get(Calendar.HOUR_OF_DAY);
            int iminute = rightNow.get(Calendar.MINUTE);

            String year = Integer.toString(iyear);
            String month;
            if (imonth<10) {
                month = "0" + Integer.toString(imonth);
            }else
            {
                month = Integer.toString(imonth);
            }
            String day;
            if (iday<10) {
                day = "0" + Integer.toString(iday);
            }else
            {
                day = Integer.toString(iday);
            }
            date = year + "-" + month + "-" + day;
            Log.e("DATE:",date);

            Log.v("NFC:","Detected");
            mDatabase.child("app").child("users").child(UID).child("medicine")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String nfcID = txtID.getText().toString();
                            Log.v("NFC ID:",nfcID);
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Medication MedName = snapshot.getValue(Medication.class);
                                System.out.println(MedName.name);
                                System.out.println(MedName.id);

                                for(int retry = 0;retry < 2;retry++) {
                                    try {
                                        if (MedName.id.equals(nfcID)) {
                                            Log.v("NFC:", "Identified");
                                            Log.v("NFC MedID:", MedName.name);
                                            medicine = MedName.name;

                                            entryNo = 0;
                                            medsSize = 1;
                                            Log.e("medication size", ""+medication.size());
                                            for (int j = 0;j<medication.size();j++){
                                                if (medication.get(j).contains(medicine)){
                                                    String freq = frequency.get(j);
                                                    Log.e("<<<<<<<<<<", medicine +" "+  freq);
                                                    if (frequency.get(j).equals("Daily")){
                                                        entryNo = 1;
                                                    }
                                                    else if (frequency.get(j).equals("Twice daily")){
                                                        entryNo = 2;
                                                    }
                                                    else if (frequency.get(j).equals("Three times daily")){
                                                        entryNo = 3;
                                                    } else if (frequency.get(j).equals("Four times per day")) {
                                                        entryNo = 4;
                                                    }
                                                }
                                            }
                                            Log.e("entryNo final",""+entryNo);
                                            for (int i1=0;i1<medsList.size();i1++){
                                                if (medsList.get(i1).contains(medicine)){
                                                    medsSize++;
                                                }
                                            }
                                            Log.e("medsSize",""+ medsSize);
                                            if (!(medsSize > entryNo)){
                                                mDatabase.child("app").child("users").child(UID).child("medicineLog").child(date).child(util.getTimeStamp()).setValue(medicine);

                                                Log.e("size", ""+ medsList.size() +"   "+goalFreq );
                                                doses = ((double) (medsList.size()+1) / goalFreq) * 100;
                                                Log.e("doses", ""+ doses );
                                                if (doses >= 80){
                                                    mDatabase.child("app").child("users").child(UID).child("MedicalCondition").child(date).setValue("green");
                                                } else {
                                                    mDatabase.child("app").child("users").child(UID).child("MedicalCondition").child(date).setValue("red");
                                                }

                                                entryNo = 0;
                                                medsSize = 1;
                                                doses = 0;

                                                didLog = true;
                                                TextView RWmode = (TextView) findViewById(R.id.NFC_RW);
                                                RWmode.setText("NFC tag recognized. Logging "+medicine +"...");
                                                mDatabase.child("app").child("users").child(UID).child("medicine").child(medicine).child("id").setValue("PlaceHolderIDvalue");
                                                Toast.makeText(NFCTag.this, "Your medication '"+medicine+"' has been logged!", Toast.LENGTH_LONG).show();
                                            } else {
                                                mDatabase.child("app").child("users").child(UID).child("medicine").child(medicine).child("id").setValue("PlaceHolderIDvalue");
                                                didLog = true;
                                                Toast.makeText(NFCTag.this, "This number of doses exceeds your prescribed frequency!", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }catch(NullPointerException e){
                                        e.printStackTrace();
                                        //Toast.makeText(NFCTag.this, "Error..... "+ medicine , Toast.LENGTH_LONG).show();
                                        //mDatabase.child("app").child("users").child(UID).child("medicine").child(medicine).child("id").setValue("PlaceHolderIDvalue");
                                    }
                                }
                            }
                            if (!didLog) {
                                Log.v("NFC","Unrecognized");
                                TextView RWmode = (TextView) findViewById(R.id.NFC_RW);
                                RWmode.setText("NFC tag unrecognized, select the medication you would like to assign to this unique NFC tag.");
                                RelativeLayout NFCspinnerLayout = (RelativeLayout) findViewById(R.id.spinnerLayout);
                                NFCspinnerLayout.setVisibility(View.VISIBLE);
                                NFCspinnerLayout.setClickable(true);
                                final TextView submitNFC = (TextView) findViewById(R.id.btnSubmit);
                                submitNFC.setVisibility(View.VISIBLE);
                                submitNFC.setClickable(true);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
            txtType.setText(ndef.getType());
            txtSize.setText(String.valueOf(ndef.getMaxSize()));
            txtWrite.setText(ndef.isWritable() ? "True" : "False");
            Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if (messages != null) {
                NdefMessage[] ndefMessages = new NdefMessage[messages.length];
                for (int i = 0; i < messages.length; i++) {
                    ndefMessages[i] = (NdefMessage) messages[i];
                }
                NdefRecord record = ndefMessages[0].getRecords()[0];

                byte[] payload = record.getPayload();
                String text = new String(payload);
                txtRead.setText(text);
                ndef.close();
            }
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Cannot Read From Tag.", Toast.LENGTH_LONG).show();
        }
    }

    public void setArray(List x){
        final Spinner NFCspinner = (Spinner) findViewById(R.id.NFCmedicationSpinner);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, x);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        NFCspinner.setAdapter(spinnerArrayAdapter);

        NFCspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                medicine = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    public void submitID(View view){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String UID = prefs.getUID();
        String nfcID = txtID.getText().toString();
        if (medicine != " ") {
            Toast.makeText(this, "nfcID: "+nfcID, Toast.LENGTH_SHORT).show();
            mDatabase.child("app").child("users").child(UID).child("medicine").child(medicine).child("id").setValue(nfcID);
        }
        else{
            Toast.makeText(this, "Please select a medication!", Toast.LENGTH_SHORT).show();
        }
    }
    public void onClose(View view){finish();}
}
