package com.epicare;

import static com.epicare.utils.MyConstants.PREF_NAME_GLOBAL;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.epicare.service.BluetoothForegroundService;
import com.epicare.service.LocationForegroundService;

public class frgSettings extends Fragment {

    private static final String LOCATION_SERVICE_ENABLED = "location_service_enabled";
    private static final String BLUETOOTH_SERVICE_ENABLED = "bluetooth_service_enabled";

    private Switch locationSwitch;
    private Switch bluetoothSwitch;
    private BluetoothAdapter bluetoothAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // Initialize Bluetooth adapter
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for the fragment
        View view = inflater.inflate(R.layout.frg_settings, container, false);

        locationSwitch = view.findViewById(R.id.switch_location_service);
        bluetoothSwitch = view.findViewById(R.id.switch_bluetooth_service);

        // ✅ Load saved preferences
        SharedPreferences prefs = getActivity().getSharedPreferences(PREF_NAME_GLOBAL, Context.MODE_PRIVATE);
        boolean isLocationServiceEnabled = prefs.getBoolean(LOCATION_SERVICE_ENABLED, false);
        boolean isBluetoothServiceEnabled = prefs.getBoolean(BLUETOOTH_SERVICE_ENABLED, false);

        locationSwitch.setChecked(isLocationServiceEnabled);
        bluetoothSwitch.setChecked(isBluetoothServiceEnabled);

        // ✅ Location Switch Listener
        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleLocationService(isChecked);
            }
        });

        // ✅ Bluetooth Switch Listener
        bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleBluetoothService(isChecked);
            }
        });

        return view;
    }

    private void toggleLocationService(boolean enable) {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences(PREF_NAME_GLOBAL, Context.MODE_PRIVATE).edit();
        editor.putBoolean(LOCATION_SERVICE_ENABLED, enable);
        editor.apply();

        if (enable) {
            getActivity().startService(new Intent(getActivity(), LocationForegroundService.class));
            Toast.makeText(getActivity(), "Location tracking enabled", Toast.LENGTH_SHORT).show();
        } else {
            getActivity().stopService(new Intent(getActivity(), LocationForegroundService.class));
            Toast.makeText(getActivity(), "Location tracking disabled", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void toggleBluetoothService(boolean enable) {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences(PREF_NAME_GLOBAL, Context.MODE_PRIVATE).edit();
        editor.putBoolean(BLUETOOTH_SERVICE_ENABLED, enable);
        editor.apply();

        if (enable) {
            if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable(); // Turn on Bluetooth
            }
            getActivity().startService(new Intent(getActivity(), BluetoothForegroundService.class));
            Toast.makeText(getActivity(), "Bluetooth service enabled", Toast.LENGTH_SHORT).show();
        } else {
            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.disable(); // Turn off Bluetooth
            }
            getActivity().stopService(new Intent(getActivity(), BluetoothForegroundService.class));
            Toast.makeText(getActivity(), "Bluetooth service disabled", Toast.LENGTH_SHORT).show();
        }
    }
}
