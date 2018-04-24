package com.appzoro.BP_n_ME.fragment;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.KeyguardManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.model.Pill;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;
import com.appzoro.BP_n_ME.util.BleManager;
import com.appzoro.BP_n_ME.util.MyReceiver;
import com.appzoro.BP_n_ME.util.util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import me.aflak.libraries.dialog.FingerprintDialog;
import me.aflak.libraries.callback.FingerprintDialogCallback;

import static android.content.Context.FINGERPRINT_SERVICE;
import static android.content.Context.KEYGUARD_SERVICE;
import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

/**
 * Created by Kedeng Pan on 13/4/2018.
 */

public class SMAPFragment extends Fragment implements View.OnClickListener, FingerprintDialogCallback {

    private View view;
    private Context context;
    private TextView connectionStatus;
    private Button disconnectButton;
    private Button connectNewDeviceButton;
    private Button dispenseButton;
    private Button reloadButton;
    private TextView numPillTextView;
    private AlertDialog amount_dialog;
    private AlertDialog channel_dialog;
    private Spinner spinner;


    //Log to calendar variables needed
    List<String> medication, frequency;
    String medicine, date, ParseDate, UID;
    MedasolPrefs prefs;
    private DatabaseReference mDatabase;
    double doses;
    int goalFreq, numPill, numChannel;
    SharedPreferences amount_sharedpref;
    SharedPreferences.Editor amount_editor;

    SharedPreferences channel_sharedpref;
    SharedPreferences.Editor channel_editor;

    //Fingerprints
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private static final String KEY_NAME = "smapkey";
    private Cipher cipher;
    private FingerprintManager.CryptoObject cryptoObject;





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
    // Text message
    private String txtMessage = "Hello";
    private String phoneNo = "4049075666";
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    // Alarm service for text message
    final static private long ONE_SECOND = 1000;
    final static private long TWENTY_SECONDS = ONE_SECOND * 20;
    PendingIntent pi;
    BroadcastReceiver br;
    AlarmManager am;

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

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            //ask user permission then restart
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }

        init();
        Listener();

        return view;
    }

    private void init() {



        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY,1);
        calendar.set(Calendar.MINUTE,1);
        calendar.set(Calendar.SECOND,15);


        Intent notifyIntent = new Intent(getContext(), MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 100, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000,pendingIntent);


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
        reloadButton = (Button) view.findViewById(R.id.reload);
        spinner = view.findViewById(R.id.medicationSpinner2);
        numPillTextView = view.findViewById(R.id.pill_amount);

        amount_sharedpref = getActivity().getSharedPreferences("pill_amount", Context.MODE_PRIVATE);
        amount_editor = amount_sharedpref.edit();

        channel_sharedpref = getActivity().getSharedPreferences("channel", Context.MODE_PRIVATE);
        channel_editor = channel_sharedpref.edit();


        //*****************************
        // Amount dialog
        final AlertDialog.Builder amountBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater_amount = this.getLayoutInflater();
        final View dialogView_amount = inflater_amount.inflate(R.layout.dialog_reload_amount, null);
        amountBuilder.setView(dialogView_amount);
        final NumberPicker np = (NumberPicker) dialogView_amount.findViewById(R.id.numberPicker1);
        np.setMaxValue(20);
        np.setMinValue(0);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                numPill = i1;
            }
        });
        amountBuilder.setMessage("Enter amount of pills after reload");
        amountBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                numPillTextView.setText(Integer.toString(numPill));
                channel_dialog.show();

            }
        });
        amountBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                resetAmount();
            }
        });
        amount_dialog = amountBuilder.create();

        //*****************************
        // Channel dialog
        final AlertDialog.Builder channelBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater_channel = this.getLayoutInflater();
        final View dialogView_channel = inflater_channel.inflate(R.layout.dialog_reload_channel, null);
        channelBuilder.setView(dialogView_channel);
        final NumberPicker np2 = (NumberPicker) dialogView_channel.findViewById(R.id.numberPicker2);
        np2.setMaxValue(2);
        np2.setMinValue(1);
        np2.setWrapSelectorWheel(false);
        np2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                numChannel = i1;
            }
        });
        channelBuilder.setMessage("Enter the Channel You are Reloading");
        channelBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                UpdateChannel(numChannel);
                UpdatePillAmount(numPill);

            }
        });
        channelBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                resetAmount();
                resetChannel();
            }
        });
        channel_dialog = channelBuilder.create();

//
//        ************Get Pill From Database***************
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

        ParseDate = outputFormat.format(new Date());

        Log.e("ParseDate",ParseDate);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        prefs = new MedasolPrefs(getActivity().getApplicationContext());
        UID = prefs.getUID();
        if(prefs.getMeds().equals("[]")) {
            medication = new ArrayList<>();
        } else {
            medication =  new ArrayList<String>(Arrays.asList(prefs.getMeds().trim().replace("[","").replace("]","").replace(" ","").split(",")));

        }
        if (prefs.getFreqList().equals("[]")) {
            frequency = new ArrayList<>();
        } else {
            frequency =  new ArrayList<String>(Arrays.asList(prefs.getFreqList().trim().replace("[","").replace("]","").split(", ")));
        }

        setArray(medication);

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

//        mDatabase.child("app").child("users").child(UID).child("medicineLog").child(ParseDate).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                medication.clear();
//                Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
//                while (it.hasNext()) {
//                    DataSnapshot medicine = it.next();
//                    String medsDate = medicine.getKey();
//                    String Medication = medicine.getValue().toString();
//                    //Log.e("Medication Date", medsDate);
//                    //Log.e("Medication ", Medication);
//                    medication.add(Medication);
//                }
//                //Log.e("meds size", String.valueOf(medication.size()));
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
//            }
//        });
//**************************
//        Get Pill from internal storage file.
//        String filename = "b.txt";
//        File file = new File(getActivity().getFilesDir(), filename);
//        medication =  new ArrayList<String>();
//        try {
//            FileInputStream fis = new FileInputStream(file);
//            DataInputStream in = new DataInputStream(fis);
//            BufferedReader br = new BufferedReader(new InputStreamReader(in));
//
//            String strLine;
//            while ((strLine = br.readLine()) != null) {
//
//                String[] stringArray = strLine.split(", ");
//                Log.d("DEBUG",stringArray[0]+stringArray[1]+stringArray[2]+stringArray[3]);
//                medication.add(stringArray[0]);
//
//            }
//            in.close();
//            fis.close();
//        }
//        catch (Exception e) {
//            Log.e("Exception", "File read failed: " + e.toString());
//        }
//        Collections.sort(medication);
//        setArray(medication);

    }



    private void Listener() {
        disconnectButton.setOnClickListener(this);
        connectNewDeviceButton.setOnClickListener(this);
        dispenseButton.setOnClickListener(this);
        reloadButton.setOnClickListener(this);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        am.cancel(pi);
//        getActivity().unregisterReceiver(br);
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
            case R.id.reload:
                reload();
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

        numChannel = channel_sharedpref.getInt(medicine,0);
        numPill = amount_sharedpref.getInt(medicine,0);
        if (numPill < 4) {
            //AlertDialog
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Please reload to at least 4 pills.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            return;
        }

        if (mBleManager.getConnectionStatus()) {
            if(FingerprintDialog.isAvailable(getContext())) {
                FingerprintDialog.initialize(getContext())
                        .title("Dispense Pill")
                        .message("Please provide Fingerprint")
                        .callback(this)
                        .show();
            }
        } else {
            util.toast(context, "Not connected to any device");
        }
    }

    @Override
    public void onAuthenticationSucceeded() {
        // Logic when fingerprint is recognized
        if (mBleManager.dispense(numChannel)) {
            //log pill taking behavior in the calendar
            submit();
            numPill--;
            numPillTextView.setText(Integer.toString(numPill));
            UpdatePillAmount(numPill);
        }
    }

    @Override
    public void onAuthenticationCancel() {
        // Logic when user canceled operation
    }

    public void reload() {
        amount_dialog.show();
    }

    private void UpdatePillAmount(int num) {
        amount_editor.putInt(medicine, num);
        amount_editor.apply();
    }

    private void resetAmount() {
        numPill = amount_sharedpref.getInt(medicine,0);
    }

    private void UpdateChannel(int num) {
        channel_editor.putInt(medicine, num);
        channel_editor.apply();
    }

    private void resetChannel() {
        numChannel = channel_sharedpref.getInt(medicine,0);
    }

    //This function records the current pill-taking behavior to the firebase databse.
    private void submit() {
        Calendar calander = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String curtime = simpleDateFormat.format(calander.getTime());
        if (medicine !=" ") {
            int entryNo = 0;
            int medsSize = 0;
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
            for (int i=0;i<medication.size();i++){
                if (medication.get(i).contains(medicine)){
                    medsSize++;
                }
            }
            //Log.e("medsSize", ""+medsSize);

            if (!(medsSize > entryNo)){
                mDatabase.child("app").child("users").child(UID).child("medicineLog").child(ParseDate).child(curtime).setValue(medicine);

                Log.e("size", ""+ medication.size() +"   "+goalFreq );
                doses = ((double) (medication.size()+1) / goalFreq) * 100;
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
                if (medicine != null ){
                    numPill = amount_sharedpref.getInt(medicine,0);
                    numPillTextView.setText(Integer.toString(numPill));
                }


            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                //spinner.setPrompt(records[0]);
            }
        });
    }


    //*****************
    // Text message
    // This Method for Send a message
//    protected void sendSMSMessage() {
//        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.SEND_SMS)) {
//            } else {
//                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
//            }
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    SmsManager smsManager = SmsManager.getDefault();
//                    smsManager.sendTextMessage(phoneNo, null, txtMessage, null, null);
//                    Toast.makeText(getActivity().getApplicationContext(), "SMS sent.",
//                            Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(getActivity().getApplicationContext(),
//                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
//                    return;
//                }
//            }
//        }
//
//    }

    //*****************
    // Alarm service for text message
    private void setup() {
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent i) {
                Toast.makeText(c, "Rise and Shine!", Toast.LENGTH_LONG).show();
            }
        };
        getActivity().registerReceiver(br, new IntentFilter("com.authorwjf.wakeywakey") );
        pi = PendingIntent.getBroadcast( getActivity(), 0, new Intent("com.authorwjf.wakeywakey"),
                0 );
        am = (AlarmManager)(getActivity().getSystemService( Context.ALARM_SERVICE ));
    }
    //************
    // Add this to trigger event
    // am.set( AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() +TWENTY_SECONDS, pi );
    //***************
}