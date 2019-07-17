package com.data.smartvoice.voicerecognition;

import com.data.smartvoice.callback.VoiceSdkCallback;
import com.data.smartvoice.entry.VoiceEntry;

public interface VoiceBaseServiceInterface {

    void voiceEnroll(VoiceEntry voiceEntry, VoiceSdkCallback voiceSdkCallback);

    void voiceRecogintion(VoiceEntry voiceEntry, VoiceSdkCallback voiceSdkCallback);

    void deleteVoice(String id, VoiceSdkCallback voiceSdkCallback);

    void getAllVoiceId(VoiceSdkCallback voiceSdkCallback);

    void queryvoiceById(String id, VoiceSdkCallback voiceSdkCallback);

    void resetConfig();
}
