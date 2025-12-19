package com.epicare.api;

import com.epicare.model.DemoData;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("posts") // Adjust this endpoint based on your actual API
    Call<ApiResponse> sendDemoData(@Body DemoData data);
}
