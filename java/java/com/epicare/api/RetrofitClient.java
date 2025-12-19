package com.epicare.api;


import static com.epicare.utils.MyConstants.PREF_NAME_GLOBAL;

import android.content.Context;
import android.content.SharedPreferences;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;

    // Method to get Base URL from SharedPreferences
    private static String getBaseUrl(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME_GLOBAL, Context.MODE_PRIVATE);
        String url = sharedPreferences.getString("urls", "").trim();

        // Default URL if not found
        return url.isEmpty() ? "http://192.168.1.5:8000/" : url;
    }

    // Initialize Retrofit with dynamic Base URL
    public static ApiService getApiService(Context context) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(getBaseUrl(context)) // Get URL from SharedPreferences
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
