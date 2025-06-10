package com.epicare.utils;

import android.widget.EditText;
import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.firebase.Timestamp;

public class myDate {

    public static String timestampTodate(Timestamp timestamp) {

        if (timestamp != null) {
            // Create a SimpleDateFormat object with the desired date format
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMMM");

            // Convert the timestamp to a Date object and format it as a string
            return formatter.format(timestamp.toDate());


        } else {
            // Handle the case where the timestamp is null
            return "N/A";
        }
    }

}
