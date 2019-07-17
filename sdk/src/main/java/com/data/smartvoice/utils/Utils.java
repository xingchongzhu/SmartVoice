package com.data.smartvoice.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Environment;


import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Utils {

    public static Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {

        Bitmap bitmap = null;

        // 获取视频的缩略图

        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);

        if(bitmap != null){  //如果视频已损坏或者格式不支持可能返回null

            System.out.println("w"+bitmap.getWidth());

            System.out.println("h"+bitmap.getHeight());

            bitmap =ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

        }

        return bitmap;

    }

    public static String getTime() {
        Date curDate =  new Date(System.currentTimeMillis());
        return curDate.toString();
    }

    public static Date stringToDate(String dateString) {
        ParsePosition position = new ParsePosition(0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateValue = simpleDateFormat.parse(dateString, position);
        return dateValue;
    }

    public static String saveBitmap(Context context,String path, Bitmap bitmap) {
        File filePic;
        try {
            filePic = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+path);
            if (!filePic.exists()) {
                //filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        return filePic.getAbsolutePath();
    }


    public static JSONObject SendGetRequest(String strUri) {
        LogUtils.i("get url is : " + strUri);
        byte[] buffer = new byte[1024*8];
        HttpURLConnection conn = null;

        try {
            URL url = new URL(strUri);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
                LogUtils.i("get request success.");
                InputStream in = conn.getInputStream();
                int len = in.read(buffer);
                String str = new String(buffer, 0, len, "UTF-8");
                JSONObject jsonObject = new JSONObject(str);
                in.close();
                return jsonObject;
            } else {
                LogUtils.e("get request failed, error is "+conn.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            conn.disconnect();
        }

        return null;
    }

}
