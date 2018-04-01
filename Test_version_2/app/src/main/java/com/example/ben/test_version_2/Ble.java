package com.example.ben.test_version_2;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.content.pm.PackageManager;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Ble extends AppCompatActivity {
    private static final String TAG = Ble.class.getSimpleName();
    public static final int REQUEST_ENABLE_BT = 1;
    private ArrayList<BluetoothDevice> device_list = new ArrayList<>();
    private ArrayList<String> device_name_list = new ArrayList<>();
    ListView mListView;// Locate list view
    ArrayAdapter<String> adapter;
    BroadcastReceiver_BTState mBTStateUpdateReceiver;
    BleDevicesScanner mBleDevicesScanner;
    BleManager mBleManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Utils.toast(getApplicationContext(), "BLE not supported");
            finish();
        }

//        mListView = (ListView) findViewById(R.id.device_list);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, device_name_list);

        mListView = (ListView) findViewById(R.id.device_list);
        mListView.setAdapter(adapter);
//        ((ScrollView) findViewById(R.id.scrollView)).addView(mListView);

        mListView.setOnItemClickListener(mDeviceClickListener);


        mBTStateUpdateReceiver = new BroadcastReceiver_BTState(getApplicationContext());
        mBleDevicesScanner = new BleDevicesScanner(this, 5000, -75);
        mBleManager = BleManager.getInstance(this);



        startScan();



    }


    public void startScan(){
        device_list.clear();
        device_name_list.clear();

        mBleDevicesScanner.start();
    }

    public void stopScan() {

        mBleDevicesScanner.stop();
    }

    public void addDevice(BluetoothDevice device, int rssi) {
        if (!device_list.contains(device) && device != null) {
            String device_name = device.getName();
            if (device_name != null) {
                device_list.add(device);
                device_name_list.add(device.getName());

                mListView.setAdapter(adapter);

            }


        }

    }

    private boolean connectDevice(BluetoothDevice device) {
        return mBleManager.connect(this, device.getAddress());

    }


    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int i, long j) {
            BluetoothDevice selected = device_list.get(i);

            // Get the device MAC address, which is the last 17 chars in the View
            String selectedName = selected.getName();
            Log.d(TAG, "Selected Device Name: " + selectedName);
            String selectedAddress = selected.getAddress();
            Log.d(TAG, selectedAddress);

            if (connectDevice(selected)) {
                Utils.toast(getApplicationContext(),"Connected to " + selected.getName());
                Intent intent = new Intent(Ble.this, HomePage.class);
                startActivity(intent);
            } else {
                Utils.toast(getApplicationContext(),"Failed to connect to " + selected.getName());
            }
        }
    };

    protected void onDestroy() {


        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
    }


}
