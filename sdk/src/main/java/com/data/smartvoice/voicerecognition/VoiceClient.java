package com.data.smartvoice.voicerecognition;

import android.os.Looper;

import com.data.smartvoice.SmartVoiceSdkManager;
import com.data.smartvoice.callback.FileInputListener;
import com.data.smartvoice.callback.VoiceSdkCallback;
import com.data.smartvoice.entry.VoiceEntry;
import com.data.smartvoice.utils.FileUtil;
import com.data.smartvoice.utils.LogUtils;

public class VoiceClient implements FileInputListener {
    public final static int VOICEENROLLTYPE = 1;
    public final static int VOICERECOGNITIONTYPE = 2;
    private int currentType = VOICEENROLLTYPE;

    private VoiceSdkCallback mVoiceSdkCallback;
    private RecorderThread mRecorderThread;
    private String id;

    /**
     *
     * @param id
     * @param currentType
     * @param mVoiceSdkCallback
     */
    public VoiceClient(String id,int currentType,VoiceSdkCallback mVoiceSdkCallback){
        this.id = id;
        this.currentType = currentType;
        this.mVoiceSdkCallback = mVoiceSdkCallback;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId(){
        return id;
    }

    /**
     *
     * @param currentType
     * @param mVoiceSdkCallback
     */
    public VoiceClient(int currentType,VoiceSdkCallback mVoiceSdkCallback){
        this("",currentType,mVoiceSdkCallback);
    }

    public void startRecord(){
        mRecorderThread= new RecorderThread(this);
        mRecorderThread.startRecording();
        mRecorderThread.start();
    }

    public void stopRecord(){
        if (mRecorderThread != null && mRecorderThread.isRecording()) {
            LogUtils.d("stop record");
            mRecorderThread.stopRecording();
        }
    }

    private void switchApi(VoiceEntry entry){
        VoiceEntry voiceEntry = new VoiceEntry(id,entry.getAudio());
        switch (currentType){
            case VOICEENROLLTYPE:
                SmartVoiceSdkManager.getSmartVoiceSdkManager()
                        .voiceEnroll(voiceEntry, mVoiceSdkCallback);
                break;
            case VOICERECOGNITIONTYPE:
                SmartVoiceSdkManager.getSmartVoiceSdkManager().
                        voiceRecogintion(entry, mVoiceSdkCallback);
                break;
        }
    }

    @Override
    public void fileInputFinish() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    String audioStream = FileUtil.encodeBase64File(mRecorderThread.getPath());//将音频转64位流文件
                    VoiceEntry voiceEntry = new VoiceEntry();
                    voiceEntry.setAudio(audioStream);
                    switchApi(voiceEntry);
                } catch (Exception e) {
                    LogUtils.e("voiceEnroll e = "+e);
                    e.printStackTrace();
                }
                Looper.loop();
            }
        }).start();
    }

    @Override
    public void fileInputError() {
        LogUtils.e("fileInputError file input fail ");
    }


}
