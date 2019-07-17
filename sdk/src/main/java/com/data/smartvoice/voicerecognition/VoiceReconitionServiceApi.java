package com.data.smartvoice.voicerecognition;

import com.data.smartvoice.entry.BaseEntry;
import com.data.smartvoice.entry.BaseResult;
import com.data.smartvoice.entry.DataResult;
import com.data.smartvoice.entry.SpeakerEntry;
import com.data.smartvoice.entry.TTS;
import com.data.smartvoice.entry.VoiceEntry;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

interface VoiceReconitionServiceApi {

    @POST("/v1/speaker/enroll")
    Call<BaseResult> voiceEnroll(@Body VoiceEntry voiceEntry);

    @POST("/v1/speaker/identify")
    Call<DataResult> voiceRecogintion(@Body VoiceEntry voiceEntry);

    @DELETE("/v1/speaker/{id}")
    Call<BaseResult> deleteVoice(@Path(value = "id")  String id);

    @GET("/v1/speaker")
    Call<SpeakerEntry> getAllVoiceId();

    @GET("/v1/speaker/{id}")
    Call<SpeakerEntry> queryId(@Path(value = "id")  String id);

}