package com.data.smartvoice.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * created by sain on 12/10/18
 */
public class ApkUtil {
    private static final String TAG = "ApkUtil";

    public static String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/";
    public static String APK_SYMBOL = "DataAppServer_";
    public static String APK_NAME = "DataAppServer.apk";


    private static void install(Context context, String filePath) {
        Log.i(TAG, "开始执行安装: " + filePath);
        String packageName = context.getPackageName();
        File apkFile = new File(filePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.w(TAG, "版本大于 N ，开始使用 fileProvider 进行安装");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(
                    context
                    , packageName +".dataserverfileprovider"
                    , apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            Log.w(TAG, "正常进行安装");
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }


    /**
     * 复制文件到SD卡
     * @param context
     * @param fileName 复制的文件名
     * @param path  保存的目录路径
     * @return
     */
    public static boolean copyAssetsFile(Context context, String fileName, String path) {
        String apkName = checkApkFileExist(context, fileName);
        if (apkName == null) {
            return false;
        }

        try {
            InputStream mInputStream = context.getAssets().open(apkName);
            File file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }
            File mFile = new File(path + APK_NAME);// 受FileProvider限制, 复制完成后,统一改名为DataAppServer.apk
            mFile.deleteOnExit();
            mFile.createNewFile();
            FileOutputStream mFileOutputStream = new FileOutputStream(mFile);
            byte[] mbyte = new byte[1024];
            int i = 0;
            while((i = mInputStream.read(mbyte)) > 0){
                mFileOutputStream.write(mbyte, 0, i);
            }
            mInputStream.close();
            mFileOutputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, fileName + " not exists or write err");
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private static String contain(String[] arr, String targetValue) {
        for (String s : arr) {
            if (s.startsWith(targetValue))
                return s;
        }
        return null;
    }

    public static String checkApkFileExist(Context context, String name) {
        try {
            String[] listFiles = context.getAssets().list("");
            String apkName = contain(listFiles, name);
            Log.i(TAG, "checkApkFileExist: " + apkName);
            if (apkName != null) {
                return apkName;
            } else {
                Log.e(TAG, "checkApkFileExist: Apk Not Exist!");
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取指定包名的版本号
     *
     * @param context
     *            本应用程序上下文
     * @param packageName
     *            你想知道版本信息的应用程序的包名
     * @return
     * @throws Exception
     */
    public static String getVersionName(Context context, String packageName) throws Exception {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo = packageManager.getPackageInfo(packageName, 0);
        String version = packInfo.versionName;
        return version;
    }

    public static int getVersionCode(Context context, String packageName){
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int versionCode = packInfo.versionCode;
        Log.d(TAG, "getVersionCode: " + versionCode);
        return versionCode;
    }

    /**
     * 获取apk包的信息：版本号，名称，图标等
     * @param absPath apk包的绝对路径
     * @param context 
     */
    public static int getApkInfo(String absPath, Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = pm.getPackageArchiveInfo(absPath, PackageManager.GET_ACTIVITIES);
        if (pkgInfo != null) {
            ApplicationInfo appInfo = pkgInfo.applicationInfo;
            appInfo.sourceDir = absPath;
            appInfo.publicSourceDir = absPath;
            String appName = pm.getApplicationLabel(appInfo).toString();
            String packageName = appInfo.packageName;
            int versionCode = pkgInfo.versionCode;
            Log.i(TAG, "getApkInfo: appName=" + appName + " ,version=" + versionCode);
            return versionCode;
        }

        return 1;
    }



    /**
     * serverApp是否已安装
     * @param mContext
     * @param packageName 包名
     * @return
     */
    public static boolean isAppInstalled(Context mContext, String packageName){
        PackageInfo mInfo;
        try {
            mInfo = mContext.getPackageManager().getPackageInfo(packageName, 0 );
        } catch (Exception e) {
            mInfo = null;
            Log.e(TAG, "serverApp 没有安装");
        }
        if(mInfo == null){
            //DataServiceApi.getInstance().reset();
            return false;
        }else {
            return true;
        }
    }


    /**
     * 运行安装好的APK
     * @param mContext
     * @param packageName
     * @param className
     */
    private void runApk(Context mContext, String packageName, String className ) {
        Intent mIntent = new Intent(Intent.ACTION_VIEW);
        mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName mComponentName = new ComponentName(packageName, className);
        mIntent.setComponent(mComponentName);
        mIntent.putExtra("content", "第一个app传过来的数据");
        mContext.startActivity(mIntent);
    }



    public  static String getMessageDigest(byte[] buffer) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(buffer);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getSignature(Context context) {
        String sigMD5 = "";
        try {
            Signature sig = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES).signatures[0];
            sigMD5 = getMessageDigest(sig.toByteArray());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return sigMD5;
    }

}
