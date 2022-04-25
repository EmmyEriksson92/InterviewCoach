package com.example.interviewcoach;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;
/**
 * Interface for chatBot RetroFit.
 * @author Emmy
 */
public interface RetroFitApi {
    @GET
    Call<MsgModel> getMessage(@Url String url);


}
