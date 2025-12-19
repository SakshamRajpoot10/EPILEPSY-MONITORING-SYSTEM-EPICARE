package com.epicare.utils;
import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.Set;

public class BluetoothUtils {
    private static final String TAG = "BluetoothUtils";

    private static final String ESP32_DEVICE_NAME = "ESP32"; // Change this if needed


    // **Get paired ESP32 device**
    @SuppressLint("MissingPermission")
    public static BluetoothDevice getPairedESP32Device(Context context) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Log.e(TAG, "Bluetooth is not enabled.");
            return null;
        }

        if (!hasBluetoothPermissions(context)) {
            Log.e(TAG, "Missing BLUETOOTH_CONNECT permission!");
            return null;
        }

        @SuppressLint("MissingPermission") Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices != null) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName() != null && device.getName().contains(ESP32_DEVICE_NAME)) {
                    Log.d(TAG, "Found ESP32 device: " + device.getName() + " (" + device.getAddress() + ")");
                    return device;
                }
            }
        }

        Log.e(TAG, "No paired ESP32 device found.");
        return null;
    }
    // Check if Bluetooth is supported
    public static boolean isBluetoothSupported(Context context) {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        return bluetoothManager != null && bluetoothManager.getAdapter() != null;
    }

    // Check if Bluetooth is enabled
    public static boolean isBluetoothEnabled() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    // Check if Bluetooth permissions are granted
    private static boolean hasBluetoothPermissions(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;

    }

    // Get a list of paired devices (requires BLUETOOTH_CONNECT permission)
    @SuppressLint("MissingPermission")
    public static Set<BluetoothDevice> getPairedDevices(Context context) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            if (!hasBluetoothPermissions(context)) {
                Log.e(TAG, "Missing BLUETOOTH_CONNECT permission!");
                return null;
            }
            return bluetoothAdapter.getBondedDevices();
        }
        return null;
    }

    // Find a paired device by its name
    @SuppressLint("MissingPermission")
    public static BluetoothDevice getPairedDeviceByName(Context context, String deviceName) {
        Set<BluetoothDevice> pairedDevices = getPairedDevices(context);
        if (pairedDevices != null) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals(deviceName)) {
                    return device;
                }
            }
        }
        return null;
    }

    // Find a paired device by its MAC address
    public static BluetoothDevice getPairedDeviceByMac(Context context, String macAddress) {
        Set<BluetoothDevice> pairedDevices = getPairedDevices(context);
        if (pairedDevices != null) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getAddress().equals(macAddress)) {
                    return device;
                }
            }
        }
        return null;
    }

    // Print all paired Bluetooth devices
    @SuppressLint("MissingPermission")
    public static void logPairedDevices(Context context) {
        Set<BluetoothDevice> pairedDevices = getPairedDevices(context);
        if (pairedDevices != null && !pairedDevices.isEmpty()) {
            for (BluetoothDevice device : pairedDevices) {
                Log.d(TAG, "Paired Device: " + device.getName() + " (" + device.getAddress() + ")");
            }
        } else {
            Log.d(TAG, "No paired Bluetooth devices found.");
        }
    }
}
