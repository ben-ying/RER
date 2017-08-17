package com.yjh.rer.webservice;


import com.yjh.rer.entity.RedEnvelope;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Webservice {
    @GET("envelopes/")
    Call<List<RedEnvelope>> getRedEnvelopes(
            @Query("token") String token, @Query("user_id") String userId);
}
