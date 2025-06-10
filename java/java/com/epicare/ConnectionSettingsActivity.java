package com.epicare;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.epicare.R;

public class ConnectionSettingsActivity extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;
    private TextView statusTextView;
    private Button enableBluetoothButton, openSettingsButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_settings);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        statusTextView = findViewById(R.id.bluetooth_status);
        enableBluetoothButton = findViewById(R.id.btn_enable_bluetooth);
        openSettingsButton = findViewById(R.id.btn_open_settings);

        updateBluetoothStatus();

        enableBluetoothButton.setOnClickListener(v -> enableBluetooth());
        openSettingsButton.setOnClickListener(v -> openBluetoothSettings());
    }

    private void updateBluetoothStatus() {
        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.isEnabled()) {
                statusTextView.setText("Bluetooth is ON");
                enableBluetoothButton.setVisibility(View.GONE);
            } else {
                statusTextView.setText("Bluetooth is OFF");
                enableBluetoothButton.setVisibility(View.VISIBLE);
            }
        } else {
            statusTextView.setText("Bluetooth not supported on this device");
            enableBluetoothButton.setVisibility(View.GONE);
        }
    }

    private void enableBluetooth() {
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
        }
    }

    private void openBluetoothSettings() {
        Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBluetoothStatus();
    }
}

