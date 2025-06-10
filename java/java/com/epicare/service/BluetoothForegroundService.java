package com.epicare.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import android.Manifest; // ✅ ADD THIS LINE

import com.epicare.api.ApiHelper;
import com.epicare.utils.BluetoothUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class BluetoothForegroundService extends Service {

    private static final String TAG = "BTForegroundService";
    private static final String CHANNEL_ID = "BluetoothServiceChannel";
    private static final UUID ESP32_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private InputStream inputStream;
    private boolean isReceiving = false;
    private Handler reconnectHandler = new Handler();
    private static final int RECONNECT_DELAY_MS = 5000; // 5 seconds

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(1, getServiceNotification());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "Missing BLUETOOTH_CONNECT permission!");
                stopSelf(); // ✅ Stop service to prevent crashes
                return;
            }
        }
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        connectToESP32();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        connectToESP32();
        return START_STICKY;
    }

    @SuppressLint("MissingPermission")
    private void connectToESP32() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Log.e(TAG, "Bluetooth is not enabled.");
            return;
        }

        // ✅ FIX: Call BluetoothUtils correctly
        BluetoothDevice espDevice = BluetoothUtils.getPairedESP32Device(this);
        if (espDevice == null) {
            Log.e(TAG, "ESP32 device not found.");
            scheduleReconnect();
            return;
        }

        try {
            bluetoothSocket = espDevice.createRfcommSocketToServiceRecord(ESP32_UUID);
            bluetoothSocket.connect();
            inputStream = bluetoothSocket.getInputStream();
            isReceiving = true;
            startReadingData();
            Log.d(TAG, "Connected to ESP32 successfully.");
        } catch (IOException e) {
            Log.e(TAG, "Failed to connect to ESP32: " + e.getMessage());
            scheduleReconnect();
        }
    }

    private void startReadingData() {
        new Thread(() -> {
            byte[] buffer = new byte[1024];
            int bytes;

            while (isReceiving) {
                try {
                    bytes = inputStream.read(buffer);
                    if (bytes > 0) {
                        String receivedData = new String(buffer, 0, bytes);
                        Log.d(TAG, "Received: " + receivedData);
                        ApiHelper.sendDataToServer(receivedData);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Connection lost, attempting to reconnect.");
                    isReceiving = false;
                    closeConnection();
                    scheduleReconnect();
                }
            }
        }).start();
    }

    private void scheduleReconnect() {
        Log.d(TAG, "Reconnecting in " + (RECONNECT_DELAY_MS / 1000) + " seconds...");
        reconnectHandler.postDelayed(this::connectToESP32, RECONNECT_DELAY_MS);
    }

    private Notification getServiceNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Bluetooth Service")
                .setContentText("Receiving data from ESP32")
                .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Bluetooth Service Channel", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isReceiving = false;
        reconnectHandler.removeCallbacksAndMessages(null);
        closeConnection();
    }

    private void closeConnection() {
        try {
            if (inputStream != null) inputStream.close();
            if (bluetoothSocket != null) bluetoothSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Error closing Bluetooth connection: " + e.getMessage());
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
