package com.epicare.utils;


public class MyConstants {


    public static final String REG_EXP_EMAIL = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public static final String REG_EXP_MOBILE = "^[7-9][0-9]{9}$";
    public static final String PREF_NAME_GLOBAL = "EpicCare";
    public static final String base_api_url ="";
    public static final String real_url ="https://inspectease-9e35b-default-rtdb.asia-southeast1.firebasedatabase.app";




//--------------------------------------------------------------------------------------------

    public static final String FROM_ACTIVITY = "FromActivity";
    public static final String KEY_SERVICE = "Service";
    public static final String KEY_MAP_LOCATION = "MapLocation";
    public static final String KEY_VOLUNTEER = "Volunteer";
    public static final String KEY_LATITUDE = "Latitude";
    public static final String KEY_LONGITUDE= "Longitude";
    public static final String KEY_USER_TYPE = "userType";

    public static final String PREF_KEY_NAME = "User Name";
    public static final String PREF_KEY_EMAIL = "email";
    public static final String PREF_KEY_PASSWORD = "email";
    public static final String PREF_KEY_MOBILE = "User Mobile";
    public static final String PREF_KEY_ID = "User Id";
    public static final String KEY_IS_LOGGED_IN = "isLoggedIn";


    public static final float calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return (float) (earthRadius * c) / 1000;//convert into km
    }


}
