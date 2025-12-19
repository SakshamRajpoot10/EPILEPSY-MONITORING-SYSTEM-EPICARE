package com.epicare.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class LocationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra("latitude") && intent.hasExtra("longitude")) {
            double lat = intent.getDoubleExtra("latitude", 0);
            double lon = intent.getDoubleExtra("longitude", 0);
            long time = intent.getLongExtra("timestamp", 0);

            String updatedLocation = "Lat: " + lat + ", Lon: " + lon + ", Time: " + time;
            Toast.makeText(context, updatedLocation, Toast.LENGTH_SHORT).show();
            Log.d("LocationReceiver", updatedLocation);
        }
    }
}

