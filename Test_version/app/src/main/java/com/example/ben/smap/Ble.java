package com.example.ben.smap;

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
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

public class Ble extends AppCompatActivity {
    private static final String TAG = Ble.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 3;
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();// BLE adapter
    private ArrayList<String> device_name_list = new ArrayList<>();
    private ArrayList<BluetoothDevice> device_list = new ArrayList<>();
    ListView ListView;// Locate list view
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);
        ListView = (ListView) findViewById(R.id.device_list);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, device_name_list);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
        // Test if support BLE

        ListView.setOnItemClickListener(mDeviceClickListener);
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Device doesn't support Bluetooth.");// Device doesn't support Bluetooth
        }
        // Test if BLE turned on, request turn on if not
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            Log.e(TAG, "BLE not enabled.");
        }
        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver1, filter);

        IntentFilter BondIntent = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mReceiver2, BondIntent);
//        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
//        Log.i(TAG, "Paired Devices\n");
//        if (pairedDevices.size() > 0) {
//            // There are paired devices. Get the name and address of each paired device.
//            for (BluetoothDevice device : pairedDevices) {
//                String deviceName = device.getName();
//                String deviceHardwareAddress = device.getAddress(); // MAC address
//                Log.i(TAG,deviceName);
//            }
//        }
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();
    }


    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int i, long j) {
            // Cancel discovery because it's costly and we're about to connect
            mBluetoothAdapter.cancelDiscovery();
            BluetoothDevice selected = device_list.get(i);

            // Get the device MAC address, which is the last 17 chars in the View
            String selectedName = selected.getName();
            Log.d(TAG, "Selected Device Name: " + selectedName);
            String selectedAddress = selected.getAddress();
            Log.d(TAG, selectedAddress);
            // Create the result Intent and include the MAC address
//            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
                Log.d(TAG, "Trying to pair with " + selectedName);
                selected.createBond();
            }
        }
    };

    protected void onDestroy() {
        mBluetoothAdapter.cancelDiscovery();
        unregisterReceiver(mReceiver1);
        unregisterReceiver(mReceiver2);

        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
    }

    private final BroadcastReceiver mReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                if (deviceName != null) {
                    Log.i(TAG, deviceName);
                    Log.i(TAG, deviceHardwareAddress);
                    if (!device_name_list.contains(deviceName)) {
                        device_name_list.add(deviceName);
                        ListView.setAdapter(adapter);
                        device_list.add(device);
                    }
                }
            }
        }
    };

    private final BroadcastReceiver mReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");
                }
                //case2: creating a bone
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
                }
                //case3: breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
                }
            }
        }
    };
}
