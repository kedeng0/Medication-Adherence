package com.appzoro.BP_n_ME.fragment;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;
import com.appzoro.BP_n_ME.util.BleManager;
import com.appzoro.BP_n_ME.util.util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

/**
 * Created by Kedeng Pan on 13/4/2018.
 */

public class SMAPFragment extends Fragment implements View.OnClickListener {

    private View view;
    private Context context;
    private TextView connectionStatus;
    private Button disconnectButton;
    private Button connectNewDeviceButton;
    private Button dispenseButton;
    Spinner spinner;


    //Log to calendar variables needed
    List<String> medsList = new ArrayList<>();
    List<String> medication, frequency;
    String medicine, date, ParseDate, UID;
    MedasolPrefs prefs;
    private DatabaseReference mDatabase;
    double doses;
    int goalFreq;

    //Connect to BLE Device variable needed
    private BleManager mBleManager;
    private boolean mConnected = false;

//    public SMAPFragment() {
//        Required empty public constructor
//    }

//    public static SMAPFragment newInstance() {
//        SMAPFragment fragment = new SMAPFragment();
//        return fragment;
//    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_item_status, container, false);
        context = container.getContext();

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getActivity().setTitle("Hardware Status");

        getContext().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        mBleManager = BleManager.getInstance(getContext());

        int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED)
        {
            //ask user permission then restart
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }

        init();
        Listener();


//        mBleManager.readCharacteristic(mBleManager.getBatteryChar());
        return view;
    }

    private void init() {
        connectionStatus = (TextView) view.findViewById(R.id.connection);

        if (mBleManager.getConnectionStatus()) {
            mConnected = true;
            connectionStatus.setText("Connected");
            connectionStatus.setTextColor(GREEN);
        } else {
            mConnected = false;
            connectionStatus.setText("Disconnected");
            connectionStatus.setTextColor(RED);
        }

        disconnectButton = (Button) view.findViewById(R.id.disconnect);
        connectNewDeviceButton = (Button) view.findViewById(R.id.newconnection);
        dispenseButton = (Button) view.findViewById(R.id.dispense);
        spinner = view.findViewById(R.id.medicationSpinner2);

        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

        ParseDate = outputFormat.format(new Date());

        Log.e("ParseDate",ParseDate);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        prefs = new MedasolPrefs(getActivity().getApplicationContext());
        UID = prefs.getUID();

        medication =  new ArrayList<String>(Arrays.asList(prefs.getMeds().replace("[","").replace("]","").replace(" ","").split(",")));
        Log.e("meds", String.valueOf(medication));

        for(int ind = 0; ind<medication.size(); ind++){
            medication.set(ind, medication.get(ind));
        }

        setArray(medication);
        frequency =  new ArrayList<String>(Arrays.asList(prefs.getFreqList().trim().replace("[","").replace("]","").split(", ")));

        goalFreq = 0;
        for (int i = 0; i < frequency.size(); i++) {
            if (frequency.get(i).equals("Daily")) {
                goalFreq += 1;
            }
            if (frequency.get(i).equals("Twice daily")) {
                goalFreq += 2;
            }
            if (frequency.get(i).equals("Three times daily")) {
                goalFreq += 3;
            }
            if (frequency.get(i).equals("Four times per day")) {
                goalFreq += 4;
            }
        }
        //Log.e("totalfreq", String.valueOf(goalFreq));

        mDatabase.child("app").child("users").child(UID).child("medicineLog").child(ParseDate).addValueEventListener(new ValueEventListener() {
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
                //Log.e("meds size", String.valueOf(medsList.size()));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });

    }



    private void Listener() {
        disconnectButton.setOnClickListener(this);
        connectNewDeviceButton.setOnClickListener(this);
        dispenseButton.setOnClickListener(this);




    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getContext().unregisterReceiver(mGattUpdateReceiver);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.disconnect:
                disconnectDevice();
                break;
            case R.id.newconnection:
                connectNewDevice();
                break;
            case R.id.dispense:
                dispense();
                break;
        }
    }

    public void disconnectDevice() {
        mBleManager.disconnect();
    }

    public void connectNewDevice() {
        BleDevicesFragment fragment = new BleDevicesFragment();
        getFragmentManager().beginTransaction().replace
                (R.id.Fragment_frame_main_activity, fragment).commit();
    }

    public void dispense() {

        if (mBleManager.getConnectionStatus()) {
            if (mBleManager.dispense()) {
                //log pill taking behavior in the calendar
                submit();
            }
        } else {
            util.toast(context, "Not connected to any device");
        }


    }

    private void submit() {
        Calendar calander = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String curtime = simpleDateFormat.format(calander.getTime());
        if (medicine !=" ") {
            int entryNo = 0;
            int medsSize = 1;
            for (int j = 0;j<medication.size();j++){
                if (medication.get(j).contains(medicine)){
                    String freq = frequency.get(j);
                    Log.e("<<<<<<<<<<", medicine +" "+  freq);
                    if (frequency.get(j).equals("Daily")){
                        entryNo = 1;
                    } else if (frequency.get(j).equals("Twice daily")){
                        entryNo = 2;
                    } else if (frequency.get(j).equals("Three times daily")){
                        entryNo = 3;
                    } else if (frequency.get(j).equals("Four times per day")) {
                        entryNo = 4;
                    }
                }
            }
            //Log.e("entryNo",""+entryNo );
            for (int i=0;i<medsList.size();i++){
                if (medsList.get(i).contains(medicine)){
                    medsSize++;
                }
            }
            //Log.e("medsSize", ""+medsSize);

            if (!(medsSize > entryNo)){
                mDatabase.child("app").child("users").child(UID).child("medicineLog").child(ParseDate).child(curtime).setValue(medicine);

                Log.e("size", ""+ medsList.size() +"   "+goalFreq );
                doses = ((double) (medsList.size()+1) / goalFreq) * 100;
                Log.e("doses", ""+ doses );
                if (doses >= 80){
                    mDatabase.child("app").child("users").child(UID).child("MedicalCondition").child(ParseDate).setValue("green");
                } else {
                    mDatabase.child("app").child("users").child(UID).child("MedicalCondition").child(ParseDate).setValue("red");
                }

                //TODO: add cancel of repeating alarm within a time frame

                    /*//10AM  limit to 5AM to 12PM
                    if((timePicker.getCurrentHour()<12)&(timePicker.getCurrentHour()>5)){
                        final Intent intent = getActivity().getIntent();
                        Log.e("Should Cancel", "sending 4");
                        *//*Intent i = new Intent(getActivity(), ReminderService.class);
                        i.putExtra("type", 4);
                        getActivity().startService(i);*//*
                    }
                    //2PM  limit to 12PM to 4PM
                    if((timePicker.getCurrentHour()<16)&(timePicker.getCurrentHour()>=12)){
                        Log.e("Alarm", "sending....");
                        Intent alarmIntent = new Intent(getActivity(), MyAlarmReceiver.class);
                        alarmIntent.setData(Uri.parse("custom://" + 1000002));
                        alarmIntent.setAction(String.valueOf(1000002));
                        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                        PendingIntent displayIntent = PendingIntent.getBroadcast(getActivity(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.cancel(displayIntent);
                    }
                    //8PM  limit to 4PM to 12AM
                    if((timePicker.getCurrentHour()<24)&(timePicker.getCurrentHour()>=14)){
                        Intent alarmIntent = new Intent(getActivity(), MyAlarmReceiver.class);
                        alarmIntent.setData(Uri.parse("custom://" + 1000003));
                        alarmIntent.setAction(String.valueOf(1000003));
                        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                        PendingIntent displayIntent = PendingIntent.getBroadcast(getActivity(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.cancel(displayIntent);
                    }*/
                int colonNdx = curtime.indexOf(":");
                String subKey = curtime.substring(0,colonNdx);
                String subKey1 = curtime.substring(colonNdx,curtime.length());
                int HR =Integer.parseInt(subKey);
                //int MIN =Integer.parseInt(subKey1);

                if(HR >= 12){
                    if(HR > 12){
                        HR = HR - 12;
                    }
                    if (HR < 10){
                        Toast.makeText(getActivity(), "Your medication '"+medicine+"' has been logged at " + "0" + HR + subKey1 + " PM ", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getActivity(), "Your medication '"+medicine+"' has been logged at " + HR + subKey1 + " PM ", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getActivity(), "Your medication '"+medicine+"' has been logged at " + curtime+ " AM ", Toast.LENGTH_LONG).show();
                }
            }
            else {
                Toast.makeText(getActivity(), "This number of doses exceeds your prescribed frequency!", Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(getActivity(), "Please select a medication!", Toast.LENGTH_SHORT).show();
        }
    }


    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BleManager.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                connectionStatus.setText("Connected");
                connectionStatus.setTextColor(GREEN);
            } else if (BleManager.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                connectionStatus.setText("Disconnected");
                connectionStatus.setTextColor(RED);
            } else if (BleManager.ACTION_DATA_AVAILABLE.equals(action)) {
                util.toast(context, intent.getStringExtra(BleManager.EXTRA_DATA));
            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleManager.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BleManager.ACTION_GATT_DISCONNECTED);
//        intentFilter.addAction(BleManager.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BleManager.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    public void setArray(List x){
        // Application of the Array to the Spinner
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, x);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinner.setAdapter(spinnerArrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                //Log.v("item", (String) parent.getItemAtPosition(position));
                medicine = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                //spinner.setPrompt(records[0]);
            }
        });
    }
}