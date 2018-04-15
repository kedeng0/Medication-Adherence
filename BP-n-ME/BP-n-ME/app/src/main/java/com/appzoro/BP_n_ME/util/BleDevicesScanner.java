package com.appzoro.BP_n_ME.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.appzoro.BP_n_ME.activity.MainActivity;
import com.appzoro.BP_n_ME.fragment.BleDevicesFragment;

/**
 * Created by admin on 22/3/2018.
 */

public class BleDevicesScanner {

    private static final String TAG = BleDevicesScanner.class.getSimpleName();
    private BleDevicesFragment ma;

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;

    private long scanPeriod;
    private int signalStrength;

    public BleDevicesScanner(BleDevicesFragment bleDevicesFragment, long scanPeriod, int signalStrength) {
        ma = bleDevicesFragment;

        mHandler = new Handler();

        this.scanPeriod = scanPeriod;
        this.signalStrength = signalStrength;

        final BluetoothManager bluetoothManager =
                (BluetoothManager) ma.getContext().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    public boolean isScanning() {
        return mScanning;
    }

    public void start() {
        if (!util.checkBluetooth(mBluetoothAdapter)) {
            util.requestUserBluetooth(ma.getActivity());
            stop();
        }
        else {
            Log.d(TAG,"Start Scan in BleDevicesScanner");
            scanLeDevice(true);
        }
    }

    public void stop() {
//        util.toast(ma.getContext(), "Stopping BLE scan...");
        scanLeDevice(false);
    }

    // If you want to scan for only specific types of peripherals,
    // you can instead call startLeScan(UUID[], BluetoothAdapter.LeScanCallback),
    // providing an array of UUID objects that specify the GATT services your app supports.
    private void scanLeDevice(final boolean enable) {
        if (enable && !mScanning) {
            util.toast(ma.getContext(), "Starting BLE scan...");

            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);

                    stop();
                }
            }, scanPeriod);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
//            mBluetoothAdapter.startLeScan(uuids, mLeScanCallback);
        }
        else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

                    final int new_rssi = rssi;
//                    if (rssi > signalStrength) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "Found Device: "+ device.getName());
                                ma.addDevice(device, new_rssi);
                            }
                        });
                    }
//                }
            };
}
