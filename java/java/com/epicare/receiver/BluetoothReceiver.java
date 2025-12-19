package com.epicare.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.epicare.ConnectionSettingsActivity;

public class BluetoothReceiver extends BroadcastReceiver {
    private static final String TAG = "BluetoothReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action == null) return;

        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    Log.e(TAG, "Bluetooth is OFF");
                    Toast.makeText(context, "Bluetooth is OFF. Please enable it.", Toast.LENGTH_LONG).show();
                    promptUserToEnableBluetooth(context);
                    break;

                case BluetoothAdapter.STATE_TURNING_OFF:
                    Log.w(TAG, "Bluetooth is turning OFF");
                    break;

                case BluetoothAdapter.STATE_ON:
                    Log.d(TAG, "Bluetooth is ON");
                    break;

                case BluetoothAdapter.STATE_TURNING_ON:
                    Log.i(TAG, "Bluetooth is turning ON");
                    break;
            }
        }
    }

    private void promptUserToEnableBluetooth(Context context) {
        Intent settingsIntent = new Intent(context, ConnectionSettingsActivity.class);
        settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(settingsIntent);
    }
}

