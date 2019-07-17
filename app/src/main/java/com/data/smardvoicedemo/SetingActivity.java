package com.data.smardvoicedemo;


import android.app.Activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.graphics.ColorUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.data.smartvoice.SmartVoiceSdkManager;
import com.data.smartvoice.common.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * 本类代码同定位业务本身无关，负责现实列表
 *
 * @author baidu
 *
 */
public class SetingActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private final String TAG = "CommonActivity";
    private final static String split = ";";
    TextView textViewHttp;
    TextView textViewport;
    TextView current_http;
    Button settingBtn;
    ListView historyView;
    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    MySimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        setStatusBar(Color.WHITE);
        initView();
        initData();
    }

    private void initView() {
        textViewHttp = findViewById(R.id.http);
        textViewport = findViewById(R.id.port);
        settingBtn = findViewById(R.id.setting_btn);
        historyView = findViewById(R.id.history);
        current_http = findViewById(R.id.current_http);

        settingBtn.setOnClickListener(this);
        historyView.setOnItemClickListener(this);
    }

    private void initData() {
        getShar();
        // 使用SimpleAdapter来作为ListView的适配器，比ArrayAdapter能展现更复杂的布局效果。为了显示较为复杂的ListView的item效果，需要写一个xml布局文件，来设置ListView中每一个item的格式。
        adapter = new MySimpleAdapter(this, list,
                R.layout.item);
        historyView.setAdapter(adapter);
        String current = (String) MySharedPreferences.get(this, MySharedPreferences.default_key, Config.TTsConfig.HOST + ":" + Config.TTsConfig.PORT);
        current_http.setText(current);
    }

    private void getShar(){
        String string = (String) MySharedPreferences.get(this, MySharedPreferences.history_key, " ");
        list.clear();
        if (!TextUtils.isEmpty(string)) {
            String[] strings = string.split(File.separator);
            if (strings != null && strings.length > 0) {
                for (String str : strings) {
                    String[] strs = str.split(split);
                    if (strs != null && strs.length > 1) {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("http", strs[0]);
                        map.put("port", strs[1]);
                        list.add(map);
                    }
                }
            }
        }
        if(list.size() <= 0){
            String history = "";
            Map<String, String> map = new HashMap<String, String>();
            map.put("http", "172.16.23.5");
            map.put("port", "8887");
            list.add(map);
            history+= File.separator+"172.16.23.5" + split + "8887";

            Map<String, String> map1 = new HashMap<String, String>();
            map1.put("http", "172.16.23.6");
            map1.put("port", "8883");
            list.add(map1);
            history+= File.separator+"172.16.23.6" + split + "8883";
            MySharedPreferences.put(this, MySharedPreferences.history_key, history);

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_btn:
                String http = textViewHttp.getText().toString();
                String port = textViewport.getText().toString();
                if (TextUtils.isEmpty(http)) {
                    Toast.makeText(this, "服务器地址不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(port) ){
                    Toast.makeText(this, "服务器端口不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
                int ports = Integer.parseInt(port);

                String history = (String) MySharedPreferences.get(this, MySharedPreferences.history_key, " ");
                String temp = http + split + port;
                if(!current_http.equals(temp)) {

                    current_http.setText(http + ":" + port);
                    Log.d("zxc","history = "+history);
                    if (!history.contains(temp)) {
                        history +=File.separator+temp;
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("http", http);
                        map.put("port", port);
                        list.add(map);
                        adapter.setmData(list);
                        MySharedPreferences.put(this, MySharedPreferences.history_key, history);
                    }
                    SmartVoiceSdkManager.getSmartVoiceSdkManager().dumpConfig();
                    SmartVoiceSdkManager.getSmartVoiceSdkManager().setTtsConfig(http,ports);
                    Toast.makeText(this,"设置成功",Toast.LENGTH_LONG).show();
                }
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Map<String, String> map = list.get(position);
        for(Map.Entry<String, String> mapentru : map.entrySet()){
            if(mapentru.getKey().equals("http")) {
                textViewHttp.setText(mapentru.getValue());
            }else if(mapentru.getKey().equals("port")) {
                textViewport.setText(mapentru.getValue());
            }
        }
    }

    /**
     * Android 6.0 以上设置状态栏颜色
     */
    protected void setStatusBar(@ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 设置状态栏底色颜色
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(color);

            // 如果亮色，设置状态栏文字为黑色
            if (isLightColor(color)) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }

    }

    /**
     * 判断颜色是不是亮色
     *
     * @param color
     * @return
     * @from https://stackoverflow.com/questions/24260853/check-if-color-is-dark-or-light-in-android
     */
    private boolean isLightColor(@ColorInt int color) {
        return ColorUtils.calculateLuminance(color) >= 0.5;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
