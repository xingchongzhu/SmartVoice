package com.data.smartvoice.voicerecognition;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.data.smartvoice.AppCommonActivity;
import com.data.smartvoice.R;
import com.data.smartvoice.SmartVoiceSdkManager;
import com.data.smartvoice.callback.FileInputListener;
import com.data.smartvoice.callback.VoiceSdkCallback;
import com.data.smartvoice.entry.BaseResult;
import com.data.smartvoice.entry.DataResult;
import com.data.smartvoice.entry.RecogintionEntry;
import com.data.smartvoice.entry.VoiceEntry;
import com.data.smartvoice.utils.FileUtil;
import com.data.smartvoice.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;


public class VoiceActivity extends AppCommonActivity {

    private final int COUNTTIME = 15;
    private String TAG = "VoiceActivity";
    public final static String ID = "voice_id";
    public final static String TYPE = "voice_type";

    public final static String VOICERESULT = "voice_result";

    private Toolbar toolbar;
    private ImageButton audioBtn;
    private TextView hint;
    private AnimationSet mAnimationSet;
    private LinearLayout wave;
    private static final int MSG_WAVE2_ANIMATION = 2;
    private static final int MSG_UPDATE_STATE_HINT = 3;
    private static final int OFFSET = 1000; //每个动画的播放时间间隔
    private VoiceSdkCallback mVoiceSdkCallback;
    int currentType = VoiceClient.VOICERECOGNITIONTYPE;
    private VoiceClient mVoiceClient;
    private boolean requestResult;
    private int countTime = COUNTTIME;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_WAVE2_ANIMATION:
                    hint.setText(""+countTime);
                    if(countTime > 0) {
                        showWaveAnimation();
                    }else{
                        finishRecord();
                    }
                    break;
                case MSG_UPDATE_STATE_HINT:
                    int color = Color.WHITE;
                    String string = "";
                    switch (currentType){
                        case VoiceClient.VOICEENROLLTYPE:
                            string = mVoiceClient.getId()+" ";
                            if(requestResult){
                                string+= getString(R.string.enroll_sucess);
                            }else{
                                string+= getString(R.string.enroll_fail);
                                color = Color.RED;
                            }
                            break;
                        case VoiceClient.VOICERECOGNITIONTYPE:
                            if(requestResult){
                                string = getString(R.string.recognition_sucess);
                            }else{
                                string = getString(R.string.recognition_fail);
                                color = Color.RED;
                            }
                            break;
                    }
                    string += msg.obj;
                    hint.setVisibility(View.VISIBLE);
                    hint.setTextColor(color);
                    hint.setText(string);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_enroll);
        initView();
        initData();
        mAnimationSet = initAnimationSet();
    }

    /**
     *
     * @param activity
     * @param id
     */
    public static void startVoiceEnrollActivity(Activity activity,String id) {
        Intent intent = new Intent(activity, VoiceActivity.class);
        intent.putExtra(ID,id);
        intent.putExtra(TYPE,VoiceClient.VOICEENROLLTYPE);
        activity.startActivityForResult(intent,0);
    }

    /**
     *
     * @param activity
     */
    public static void startVoiceRecogintionActivity(Activity activity) {
        Intent intent = new Intent(activity, VoiceActivity.class);
        intent.putExtra(TYPE,VoiceClient.VOICERECOGNITIONTYPE);
        activity.startActivityForResult(intent,0);
    }

    private void initView() {
        toolbar = findViewById(R.id.tools);
        audioBtn = findViewById(R.id.record_audio);
        wave = findViewById(R.id.wave);
        hint = findViewById(R.id.result_hint);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult();
            }
        });

        audioBtn.setOnLongClickListener(new startRecordListener());
        audioBtn.setOnClickListener(new stopRecordListener());
    }

    private void setResult(){
        Intent intent = new Intent();
        intent.putExtra(VOICERESULT, requestResult);
        VoiceActivity.this.setResult(RESULT_OK,intent);
        VoiceActivity.this.finish();
    }

    private void initData(){
        mVoiceSdkCallback = new VoiceSdkCallback() {
            @Override
            public void onResponse(@NonNull BaseResult result) {
                audioBtn.setEnabled(true);
                Message message = mHandler.obtainMessage();

                switch (currentType){
                    case VoiceClient.VOICEENROLLTYPE:
                        requestResult = true;
                        message.obj = "";
                        LogUtils.d("onResponse enroll sucess result = "+result);
                        break;
                    case VoiceClient.VOICERECOGNITIONTYPE:
                        DataResult mDataResult = (DataResult)result;
                        LogUtils.d("onResponse recognittion sucess result = " + mDataResult);
                        if(mDataResult != null) {
                            requestResult = true;
                            ArrayList<RecogintionEntry> list = (ArrayList<RecogintionEntry>)mDataResult.getData();
                            if(list.size() > 0){
                                message.obj = list.get(0);
                                LogUtils.d(" recognittion sucess list = " + list.get(0));
                            }
                        }
                        break;
                }
                message.what = MSG_UPDATE_STATE_HINT;
                mHandler.sendMessage(message);
            }

            @Override
            public void onFailure(@Nullable String msg) {
                audioBtn.setEnabled(true);
                Message message = mHandler.obtainMessage();
                message.what = MSG_UPDATE_STATE_HINT;
                message.obj = msg;
                requestResult = false;
                mHandler.sendMessage(message);
                LogUtils.e("onResponse onFailure msg = "+msg);
            }
        };
        currentType = getIntent().getIntExtra(TYPE,VoiceClient.VOICEENROLLTYPE);
        switch (currentType){
            case VoiceClient.VOICEENROLLTYPE:
                toolbar.setTitle(R.string.voice_enroll_title);//注册需要带上id
                mVoiceClient = new VoiceClient(getIntent().getStringExtra(ID),currentType,mVoiceSdkCallback);
                break;
            case VoiceClient.VOICERECOGNITIONTYPE:
                toolbar.setTitle(R.string.voice_recognition_title);
                mVoiceClient = new VoiceClient(currentType,mVoiceSdkCallback);
                break;
        }

    }

    //录音动画
    private AnimationSet initAnimationSet() {
        AnimationSet as = new AnimationSet(true);
        ScaleAnimation sa = new ScaleAnimation(1f, 1.5f, 1f, 1.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(OFFSET * 2);
        sa.setRepeatCount(Animation.INFINITE);// 设置循环
        AlphaAnimation aa = new AlphaAnimation(1, 0.1f);
        aa.setDuration(OFFSET * 2);
        aa.setRepeatCount(Animation.INFINITE);//设置循环
        as.addAnimation(sa);
        as.addAnimation(aa);
        return as;
    }

    private void showWaveAnimation() {
        countTime--;
        mHandler.sendEmptyMessageDelayed(MSG_WAVE2_ANIMATION, OFFSET);
    }

    private void clearWaveAnimation() {
        mHandler.removeMessages(MSG_WAVE2_ANIMATION);
        wave.clearAnimation();
    }

    //长按录音，松开后自动执行短按操作
    class startRecordListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {
            sartRecord();
            return false;
        }
    }

    //短按停止录音，直接点击短按无效
    class stopRecordListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finishRecord();
        }
    }

    private void sartRecord(){
        hint.setText(""+countTime);
        wave.startAnimation(mAnimationSet);
        showWaveAnimation();
        requestResult = false;
        hint.setTextColor(Color.WHITE);
        hint.setVisibility(View.VISIBLE);
        countTime = COUNTTIME;
        mVoiceClient.startRecord();
    }

    private void finishRecord(){
        audioBtn.setEnabled(false);
        clearWaveAnimation();
        //hint.setVisibility(View.GONE);
        hint.setText(getString(R.string.wait_result));
        mVoiceClient.stopRecord();
    }
}
