package com.data.smartvoice.common;

import android.support.annotation.NonNull;

import com.data.smartvoice.tts.TtsBaseServiceInterface;
import com.data.smartvoice.voicerecognition.VoiceBaseServiceInterface;

public abstract class CommonBaseServiceInterface implements TtsBaseServiceInterface, VoiceBaseServiceInterface {

    public abstract void isAppValid(String appid, @NonNull String packageName, @NonNull String appName, String sign);
}
