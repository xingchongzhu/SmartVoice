package com.data.smartvoice.common;

import android.os.Environment;

public class Config {

    public final static String ROOTDIR  = Environment.getExternalStorageDirectory().getAbsolutePath();


    public final static String SDKFILEDIR = "/voicesdk";
    public final static String TEMPFILENAME  = "tts_source";

    public final static class TTsConfig{
        public static String HOST = "172.16.23.6";
        public static int    PORT = 8883;
        public static String DATA_FORMAT = "yyyy-MM-dd HH:mm:ss";
        public static int    TIMEOUT = 15;
    }

    public final static class VoiceRecognitionConfig{
        public static String HOST = "172.16.23.12";
        public static int    PORT = 8080;
        public static String DATA_FORMAT = "yyyy-MM-dd HH:mm:ss";
        public static int    TIMEOUT = 15;
    }

    public final static class AppidAuthentication{
        public static String HOST = "54.214.166.240";
        public static int    PORT = 9000;
        public static String DATA_FORMAT = "yyyy-MM-dd HH:mm:ss";
        public static int    TIMEOUT = 15;
    }
}
