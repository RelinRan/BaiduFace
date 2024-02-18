package com.baidu.idl.face.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FaceImage {

    private final String TAG = FaceImage.class.getSimpleName();

    /**
     * 人脸文件夹
     *
     * @param project 项目名称
     * @return
     */
    private File getFaceDir(String project) {
        File projectDir = new File(Environment.getExternalStorageDirectory(), project);
        if (!projectDir.exists()) {
            projectDir.mkdirs();
        }
        File dir = new File(projectDir, "Face");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 保存人脸文件
     *
     * @param project  项目文件名称
     * @param filename 文件名称，xxx.jpg
     * @param bitmap   位图
     * @return
     */
    public File save(String project, String filename, Bitmap bitmap) {
        File file = new File(getFaceDir(project), filename);
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            Log.i(TAG, "crop file:" + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * Bitmap转byte[]
     *
     * @param bitmap
     * @return
     */
    public static byte[] toBytes(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        return bos.toByteArray();
    }

    /**
     * 转16进制字符串
     *
     * @param bitmap
     * @return
     */
    public static String toHex(Bitmap bitmap) {
        return Hex.toHex(FaceImage.toBytes(bitmap));
    }

    /**
     * 16进制字符串转Bitmap
     *
     * @param hexString
     * @return
     */
    public static Bitmap decodeHex(String hexString) {
        byte[] data = Hex.toBytes(hexString);
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    /**
     * byte[]转Bitmap
     *
     * @param data
     * @return
     */
    public static Bitmap decodeByteArray(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

}
