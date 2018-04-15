package com.appzoro.BP_n_ME.fragment;

import android.content.pm.ActivityInfo;
import android.support.v4.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.activity.MainActivity;
import com.appzoro.BP_n_ME.util.BleDevicesScanner;
import com.appzoro.BP_n_ME.util.BleManager;
import com.appzoro.BP_n_ME.util.util;

import java.util.ArrayList;

public class BleDevicesFragment extends Fragment implements View.OnClickListener {


    private View view;
    private static final String TAG = BleDevicesFragment.class.getSimpleName();
    public static final int REQUEST_ENABLE_BT = 1;
    private ArrayList<BluetoothDevice> device_list = new ArrayList<>();
    private ArrayList<String> device_name_list = new ArrayList<>();
    private ListView mListView;// Locate list view
    ArrayAdapter<String> adapter;
    BleDevicesScanner mBleDevicesScanner;
    BleManager mBleManager;
    ImageView img_back;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_bledevices, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getActivity().setTitle("Hardware Status");

        init();


//        mBleManager.readCharacteristic(mBleManager.getBatteryChar());
        return view;
    }
    private void init() {
//        setContentView(R.layout.fragment_bledevices);
        if (!getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            util.toast(getContext(), "BLE not supported");
            return;
        }

//        mListView = (ListView) findViewById(R.id.device_list);
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, device_name_list);

        mListView = (ListView) view.findViewById(R.id.device_list);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(mDeviceClickListener);

        img_back = view.findViewById(R.id.iv_back);
        img_back.setOnClickListener(this);




        mBleDevicesScanner = new BleDevicesScanner(this, 2500, -75);
        mBleManager = BleManager.getInstance(getContext());

        if (mBleManager.getConnectionStatus()) {
            mBleManager.disconnect();
        }

        startScan();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                //getFragmentManager().popBackStack();
                getFragmentManager().beginTransaction().replace
                        (R.id.Fragment_frame_main_activity, new SMAPFragment()).commit();
                break;
        }
    }


    public void startScan(){
        device_list.clear();
        device_name_list.clear();
        Log.d(TAG,"start scan in BleDevicesFragment")    ;

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
        return mBleManager.connect(getContext(), device.getAddress());

    }


    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int i, long j) {
            BluetoothDevice selected = device_list.get(i);

            // Get the device MAC address, which is the last 17 chars in the View
            String selectedName = selected.getName();
            Log.d(TAG, "Selected Device Name: " + selectedName);
            String selectedAddress = selected.getAddress();
            Log.d(TAG, selectedAddress);

//            Intent intent = new Intent(Ble.this, HomePage.class);
//            startActivity(intent);
            if (connectDevice(selected)) {
                util.toast(getContext(),"Connected to " + selected.getName());
                
                getFragmentManager().beginTransaction().replace
                                (R.id.Fragment_frame_main_activity, new SMAPFragment()).commit();

                getFragmentManager().popBackStack();

            } else {
                util.toast(getContext(),"Failed to connect to " + selected.getName());
            }
        }
    };




}
