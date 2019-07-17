package com.data.smartvoice;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.data.smartvoice.callback.SdkCallback;
import com.data.smartvoice.callback.VoiceSdkCallback;
import com.data.smartvoice.common.CommonBaseServiceInterface;
import com.data.smartvoice.common.Config;
import com.data.smartvoice.entry.VoiceEntry;
import com.data.smartvoice.utils.ApkUtil;
import com.data.smartvoice.utils.LogUtils;

public class SmartVoiceSdkManager extends CommonBaseServiceInterface {
    private String localAppId;
    private String localPackageName;
    private String localAppName;
    private String localSign;

    private static SmartVoiceSdkManager mSmartVoiceSdkManager;

    private SmartVoiceSdkManager() {
    }

    public static SmartVoiceSdkManager getSmartVoiceSdkManager() {
        if (mSmartVoiceSdkManager == null) {
            mSmartVoiceSdkManager = new SmartVoiceSdkManager();
        }
        return mSmartVoiceSdkManager;
    }

    /**
     * 只需要调用一次, 也可以放在Application onCreate 方法中
     *
     * @param context
     * @param appId
     */
    public synchronized void init(@NonNull final Context context, final String appId) {
        if (TextUtils.isEmpty(appId)) {
            localAppId = "xxx";
        } else {
            localAppId = appId;
        }
        localSign = ApkUtil.getSignature(context);
        localPackageName = context.getPackageName();
        try {
            localAppName = context.getString(context.getPackageManager().getPackageInfo(localPackageName, 0)
                    .applicationInfo.labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        LogUtils.d("init appId: " + appId);
        LogUtils.d("init: " + localAppId + "/" + localPackageName + "/" + localAppName + "/" + localSign);
        checkAppIdOnline();
    }

    public void setTtsConfig(String host,int port){
        Config.TTsConfig.HOST = host;
        Config.TTsConfig.PORT = port;
        CommonServiceManager.getInstance().resetConfig();
    }

    public void setVoiceRecognitionConfig(String host,int port){
        Config.VoiceRecognitionConfig.HOST = host;
        Config.VoiceRecognitionConfig.PORT = port;
        CommonServiceManager.getInstance().resetConfig();
    }

    public void dumpConfig(){
        LogUtils.d("tts host = "+Config.TTsConfig.HOST+" port = "+Config.TTsConfig.PORT);
        //"\n"+
        //                " voiceRecognition host = "+Config.VoiceRecognitionConfig.HOST+" port = "+Config.VoiceRecognitionConfig.PORT
    }

    private void checkAppIdOnline() {
        CommonServiceManager.getInstance().isAppValid(localAppId, localPackageName, localAppName, localSign);
    }

    @Override
    public void isAppValid(String appid, @NonNull String packageName, @NonNull String appName, String sign) {
    }

    @Override
    public void resetConfig() {
        CommonServiceManager.getInstance().resetConfig();
    }

    @Override
    public void speechText(String text, SdkCallback sdkCallback) {
        CommonServiceManager.getInstance().speechText(text,sdkCallback);
    }

    @Override
    public void interruptSpeech() {
        CommonServiceManager.getInstance().interruptSpeech();
    }

    @Override
    public void voiceEnroll(VoiceEntry voiceEntry, VoiceSdkCallback voiceSdkCallback) {
        CommonServiceManager.getInstance().voiceEnroll(voiceEntry,voiceSdkCallback);
    }

    @Override
    public void voiceRecogintion(VoiceEntry voiceEntry, VoiceSdkCallback voiceSdkCallback) {
        CommonServiceManager.getInstance().voiceRecogintion(voiceEntry,voiceSdkCallback);
    }

    @Override
    public void getAllVoiceId(VoiceSdkCallback voiceSdkCallback) {
        CommonServiceManager.getInstance().getAllVoiceId(voiceSdkCallback);
    }

    @Override
    public void deleteVoice(String id, VoiceSdkCallback voiceSdkCallback) {
        CommonServiceManager.getInstance().deleteVoice(id,voiceSdkCallback);
    }

    @Override
    public void queryvoiceById(String id, VoiceSdkCallback voiceSdkCallback) {
        CommonServiceManager.getInstance().queryvoiceById(id,voiceSdkCallback);
    }
}
