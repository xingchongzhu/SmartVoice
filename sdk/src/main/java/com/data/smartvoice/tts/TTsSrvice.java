package com.data.smartvoice.tts;


import android.support.annotation.NonNull;

import com.data.smartvoice.callback.SdkCallback;
import com.data.smartvoice.common.Config;
import com.data.smartvoice.callback.VoiceSdkCallback;
import com.data.smartvoice.entry.TTS;
import com.data.smartvoice.utils.FileUtil;
import com.data.smartvoice.utils.LogUtils;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


final class TTsSrvice {

    private TTsSrviceApi mTTsSrviceApi;

    public TTsSrvice() {
        resetConfig();
    }

    public void resetConfig(){
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl("http://" + Config.TTsConfig.HOST + ":" + Config.TTsConfig.PORT)
                .addConverterFactory(
                        GsonConverterFactory.create(new GsonBuilder()
                                .setDateFormat(Config.TTsConfig.DATA_FORMAT)
                                .create()))
                .client(new OkHttpClient.Builder().connectTimeout(Config.TTsConfig.TIMEOUT, TimeUnit.SECONDS).build())
                .build();
        mTTsSrviceApi = mRetrofit.create(TTsSrviceApi.class);
    }

    public synchronized void speechText(final String text, @NonNull final VoiceSdkCallback callback, final List<String> speechFileQueue) {
        Call<ResponseBody> call = mTTsSrviceApi.speechText(text);
        LogUtils.d("speechText  text = " + text);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    ResponseBody result = response.body();
                    InputStream inputStream = result.byteStream();
                    File file = FileUtil.convertStreamToFile(inputStream);
                    if (file != null) {
                        speechFileQueue.add(file.getAbsolutePath());
                        //MediaPlayerHelper.getInstance().play(file.getAbsolutePath());
                    }
                    LogUtils.d("speechText file sucess ");

                    if (result != null) {
                        callback.onResponse(null);
                    } else {
                        callback.onFailure(response.message());
                    }
                } else {
                    if (response.errorBody() != null) {
                        try {
                            callback.onFailure(response.errorBody().string());
                            LogUtils.w("speechText file fail " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        callback.onFailure("Http request Fail.");
                        LogUtils.w("Http request Fail.");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                LogUtils.w("Http request Fail " + t.getMessage());
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void spliteMuliteText(final String text, @NonNull final SdkCallback<TTS> callback, final List<String> speechQueue) {
        Call<TTS> call = mTTsSrviceApi.spliteText(text);
        call.enqueue(new Callback<TTS>() {
            @Override
            public void onResponse(Call<TTS> call, Response<TTS> response) {
                if (response.isSuccessful()) {
                    TTS result = response.body();

                    LogUtils.d("spliteMuliteText file sucess " + text + " result = " + result.toString());
                    if (result != null) {
                        speechQueue.addAll(result.getList_of_slpit_text());
                        callback.onResponse(result);
                    } else {
                        callback.onFailure(response.message());
                    }
                } else {
                    if (response.errorBody() != null) {
                        try {
                            callback.onFailure(response.errorBody().string());
                            LogUtils.w("spliteMuliteText file fail " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        callback.onFailure("Http request Fail.");
                        LogUtils.w("Http request Fail.");
                    }
                }
            }

            @Override
            public void onFailure(Call<TTS> call, Throwable t) {
                t.printStackTrace();
                LogUtils.w("Http request Fail " + t.getMessage());
                callback.onFailure(t.getMessage());
            }
        });
    }
}