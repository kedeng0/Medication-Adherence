package com.example.ben.test_version_2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;
import java.util.UUID;

/**
 * Created by admin on 23/3/2018.
 */

public class BleManager  {
    // Log
    private final static String TAG = BleManager.class.getSimpleName();
    Context activityContext;

    // Enumerations
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.ben.smap.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.ben.smap.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.ben.smap.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.ben.smap.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.ben.smap.EXTRA_DATA";

    public final static UUID MY_SERVICE_UUID =UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    public final static UUID MY_CHARACTERISTIC_UUID =UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
    // Singleton
    private static BleManager mInstance = null;


    // Data
    private BluetoothAdapter mAdapter;
    private BluetoothGatt mGatt;
    List<BluetoothGattService> serviceList;
    BluetoothGattCharacteristic rx;


    private BluetoothDevice mDevice;
    private String mDeviceAddress;
    private int mConnectionState = STATE_DISCONNECTED;


    public static BleManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new BleManager(context);
        }
        return mInstance;
    }

    public BleManager(Context context) {
        // Init Adapter
        activityContext = context;
        if (mAdapter == null) {
            mAdapter = Utils.getBluetoothAdapter(context);
        }

        if (mAdapter == null || !mAdapter.isEnabled()) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
        }
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result is reported asynchronously through the {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)} callback.
     */
    public boolean connect(Context context, final String address) {
        if (mAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mDeviceAddress != null && address.equals(mDeviceAddress)
                && mGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mGatt.connect()) {
                mConnectionState = STATE_CONNECTING;

                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mGatt = device.connectGatt(context, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mDeviceAddress = address;
        mDevice = device;
        mConnectionState = STATE_CONNECTING;
        return true;
    }


    public boolean disconnect() {
        if (mAdapter == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return false;
        }
        if (mConnectionState != STATE_CONNECTED) {
            Log.w(TAG, "Not connected to any device");
            Utils.toast(activityContext,"Not connected to any device");
            return false;
        } else {
            mGatt.disconnect();
            return true;
        }


    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        mGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                mDeviceAddress = null;
                mDevice = null;
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d(TAG, "Service Discovered");
            serviceList = gatt.getServices();
            DisplayService(serviceList);

            if (status == BluetoothGatt.GATT_SUCCESS) {
//                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
//                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
//            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        activityContext.sendBroadcast(intent);
    }

        public void DisplayService(List<BluetoothGattService> list) {
            for (BluetoothGattService service : list) {
                Log.d(TAG, "Service Found: " + service.getUuid());
            }
        }

        public boolean dispense() {
            if (mAdapter == null || mGatt == null || serviceList == null) {
                Log.d(TAG, "Unable to Dispense");
                Utils.toast(activityContext, "Unable to Dispense");
                return false;
            } else {
                boolean hasService = false;
                for (BluetoothGattService service : serviceList) {
                    if (service.getUuid().equals(MY_SERVICE_UUID)) {
                        rx =  service.getCharacteristic(MY_CHARACTERISTIC_UUID);
                        hasService = true;
                    }
                }
                if (!hasService || rx == null) {
                    return false;
                }
                //write 'rua' to characteristic
                String str = "g";
                final byte[] value = str.getBytes();
                rx.setValue(value);
                mGatt.writeCharacteristic(rx);
                return true;
            }
        }


        public BluetoothDevice getDevice() {
            return mDevice;
        }
        public boolean getConnectionStatus() {return mConnectionState == STATE_CONNECTED;}




}
