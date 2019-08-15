package com.yjh.rer.network;

import androidx.lifecycle.LiveData;

import com.yjh.rer.model.CustomResponse;
import com.yjh.rer.model.ListResponseResult;
import com.yjh.rer.room.entity.RedEnvelope;

import java.util.List;

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
    LiveData<ApiResponse<CustomResponse<ListResponseResult<List<RedEnvelope>>>>> getRedEnvelopes(
            @Query("token") String token, @Query("user_id") String userId);

    @FormUrlEncoded
    @POST(URL_RED_ENVELOPES)
    LiveData<ApiResponse<CustomResponse<RedEnvelope>>> addRedEnvelope(
            @Field("money_from") String moneyFrom,
            @Field("money") String money,
            @Field("remark") String remark,
            @Field("token") String token);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = URL_RED_ENVELOPES + "{redEnvelopeId}/", hasBody = true)
    LiveData<ApiResponse<CustomResponse<RedEnvelope>>> deleteRedEnvelope(
            @Path("redEnvelopeId") int reId,
            @Field("token") String token);
}
