package com.data.smartvoice.voicerecognition;

import com.data.smartvoice.callback.VoiceSdkCallback;
import com.data.smartvoice.entry.VoiceEntry;

public class VoiceBaseServiceManager implements VoiceBaseServiceInterface {

    VoiceReconitionService mVoiceReconitionService;
    private static VoiceBaseServiceInterface mVoiceBaseServiceManager;

    private VoiceBaseServiceManager(){
        mVoiceReconitionService = new VoiceReconitionService();
    }

    public static VoiceBaseServiceInterface getInstance(){
        if(mVoiceBaseServiceManager == null) {
            mVoiceBaseServiceManager = new VoiceBaseServiceManager();
        }
        return mVoiceBaseServiceManager;
    }

    /**
     *
     * @param voiceEntry
     * @param voiceSdkCallback
     */
    @Override
    public void voiceEnroll(final VoiceEntry voiceEntry, final VoiceSdkCallback voiceSdkCallback) {
        mVoiceReconitionService.voiceEnroll(voiceEntry, voiceSdkCallback);
    }

    /**
     *
     * @param voiceEntry
     * @param voiceSdkCallback
     */
    @Override
    public void voiceRecogintion(VoiceEntry voiceEntry, final VoiceSdkCallback voiceSdkCallback) {
        mVoiceReconitionService.voiceRecogintion(voiceEntry, voiceSdkCallback);
    }

    @Override
    public void resetConfig() {
        mVoiceReconitionService.resetConfig();
    }

    /**
     *
     * @param id
     * @param voiceSdkCallback
     */
    @Override
    public void deleteVoice(String id, VoiceSdkCallback voiceSdkCallback) {
        mVoiceReconitionService.deleteVoice(id, voiceSdkCallback);
    }

    /**
     *
     * @param voiceSdkCallback
     */
    @Override
    public void getAllVoiceId(VoiceSdkCallback voiceSdkCallback) {
        mVoiceReconitionService.getAllVoice(voiceSdkCallback);
    }

    /**
     *
     * @param id
     * @param voiceSdkCallback
     */
    @Override
    public void queryvoiceById(String id, VoiceSdkCallback voiceSdkCallback) {
        mVoiceReconitionService.queryVoiceById(id,voiceSdkCallback);
    }
}

