package com.yjh.rer.network;


import android.arch.lifecycle.LiveData;

import com.yjh.rer.model.ResponseBody;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Webservice {
    @GET("envelopes/")
    LiveData<ApiResponse<ResponseBody>> getRedEnvelopes(
            @Query("token") String token, @Query("user_id") String userId);
}
