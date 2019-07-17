package com.data.smartvoice.tts;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.data.smartvoice.callback.SdkCallback;
import com.data.smartvoice.callback.VoiceSdkCallback;
import com.data.smartvoice.entry.BaseResult;
import com.data.smartvoice.entry.TTS;
import com.data.smartvoice.tts.audioutil.MediaPlayerHelper;
import com.data.smartvoice.tts.audioutil.MeidaPlayerManager;
import com.data.smartvoice.utils.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.data.smartvoice.tts.audioutil.MediaPlayerHelper.CallBackState.COMPLETE;
import static com.data.smartvoice.tts.audioutil.MediaPlayerHelper.CallBackState.ERROR;

public class TtsServiceManager implements MediaPlayerHelper.MediaPlayerHelperCallBack, TtsBaseServiceInterface {

    static TtsBaseServiceInterface mTtsServiceManager;
    TTsSrvice mTTsSrvice;
    private boolean isRunning;
    private List<String> speechTextQueue = new ArrayList<>();
    private List<String> speechFileQueue = new ArrayList<>();

    private TtsServiceManager() {
        mTTsSrvice = new TTsSrvice();
        //MeidaPlayerManager.getInstance(this);
		 MediaPlayerHelper.getInstance().setMediaPlayerHelperCallBack(this);
    }

    public static TtsBaseServiceInterface getInstance() {
        if (mTtsServiceManager == null) {
            mTtsServiceManager = new TtsServiceManager();
        }
        return mTtsServiceManager;
    }

    /**
     * @param text
     * @param sdkCallback
     */
    @Override
    public void speechText(final String text, final SdkCallback sdkCallback) {
        isRunning = false;
        mTTsSrvice.spliteMuliteText(text, new SdkCallback<TTS>() {
            @Override
            public void onResponse(@NonNull TTS result) {
                LogUtils.d("onResponse result = " + result);
                voiceSynthesis();
                if (sdkCallback != null) {
                    sdkCallback.onResponse(result);
                }
            }

            @Override
            public void onFailure(@Nullable Object msg) {
                LogUtils.e("onFailure msg = " + msg);
                if (sdkCallback != null) {
                    sdkCallback.onFailure(msg);
                }
            }
        }, speechTextQueue);
    }

    @Override
    public void onCallBack(MediaPlayerHelper.CallBackState state, MediaPlayerHelper mediaPlayerHelper, Object... args) {
        if (state == COMPLETE || state == ERROR) {
            playNext();
        }
    }

    @Override
    public void resetConfig() {
        mTTsSrvice.resetConfig();
    }

    @Override
    public void interruptSpeech() {
        isRunning = true;
        //MeidaPlayerManager.getInstance(this).interrupt();
		MediaPlayerHelper.getInstance().stopPlay();
        speechTextQueue.clear();
        speechFileQueue.clear();
    }

    private void voiceSynthesis() {
        if (!isRunning && speechTextQueue.size() > 0) {
            playNext();
            String text = speechTextQueue.get(0);
            mTTsSrvice.speechText(text, new VoiceSdkCallback() {
                @Override
                public void onResponse(@NonNull BaseResult result) {
                    if (speechTextQueue.size() > 0) {
                        speechTextQueue.remove(0);
                    }
                    if (speechTextQueue.size() > 0) {
                        voiceSynthesis();
                    }
                    playNext();
                }

                @Override
                public void onFailure(@Nullable String msg) {
                    if (speechTextQueue.size() > 0) {
                        speechTextQueue.remove(0);
                    }
                    if (speechTextQueue.size() > 0) {
                        voiceSynthesis();
                    }
                    playNext();
                }

            }, speechFileQueue);
        }
    }

    private void playNext() {
       /* if (speechFileQueue.size() > 0 && MeidaPlayerManager.getInstance(this).prepareNext(speechFileQueue.get(0))) {
            speechFileQueue.remove(0);
        }
        MeidaPlayerManager.getInstance(this).play();*/
		if (speechFileQueue.size() > 0 && !MediaPlayerHelper.getInstance().getMediaPlayer().isPlaying()) {
            MediaPlayerHelper.getInstance().play(speechFileQueue.get(0));
            speechFileQueue.remove(0);
        }
    }

}
