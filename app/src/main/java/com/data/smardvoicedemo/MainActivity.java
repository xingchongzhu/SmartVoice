package com.data.smardvoicedemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.data.smartvoice.SmartVoiceSdkManager;
import com.data.smartvoice.callback.SdkCallback;
import com.data.smartvoice.callback.VoiceSdkCallback;
import com.data.smartvoice.entry.BaseResult;
import com.data.smartvoice.entry.SpeakerEntry;
import com.data.smartvoice.voicerecognition.VoiceActivity;


public class MainActivity extends AppCommonActivity implements View.OnClickListener {

    private String TAG = "MainActivity";

    private TextView textResult;
    private EditText ttsEdit;
    private Button ttsbtn;
    private Button voice_enroll_btn;
    private Button voice_recogintion_btn;
    private Button delete_voice_btn;
    private Button get_all_voice_btn;
    private Button query_voice_btn;
    private Button setting_btn;
    private final static String appid = "65454c04c01000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        SmartVoiceSdkManager.getSmartVoiceSdkManager().init(getApplicationContext(),appid);
    }

    private void initView() {
        textResult = findViewById(R.id.result);
        ttsEdit = findViewById(R.id.tts_edit);
        ttsbtn = findViewById(R.id.tts_btn);
        delete_voice_btn = findViewById(R.id.delete_voice_btn);
        get_all_voice_btn = findViewById(R.id.get_all_voice_btn);
        query_voice_btn = findViewById(R.id.query_voice_btn);
        setting_btn = findViewById(R.id.setting_btn);

        voice_recogintion_btn = findViewById(R.id.voice_recogintion_btn);
        voice_enroll_btn = findViewById(R.id.voice_enroll_btn);
        voice_enroll_btn.setOnClickListener(this);
        voice_recogintion_btn.setOnClickListener(this);
        ttsbtn.setOnClickListener(this);
        delete_voice_btn.setOnClickListener(this);
        get_all_voice_btn.setOnClickListener(this);
        query_voice_btn.setOnClickListener(this);
        setting_btn.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        String string = ttsEdit.getText().toString();
        switch (v.getId()) {
            case R.id.setting_btn:
                Intent intent = new Intent();
                intent.setClass(this,SetingActivity.class);
                startActivity(intent);
                break;
            case R.id.tts_btn:
                SmartVoiceSdkManager.getSmartVoiceSdkManager().speechText(string, new SdkCallback() {
                    @Override
                    public void onResponse(@NonNull BaseResult result) {
                        Log.d(TAG, "speechText onResponse result = " + result);
                    }

                    @Override
                    public void onFailure(@Nullable Object msg) {
                        Toast.makeText(MainActivity.this,""+msg,Toast.LENGTH_LONG).show();
                        Log.d(TAG, "speechText onFailure msg = " + msg);
                    }
                });
                break;
            case R.id.voice_enroll_btn:
                VoiceActivity.startVoiceEnrollActivity(this, string);
                break;
            case R.id.voice_recogintion_btn:
                VoiceActivity.startVoiceRecogintionActivity(this);
                break;
            case R.id.delete_voice_btn:
                SmartVoiceSdkManager.getSmartVoiceSdkManager().deleteVoice(string
                        , new VoiceSdkCallback() {
                            @Override
                            public void onResponse(@NonNull BaseResult result) {
                                if(result != null) {
                                    show("deleteVoice sucess ");
                                }
                                Log.d(TAG, "deleteVoice onResponse result = " + result);
                            }

                            @Override
                            public void onFailure(@Nullable String msg) {
                                show("deleteVoice onFailure msg = " + msg);

                                Log.d(TAG, "deleteVoice onFailure msg = " + msg);
                            }
                        });
                break;
            case R.id.get_all_voice_btn:
                SmartVoiceSdkManager.getSmartVoiceSdkManager().getAllVoiceId(
                        new VoiceSdkCallback() {
                            @Override
                            public void onResponse(@NonNull BaseResult result) {
                                SpeakerEntry mDataResult = (SpeakerEntry) result;
                                Log.d(TAG,"getAllVoiceId onResponse recognittion sucess result = " + mDataResult);
                                if (mDataResult != null) {
                                    textResult.setText("getAllVoiceId sucess\n " +mDataResult.getData());
                                    //show("getAllVoiceId sucess = " + mDataResult.getData());
                                    Log.d(TAG, "getAllVoiceId recognittion sucess list = " + mDataResult.getData());
                                }
                            }

                            @Override
                            public void onFailure(@Nullable String msg) {
                                Log.d(TAG, "getAllVoiceId onFailure msg = " + msg);
                                show("getAllVoiceId onFailure msg = " + msg);

                            }
                        });
                break;
            case R.id.query_voice_btn:
                SmartVoiceSdkManager.getSmartVoiceSdkManager().queryvoiceById(string,
                        new VoiceSdkCallback() {
                            @Override
                            public void onResponse(@NonNull BaseResult result) {
                                SpeakerEntry mDataResult = (SpeakerEntry) result;
                                Log.d(TAG,"queryvoiceById onResponse recognittion sucess result = " + mDataResult);
                                if (mDataResult != null) {
                                    show("queryvoiceById sucess = " + mDataResult.getData());
                                    Log.d(TAG, "queryvoiceById recognittion sucess list = " + mDataResult.getData());
                                }
                            }

                            @Override
                            public void onFailure(@Nullable String msg) {
                                show("queryvoiceById onFailure msg = " + msg);
                                Log.d(TAG, "queryvoiceById onFailure msg = " + msg);
                            }
                        });
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case RESULT_OK:
               boolean result = data.getBooleanExtra(VoiceActivity.VOICERESULT,false);
               Log.d(TAG,"result = "+result);
        }
    }

    public void show(String str){
        Toast.makeText(this,str,Toast.LENGTH_LONG).show();
    }
}
