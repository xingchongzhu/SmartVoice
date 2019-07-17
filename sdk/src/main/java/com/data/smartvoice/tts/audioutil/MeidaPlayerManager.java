package com.data.smartvoice.tts.audioutil;

import android.util.Log;

import com.data.smartvoice.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class MeidaPlayerManager{
    private final int MAX_COUNT = 2;
    private List<MediaPlayerHelper> mediaPlayerHelperListist = new ArrayList<>();

    private static MeidaPlayerManager mMeidaPlayerManager;
    private MediaPlayerHelper mMediaPlayerHelper;

    private MeidaPlayerManager(MediaPlayerHelper.MediaPlayerHelperCallBack mediaPlayerHelperCallBack) {
        for (int i = 0; i < MAX_COUNT; i++) {
            mediaPlayerHelperListist.add(new MediaPlayerHelper());
            mediaPlayerHelperListist.get(i).setMediaPlayerHelperCallBack(mediaPlayerHelperCallBack);
        }
    }

    public static MeidaPlayerManager getInstance(MediaPlayerHelper.MediaPlayerHelperCallBack mediaPlayerHelperCallBack) {
        if (mMeidaPlayerManager == null) {
            mMeidaPlayerManager = new MeidaPlayerManager(mediaPlayerHelperCallBack);
        }
        return mMeidaPlayerManager;
    }

    public void play() {
        if(mMediaPlayerHelper != null && mMediaPlayerHelper.getMediaState() == MediaPlayerHelper.MediaState.prepare && !isPlaying()) {
            Log.d("zxc","paly path = "+mMediaPlayerHelper.getCurrentfile()+" mMediaPlayerHelper = "+mMediaPlayerHelper);

            mMediaPlayerHelper.start();
        }
    }

    public boolean isPlaying(){
        for (int i = 0; i < MAX_COUNT; i++) {
            if (mediaPlayerHelperListist.get(i).getMediaPlayer().isPlaying()) {
                return true;
            }
        }
        return false;
    }

    public void interrupt(){
        for (int i = 0; i < MAX_COUNT; i++) {
            if (mediaPlayerHelperListist.get(i).getMediaState() == MediaPlayerHelper.MediaState.playing) {
                mediaPlayerHelperListist.get(i).stopPlay();
            }
        }
    }

    public boolean prepareNext(String path) {
        for (int i = 0; i < MAX_COUNT; i++) {
            if (mediaPlayerHelperListist.get(i).getMediaState() == MediaPlayerHelper.MediaState.wait) {
                mMediaPlayerHelper = mediaPlayerHelperListist.get(i);
                //mMediaPlayerHelper.prepare(path);
                return true;
            }
        }
        return false;
    }
}
