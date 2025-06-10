package com.epicare.service;

import static com.epicare.utils.LocationUtils.saveLastKnownLocation;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.epicare.api.ApiHelper;

public class LocationForegroundService extends Service {
    private static final String TAG = "LocationService";
    private static final String CHANNEL_ID = "LocationServiceChannel";
    private LocationManager locationManager;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
        startForeground(2, getServiceNotification());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "Missing FOREGROUND_SERVICE_LOCATION permission!");
                stopSelf(); // Stop service to prevent crash
                return;
            }
        }
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        requestLocationUpdates();
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Location permission not granted!");
            return;
        }

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000, // Every 5 seconds
                5,    // 5 meters change
                locationListener
        );
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            long timestamp = System.currentTimeMillis();

            Toast.makeText(LocationForegroundService.this, "lat:"+latitude +"Long:"+longitude+"time:"+timestamp, Toast.LENGTH_SHORT).show();

            // ✅ Send to API
            ApiHelper.sendLocationToServer(latitude, longitude, timestamp);
            saveLastKnownLocation(getApplicationContext() ,latitude, longitude, timestamp);
            // ✅ Broadcast location for UI update
            sendLocationUpdate(latitude, longitude, timestamp);
        }

        @Override
        public void onStatusChanged(String provider, int status, android.os.Bundle extras) {}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onProviderDisabled(String provider) {}

    };
    private void sendLocationUpdate(double latitude, double longitude, long timestamp) {
        Intent intent = new Intent("com.epicare.LOCATION_UPDATE");
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        intent.putExtra("timestamp", timestamp);
        sendBroadcast(intent); // ✅ Broadcast location update
    }
    private Notification getServiceNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Location Tracking")
                .setContentText("Tracking live location...")
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Location Service Channel", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

