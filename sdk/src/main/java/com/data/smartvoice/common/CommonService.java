package com.data.smartvoice.common;

import android.support.annotation.NonNull;

import com.data.smartvoice.callback.SdkCallback;
import com.data.smartvoice.entry.DataResult;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

final public class CommonService {

    private CommonServiceApi mCommonServiceApi;

    public CommonService() {
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl("http://" + Config.AppidAuthentication.HOST + ":" + Config.AppidAuthentication.PORT)
                .addConverterFactory(
                        GsonConverterFactory.create(new GsonBuilder()
                                .setDateFormat(Config.AppidAuthentication.DATA_FORMAT)
                                .create()))
                .client(new OkHttpClient.Builder().connectTimeout(Config.AppidAuthentication.TIMEOUT, TimeUnit.SECONDS).build())
                .build();
        mCommonServiceApi = mRetrofit.create(CommonServiceApi.class);
    }

    public void isAppValid(String appid, @NonNull String packageName, @NonNull String appName, String sign,
                           @NonNull final SdkCallback<DataResult<String>> callback){

        Call<DataResult<String>> resultCall = mCommonServiceApi.isAppValid(appid, packageName, appName, sign);
        resultCall.enqueue(new Callback<DataResult<String>>() {
            @Override
            public void onResponse(Call<DataResult<String>> call, Response<DataResult<String>> response) {
                if (response.isSuccessful()) {
                    DataResult<String> dataResult = response.body();
                    callback.onResponse(dataResult);
                }
            }

            @Override
            public void onFailure(Call<DataResult<String>> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }
}
