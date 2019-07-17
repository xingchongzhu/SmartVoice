package com.data.smartvoice;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.data.smartvoice.callback.SdkCallback;
import com.data.smartvoice.callback.VoiceSdkCallback;
import com.data.smartvoice.common.CommonBaseServiceInterface;
import com.data.smartvoice.common.CommonService;
import com.data.smartvoice.entry.DataResult;
import com.data.smartvoice.entry.VoiceEntry;
import com.data.smartvoice.tts.TtsServiceManager;
import com.data.smartvoice.utils.LogUtils;
import com.data.smartvoice.voicerecognition.VoiceBaseServiceManager;

class CommonServiceManager extends CommonBaseServiceInterface {

    private static CommonServiceManager mCommonServiceManager;
    private CommonService mCommonService;
    private boolean isValid;

    private CommonServiceManager() {
        mCommonService = new CommonService();
    }

    public static CommonBaseServiceInterface getInstance() {
        if (mCommonServiceManager == null) {
            mCommonServiceManager = new CommonServiceManager();
        }
        return mCommonServiceManager;
    }

    private boolean checkAppidVaile() {
        if (!isValid) {
            LogUtils.e("appid Is inValid please init SmartVoiceSdkManager");
            return false;
        }
        return true;
    }

    @Override
    public void isAppValid(String appid, @NonNull String packageName, @NonNull String appName, String sign) {
        mCommonService.isAppValid(appid, packageName, appName, sign, new SdkCallback<DataResult<String>>() {
            @Override
            public void onResponse(@NonNull DataResult<String> result) {
                if (result != null) {
                    LogUtils.i("isAppValid --- checkAppIdOnline: " + result.toString());
                    if (result.getStatus().equalsIgnoreCase("Success")) {
                        isValid = true;
                    } else {
                        LogUtils.e("checkAppIdOnline status: Fail");
                        isValid = false;
                    }
                }
            }

            @Override
            public void onFailure(@Nullable Object msg) {
                LogUtils.e("onFailure: msg = " + msg);
                isValid = false;
            }
        });
    }

    @Override
    public void resetConfig() {
        TtsServiceManager.getInstance().resetConfig();
        VoiceBaseServiceManager.getInstance().resetConfig();
    }

    @Override
    public void speechText(String text, SdkCallback sdkCallback) {
        if(checkAppidVaile()) {
            TtsServiceManager.getInstance().speechText(text, sdkCallback);
        }
    }

    @Override
    public void interruptSpeech() {
        if(checkAppidVaile()) {
            TtsServiceManager.getInstance().interruptSpeech();
        }
    }

    @Override
    public void voiceEnroll(VoiceEntry voiceEntry, VoiceSdkCallback voiceSdkCallback) {
        if(checkAppidVaile()) {
            VoiceBaseServiceManager.getInstance().voiceEnroll(voiceEntry, voiceSdkCallback);
        }
    }

    @Override
    public void voiceRecogintion(VoiceEntry voiceEntry, VoiceSdkCallback voiceSdkCallback) {
        if(checkAppidVaile()) {
            VoiceBaseServiceManager.getInstance().voiceRecogintion(voiceEntry, voiceSdkCallback);
        }
    }

    @Override
    public void getAllVoiceId(VoiceSdkCallback voiceSdkCallback) {
        if(checkAppidVaile()) {
            VoiceBaseServiceManager.getInstance().getAllVoiceId(voiceSdkCallback);
        }
    }

    @Override
    public void deleteVoice(String id, VoiceSdkCallback voiceSdkCallback) {
        if(checkAppidVaile()) {
            VoiceBaseServiceManager.getInstance().deleteVoice(id, voiceSdkCallback);
        }
    }

    @Override
    public void queryvoiceById(String id, VoiceSdkCallback voiceSdkCallback) {
        if(checkAppidVaile()) {
            VoiceBaseServiceManager.getInstance().queryvoiceById(id, voiceSdkCallback);
        }
    }

}
