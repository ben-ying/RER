package com.yjh.rer.network;


import android.arch.lifecycle.LiveData;

import com.google.gson.JsonObject;
import com.yjh.rer.BaseResponse;
import com.yjh.rer.room.entity.RedEnvelope;

import org.json.JSONObject;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Webservice {
    @GET("envelopes/")
    LiveData<ApiResponse<BaseResponse>> getRedEnvelopes(
            @Query("token") String token, @Query("user_id") String userId);
}
