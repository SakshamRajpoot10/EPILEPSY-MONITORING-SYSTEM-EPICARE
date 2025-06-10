package com.epicare.api;

import android.util.Log;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiHelper {

    private static final String SERVER_URL = "https://yourapi.com/bluetooth/data";
    private static final String SERVER_URL2 = "https://yourapi.com/location/update";
    public static void sendDataToServer(String data) {
        new Thread(() -> {
            try {
                URL url = new URL(SERVER_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                String jsonPayload = "{\"bluetooth_data\":\"" + data + "\"}";
                OutputStream os = conn.getOutputStream();
                os.write(jsonPayload.getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                Log.d("ApiHelper", "Response Code: " + responseCode);
            } catch (Exception e) {
                Log.e("ApiHelper", "Error sending data: " + e.getMessage());
            }
        }).start();
    }

    public static void sendLocationToServer(double latitude, double longitude, long timestamp) {
        new Thread(() -> {
            try {
                URL url = new URL(SERVER_URL2);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                String jsonPayload = "{\"latitude\":" + latitude + ", \"longitude\":" + longitude + ", \"timestamp\":" + timestamp + "}";
                OutputStream os = conn.getOutputStream();
                os.write(jsonPayload.getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                Log.d("ApiHelper", "Location API Response: " + responseCode);
            } catch (Exception e) {
                Log.e("ApiHelper", "Error sending location: " + e.getMessage());
            }
        }).start();
    }
}

