package com.data.smartvoice.tts.audioutil;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.data.smartvoice.common.Config;

import java.io.File;
import java.io.IOException;

public class MediaPlayerHelper implements OnCompletionListener,OnBufferingUpdateListener,OnErrorListener,OnInfoListener,
        OnPreparedListener,OnSeekCompleteListener,OnVideoSizeChangedListener,SurfaceHolder.Callback{
    public static final String TAG="MediaPlayerHelper";
    private String[] ext={".3gp",".3GP",".mp4", ".MP4",".mp3", ".ogg",".OGG",".MP3",".wav",".WAV"};//定义我们支持的文件格式
    public Holder uiHolder;
    private MediaPlayerHelperCallBack MediaPlayerHelperCallBack=null;
    private static MediaPlayerHelper instance;
    private int delaySecondTime=0;
    private String currentfile;
    private MediaState mMediaState = MediaState.wait;
    public enum MediaState{
        wait,
        prePrepare,
        prepare,
        playing,
    }

    /** 状态枚举 */
    public enum CallBackState{
        PREPARE("MediaPlayer--准备完毕"),
        COMPLETE("MediaPlayer--播放结束"),
        ERROR("MediaPlayer--播放错误"),
        EXCEPTION("MediaPlayer--播放异常"),
        INFO("MediaPlayer--播放开始"),
        PROGRESS("MediaPlayer--播放进度回调"),
        SEEK_COMPLETE("MediaPlayer--拖动到尾端"),
        VIDEO_SIZE_CHANGE("MediaPlayer--读取视频大小"),
        BUFFER_UPDATE("MediaPlayer--更新流媒体缓存状态"),
        FORMATE_NOT_SURPORT("MediaPlayer--音视频格式可能不支持"),
        SURFACEVIEW_NULL("SurfaceView--还没初始化"),
        SURFACEVIEW_NOT_ARREADY("SurfaceView--还没准备好"),
        SURFACEVIEW_CHANGE("SurfaceView--Holder改变"),
        SURFACEVIEW_CREATE("SurfaceView--Holder创建"),
        SURFACEVIEW_DESTROY("SurfaceView--Holder销毁");

        private final String state;

        CallBackState(String var3) {
            this.state = var3;
        }

        public String toString() {
            return this.state;
        }
    }

    /**
     * 获得静态类
     * @return 类对象
     */
    public static synchronized MediaPlayerHelper getInstance(){
        if(instance == null){
            instance=new MediaPlayerHelper();
        }
        return instance;
    }

    /**
     * 构造函数
     */
    public MediaPlayerHelper() {
        this.uiHolder = new Holder ();
        uiHolder.player = new MediaPlayer();
        uiHolder.player.setOnCompletionListener(this);
        uiHolder.player.setOnErrorListener(this);
        uiHolder.player.setOnInfoListener(this);
        uiHolder.player.setOnPreparedListener(this);
        uiHolder.player.setOnSeekCompleteListener(this);
        uiHolder.player.setOnVideoSizeChangedListener(this);
        uiHolder.player.setOnBufferingUpdateListener(this);
    }

    /**
     * 设置播放进度时间间隔
     * @param time 时间
     * @return 类对象
     */
    public MediaPlayerHelper setProgressInterval(int time){
        delaySecondTime=time;
        return instance;
    }

    public MediaState getMediaState(){
        return mMediaState;
    }

    public String getCurrentfile(){
        return currentfile;
    }

    /**
     * 设置SurfaceView
     * @param surfaceView 控件
     * @return 类对象
     */
    public MediaPlayerHelper setSurfaceView (SurfaceView surfaceView) {
        if(surfaceView==null){
            callBack(CallBackState.SURFACEVIEW_NULL, uiHolder.player);
        }else {
            uiHolder.surfaceView = surfaceView;
            uiHolder.surfaceHolder = uiHolder.surfaceView.getHolder();
            uiHolder.surfaceHolder.addCallback(this);
        }
        return instance;
    }

    /**
     * 设置回调
     * @param MediaPlayerHelperCallBack 回调
     * @return 类对象
     */
    public MediaPlayerHelper setMediaPlayerHelperCallBack (MediaPlayerHelperCallBack MediaPlayerHelperCallBack) {
        this.MediaPlayerHelperCallBack = MediaPlayerHelperCallBack;
        return instance;
    }

    /**
     * 释放资源
     */
    public void release () {
        mMediaState = MediaState.wait;
        if(uiHolder.player != null){
            uiHolder.player.release();
            uiHolder.player = null;
        }
        refress_time_handler.removeCallbacks(refress_time_Thread);
    }

    public void stopPlay(){
        if(uiHolder.player != null){
            uiHolder.player.stop();
        }
        deleteFile();
    }
    /**
     * 通过Assets文件名播放Assets目录下的文件
     * @param context 引用
     * @param assetName 名字,带后缀，比如:text.mp3
     * @return 是否成功
     */
    public boolean playAsset (Context context, String assetName) {
        if(!checkAvalable(assetName)){
            return false;
        }
        AssetManager assetMg= context.getAssets();
        try {
            uiHolder.assetDescriptor = assetMg.openFd(assetName);
            uiHolder.player.setDisplay(null);
            uiHolder.player.reset();
            uiHolder.player.setDataSource(uiHolder.assetDescriptor.getFileDescriptor(), uiHolder.assetDescriptor.getStartOffset(), uiHolder.assetDescriptor.getLength());
            uiHolder.player.prepare();
        } catch (Exception e) {
            callBack(CallBackState.ERROR, uiHolder.player);
            return false;
        }
        return true;
    }

    /**
     * 通过文件路径或者网络路径播放音视频
     * @param localPathOrURL 路径
     * @return 是否成功
     */
    public boolean play(final String localPathOrURL) {
        if(!checkAvalable(localPathOrURL)){
            return false;
        }
        mMediaState = MediaState.prePrepare;

        currentfile = localPathOrURL;
        try {
            /**
             * 其实仔细观察优酷app切换播放网络视频时的确像是这样做的：先暂停当前视频，
             * 让mediaplayer与先前的surfaceHolder脱离“绑定”,当mediaplayer再次准备好要start时，
             * 再次让mediaplayer与surfaceHolder“绑定”在一起，显示下一个要播放的视频。
             * 注：MediaPlayer.setDisplay()的作用： 设置SurfaceHolder用于显示的视频部分媒体。
             */
            uiHolder.player.setDisplay(null);
            uiHolder.player.reset();
            uiHolder.player.setDataSource(localPathOrURL);
            uiHolder.player.prepare();
            uiHolder.player.start();
        } catch (Exception e) {
            mMediaState = MediaState.wait;
            callBack(CallBackState.ERROR, uiHolder.player);
            return false;
        }
        return true;
    }

    public void start(){
        mMediaState = MediaState.playing;
        uiHolder.player.start();
    }

    /**
     * 获得流媒体对象
     * @return 对象
     */
    public MediaPlayer getMediaPlayer(){
        return uiHolder.player;
    }

    /**
     * 检查是否可以播放
     * @param path 参数
     * @return 结果
     */
    private boolean checkAvalable(String path){
        boolean surport=false;
        for(int i=0;i<ext.length;i++){
            if(path.endsWith(ext[i])){
                surport=true;
            }
        }
        if(!surport){
            callBack(CallBackState.FORMATE_NOT_SURPORT, uiHolder.player);
            Log.v(TAG, CallBackState.FORMATE_NOT_SURPORT.toString());
            return false;
        }
        return true;
    }

    /**
     *  播放进度定时器
     */
    Handler refress_time_handler = new Handler();
    Runnable refress_time_Thread = new Runnable(){
        public void run() {
            refress_time_handler.removeCallbacks(refress_time_Thread);
            if(uiHolder.player!=null&&uiHolder.player.isPlaying()){
                callBack(CallBackState.PROGRESS, 100*uiHolder.player.getCurrentPosition()/uiHolder.player.getDuration());
            }
            refress_time_handler.postDelayed(refress_time_Thread,delaySecondTime);
        }
    };

    /**
     * 封装UI
     */
    private static final class Holder {
        private SurfaceHolder surfaceHolder;
        private MediaPlayer player;
        private SurfaceView surfaceView;
        private AssetFileDescriptor assetDescriptor;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {}

    @Override
    public void onCompletion(MediaPlayer mp) {
        //callBack(CallBackState.PROGRESS, 100);
        callBack(CallBackState.COMPLETE, mp);
        deleteFile();
    }

    private void deleteFile(){
        mMediaState = MediaState.wait;
        if(!TextUtils.isEmpty(currentfile)) {
            File file = new File(currentfile);
            if(file.exists()){
                file.delete();
            }
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        callBack(CallBackState.ERROR, mp, what, extra);
        deleteFile();
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        callBack(CallBackState.INFO, mp, what, extra);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        try {
            if(uiHolder.surfaceView!=null) {
                uiHolder.player.setDisplay(uiHolder.surfaceHolder);
            }
            mMediaState = MediaState.prepare;
            uiHolder.player.start();
            refress_time_handler.postDelayed(refress_time_Thread, delaySecondTime);
        } catch (Exception e) {
            callBack(CallBackState.EXCEPTION,mp);
        }
        callBack(CallBackState.PREPARE,mp);
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        callBack(CallBackState.SEEK_COMPLETE,mp);
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        callBack(CallBackState.VIDEO_SIZE_CHANGE, width, height);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(uiHolder.player != null && holder != null){
            uiHolder.player.setDisplay(holder);
        }
        callBack(CallBackState.SURFACEVIEW_CREATE,holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        callBack(CallBackState.SURFACEVIEW_CHANGE,format, width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        callBack(CallBackState.SURFACEVIEW_DESTROY,holder);
    }

    /**
     * 统一回调
     * @param state 状态
     * @param args 若干参数
     */
    private void callBack(CallBackState state,Object... args){
        if(MediaPlayerHelperCallBack!=null) {
            MediaPlayerHelperCallBack.onCallBack(state, instance,args);
        }
    }

    /**
     * 回调接口
     */
    public interface MediaPlayerHelperCallBack {
        /**
         * 状态回调
         * @param state 状态
         * @param mediaPlayerHelper MediaPlayer
         * @param args 若干参数
         */
        void onCallBack(CallBackState state, MediaPlayerHelper mediaPlayerHelper, Object... args);
    }

    MediaPlayer mMediaPlayer;
    public  void playtest(final String text) {
        releaseMediaPlayer();
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource("http://" + Config.TTsConfig.HOST + ":" + Config.TTsConfig.PORT + "/synthesize?text=" + text);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            releaseMediaPlayer();
                        }
                    });
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放上一次MediaPlayer资源
     */
    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

}
