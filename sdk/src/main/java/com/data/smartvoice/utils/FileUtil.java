package com.data.smartvoice.utils;

import android.os.Environment;
import android.util.Base64;

import com.data.smartvoice.common.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import sun.misc.BASE64Decoder;

public class FileUtil {

    public static File convertStreamToFile(InputStream stream) {
        try {
            File dir = new File(Config.ROOTDIR+Config.SDKFILEDIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File convertedFile = new File(dir, Config.TEMPFILENAME + System.currentTimeMillis() + ".wav");
            LogUtils.d("Successful file and folder creation.");
            FileOutputStream out = new FileOutputStream(convertedFile);
            LogUtils.d("Success out set as output stream.");
            byte buffer[] = new byte[16384];
            int length = 0;
            while ((length = stream.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }
            LogUtils.d("Success buffer is filled.");
            out.close();
            return convertedFile;
        } catch (Exception e) {
            LogUtils.e(e.toString());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将文件转成base64 字符串
     *
     * @param path 文件路径
     * @return
     * @throws Exception
     */
    public static String encodeBase64File(String path) throws Exception {
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        file.delete();
        return Base64.encodeToString(buffer, Base64.DEFAULT);
    }

    /**
     * 将base64字符解码保存文件
     *
     * @param base64Code
     * @param targetPath
     * @throws Exception
     */
    public static void decoderBase64File(String base64Code, String targetPath) throws Exception {
        byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
        FileOutputStream out = new FileOutputStream(targetPath);
        out.write(buffer);
        out.close();
    }

    /**
     * 将base64字符保存文本文件
     *
     * @param base64Code
     * @param targetPath
     * @throws Exception
     */
    public static void toFile(String base64Code, String targetPath) throws Exception {
        byte[] buffer = base64Code.getBytes();
        FileOutputStream out = new FileOutputStream(targetPath);
        out.write(buffer);
        out.close();
    }
}
