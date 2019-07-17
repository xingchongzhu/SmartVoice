package com.data.smartvoice.tts;

import com.data.smartvoice.callback.SdkCallback;

public interface TtsBaseServiceInterface {

    void speechText(String text, SdkCallback sdkCallback);

    void interruptSpeech();

    void resetConfig();
}
