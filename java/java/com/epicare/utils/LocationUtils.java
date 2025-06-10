package com.epicare.utils;
import static com.epicare.utils.MyConstants.PREF_NAME_GLOBAL;
import android.content.Context;
import android.content.SharedPreferences;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class LocationUtils {

    private static final String LAT_KEY = "lat";
    private static final String LON_KEY = "lon";
    private static final String TIME_KEY = "time";

    public static void saveLastKnownLocation(Context context, double lat, double lon, long time) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME_GLOBAL, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(LAT_KEY, (float) lat);
        editor.putFloat(LON_KEY, (float) lon);
        editor.putLong(TIME_KEY, time);
        editor.apply();
    }

    public static String getLastKnownLocation(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME_GLOBAL, Context.MODE_PRIVATE);
        float lat = prefs.getFloat(LAT_KEY, 0);
        float lon = prefs.getFloat(LON_KEY, 0);
        long time = prefs.getLong(TIME_KEY, 0);
        String formattedTime = formatTimestamp(time);

        if (lat == 0.0 && lon == 0.0) {
            return "No location available";
        }
        return "Lat: " + lat + ", Lon: " + lon + "\nTime: " + formattedTime;
    }
    private static String formatTimestamp(long timestamp) {
        if (timestamp == 0) return "Unknown Time"; // Handle case where time is not set
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}

