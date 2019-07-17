package com.data.smartvoice.tts;


import android.provider.MediaStore;
import android.support.annotation.RequiresApi;

import com.data.smartvoice.entry.DataResult;
import com.data.smartvoice.entry.TTS;

import org.json.JSONObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

interface TTsSrviceApi {

    @GET("/split")
    Call<TTS> spliteText(@Query(value = "text") String text);

    @GET("/synthesize")
    Call<ResponseBody> speechText(@Query(value = "text") String text);

}