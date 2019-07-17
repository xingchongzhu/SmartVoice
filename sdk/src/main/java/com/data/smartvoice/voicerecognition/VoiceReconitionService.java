package com.data.smartvoice.voicerecognition;

import android.support.annotation.NonNull;

import com.data.smartvoice.common.Config;
import com.data.smartvoice.callback.VoiceSdkCallback;
import com.data.smartvoice.entry.BaseResult;
import com.data.smartvoice.entry.DataResult;
import com.data.smartvoice.entry.SpeakerEntry;
import com.data.smartvoice.entry.VoiceEntry;
import com.data.smartvoice.utils.LogUtils;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class VoiceReconitionService {

    private VoiceReconitionServiceApi mVoiceReconitionServiceApi;

    public VoiceReconitionService() {
        resetConfig();
    }

    public void resetConfig(){
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl("http://" + Config.VoiceRecognitionConfig.HOST + ":" + Config.VoiceRecognitionConfig.PORT)
                .addConverterFactory(
                        GsonConverterFactory.create(new GsonBuilder()
                                .setDateFormat(Config.TTsConfig.DATA_FORMAT)
                                .create()))
                .client(new OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS).build())
                .build();
        mVoiceReconitionServiceApi = mRetrofit.create(VoiceReconitionServiceApi.class);
    }

    public synchronized void voiceEnroll(final VoiceEntry voiceEntry, @NonNull final VoiceSdkCallback callback) {
        Call<BaseResult> call = mVoiceReconitionServiceApi.voiceEnroll(voiceEntry);
        call.enqueue(new Callback<BaseResult>() {
            @Override
            public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {
                if (response.isSuccessful()) {
                    BaseResult result = response.body();
                    if(result == null){
                        LogUtils.d("voiceEnroll result null ");
                        return;
                    }
                    LogUtils.d("voiceEnroll file result = "+result);
                    if(callback != null) {
                        if (result.getCode() == 200) {
                            callback.onResponse(result);
                        } else {
                            callback.onFailure(result.getMsg());
                        }
                    }
                } else {
                    if(callback != null) {
                        if (response.errorBody() != null) {
                            try {
                                callback.onFailure(response.errorBody().string());
                                LogUtils.w("voiceEnroll file fail " + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            callback.onFailure("Http request Fail.");
                            LogUtils.w("Http request Fail.");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResult> call, Throwable t) {
                t.printStackTrace();
                LogUtils.w("Http request Fail " + t.getMessage());
                if(callback != null) {
                    callback.onFailure(t.getMessage());
                }
            }
        });
    }

    public synchronized void voiceRecogintion(final VoiceEntry voiceEntry, @NonNull final VoiceSdkCallback callback) {
        Call<DataResult> call = mVoiceReconitionServiceApi.voiceRecogintion(voiceEntry);
        call.enqueue(new Callback<DataResult>() {
            @Override
            public void onResponse(Call<DataResult> call, Response<DataResult> response) {
                if (response.isSuccessful()) {
                    DataResult result = response.body();
                    if(result == null){
                        LogUtils.d("voiceRecogintion result null ");
                        return;
                    }
                    LogUtils.d("voiceRecogintion file sucess result = "+result.getData());
                    if(callback != null) {
                        if (result.getCode() == 200) {
                            callback.onResponse(result);
                        } else {
                            callback.onFailure(result.getMsg());
                        }
                    }
                } else {
                    if(callback != null) {
                        if (response.errorBody() != null) {
                            try {
                                callback.onFailure(response.errorBody().string());
                                LogUtils.w("voiceEnroll file fail " + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            callback.onFailure("Http request Fail.");
                            LogUtils.w("Http request Fail.");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<DataResult> call, Throwable t) {
                t.printStackTrace();
                LogUtils.w("Http request Fail " + t.getMessage());
                if(callback != null) {
                    callback.onFailure(t.getMessage());
                }
            }
        });
    }

    public synchronized void deleteVoice(final String id, @NonNull final VoiceSdkCallback callback) {
        Call<BaseResult> call = mVoiceReconitionServiceApi.deleteVoice(id);
        call.enqueue(new Callback<BaseResult>() {
            @Override
            public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {
                if (response.isSuccessful()) {
                    BaseResult result = response.body();
                    if(result == null){
                        LogUtils.d("deleteVoice result null ");
                        return;
                    }
                    LogUtils.d("deleteVoice file sucess "+id);
                    if(callback != null) {
                        if (result.getCode() == 200) {
                            callback.onResponse(result);
                        }else if (result.getCode() == 404){
                            callback.onFailure(id+" user not exist");
                        } else {
                            callback.onFailure(response.message());
                        }
                    }
                } else {
                    if(callback != null) {
                        if (response.errorBody() != null) {
                            try {
                                callback.onFailure(response.errorBody().string());
                                LogUtils.w("deleteVoice file fail " + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            callback.onFailure("Http request Fail.");
                            LogUtils.w("Http request Fail.");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResult> call, Throwable t) {
                t.printStackTrace();
                LogUtils.w("Http request Fail " + t.getMessage());
                if(callback != null) {
                    callback.onFailure(t.getMessage());
                }
            }
        });
    }

    public synchronized void getAllVoice( @NonNull final VoiceSdkCallback callback) {
        Call<SpeakerEntry> call = mVoiceReconitionServiceApi.getAllVoiceId();
        call.enqueue(new Callback<SpeakerEntry>() {
            @Override
            public void onResponse(Call<SpeakerEntry> call, Response<SpeakerEntry> response) {
                if (response.isSuccessful()) {
                    SpeakerEntry result = response.body();
                    if(result == null){
                        LogUtils.d("getAllVoice result null ");
                        return;
                    }
                    LogUtils.d("getAllVoice file sucess ");
                    if(callback != null) {
                        if (result.getCode() == 200) {
                            callback.onResponse(result);
                        }else if(result.getCode() == 404){
                            callback.onFailure(result.getMsg());
                        }
                    }
                } else {
                    if(callback != null) {
                        if (response.errorBody() != null) {
                            try {
                                callback.onFailure(response.errorBody().string());
                                LogUtils.w("getAllVoice file fail " + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            callback.onFailure("Http request Fail.");
                            LogUtils.w("Http request Fail.");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<SpeakerEntry> call, Throwable t) {
                t.printStackTrace();
                LogUtils.w("Http request Fail " + t.getMessage());
                if(callback != null) {
                    callback.onFailure(t.getMessage());
                }
            }
        });
    }

    public synchronized void queryVoiceById(final String id, @NonNull final VoiceSdkCallback callback) {
        Call<SpeakerEntry> call = mVoiceReconitionServiceApi.queryId(id);
        call.enqueue(new Callback<SpeakerEntry>() {
            @Override
            public void onResponse(Call<SpeakerEntry> call, Response<SpeakerEntry> response) {
                if (response.isSuccessful()) {
                    SpeakerEntry result = response.body();
                    LogUtils.d("queryVoiceById file sucess "+id);
                    if(callback != null) {
                        if (result.getCode() == 200) {
                            callback.onResponse(result);
                        }else {
                            callback.onFailure(result.getMsg());
                        }
                    }
                } else {
                    if(callback != null) {
                        if (response.errorBody() != null) {
                            try {
                                callback.onFailure(response.errorBody().string());
                                LogUtils.w("queryVoiceById file fail " + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            callback.onFailure("Http request Fail.");
                            LogUtils.w("Http request Fail.");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<SpeakerEntry> call, Throwable t) {
                t.printStackTrace();
                LogUtils.w("Http request Fail " + t.getMessage());
                if(callback != null) {
                    callback.onFailure(t.getMessage());
                }
            }
        });
    }

}
