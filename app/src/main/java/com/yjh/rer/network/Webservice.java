package com.yjh.rer.network;


import android.arch.lifecycle.LiveData;

import com.yjh.rer.model.ResponseBody;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Webservice {
    String URL_RED_ENVELOPES = "envelopes/";

    @GET(URL_RED_ENVELOPES)
    LiveData<ApiResponse<ResponseBody>> getRedEnvelopes(
            @Query("token") String token, @Query("user_id") String userId);

    @FormUrlEncoded
    @POST(URL_RED_ENVELOPES)
    LiveData<ApiResponse<ResponseBody>> addRedEnvelope(
            @Field("money_from") String moneyFrom,
            @Field("money") String money,
            @Field("remark") String remark,
            @Field("token") String token);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = URL_RED_ENVELOPES + "{redEnvelopeId}", hasBody = true)
    LiveData<ApiResponse<ResponseBody>> deleteRedEnvelope(
            @Path("redEnvelopeId") int reId,
            @Field("token") String token);
}
