package com.example.ben.test_version_2;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ItemStatusFragment extends Fragment implements  View.OnClickListener {

    private TextView connectionStatus;
    private Button disconnectButton;
    private Button connectNewDeviceButton;

    private BleManager mBleManager = BleManager.getInstance(getContext());
    private boolean mConnected = false;

    public ItemStatusFragment() {
        // Required empty public constructor
    }
    public static ItemStatusFragment newInstance() {
        ItemStatusFragment fragment = new ItemStatusFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View res = inflater.inflate(R.layout.fragment_item_status, container, false);
        getContext().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());


        connectionStatus = (TextView) res.findViewById(R.id.connection);
        if (mBleManager.getConnectionStatus()) {
            mConnected = true;
            connectionStatus.setText("Connected");
        } else {
            mConnected = false;
            connectionStatus.setText("Disconnected");
        }

        disconnectButton = (Button)res.findViewById(R.id.disconnect);
        disconnectButton.setOnClickListener(this);
        connectNewDeviceButton = (Button)res.findViewById(R.id.newconnection);
        connectNewDeviceButton.setOnClickListener(this);
        return res;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getContext().unregisterReceiver(mGattUpdateReceiver);
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.disconnect:
                disconnectDevice();
                break;
            case R.id.newconnection:
                connectNewDevice();
                break;
        }
    }

    public void disconnectDevice(){
        mBleManager.disconnect();
    }

    public void connectNewDevice() {
        Intent intent = new Intent(getContext(), Ble.class);
        startActivity(intent);
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
            } else if (BleManager.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                connectionStatus.setText("Disconnected");
            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleManager.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BleManager.ACTION_GATT_DISCONNECTED);
//        intentFilter.addAction(BleManager.ACTION_GATT_SERVICES_DISCOVERED);
//        intentFilter.addAction(BleManager.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }


}
