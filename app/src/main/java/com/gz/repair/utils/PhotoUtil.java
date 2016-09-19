package com.gz.repair.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Endeavor on 2016/9/18.
 */
public class PhotoUtil {
    public static File scal(String path) {
//        String path = fileUri.getPath();
        File outputFile = new File(path);
        long fileSize = outputFile.length();// 图片总大小 (字节)
        final long fileMaxSize = 200 * 1024;// 允许最大值 200kb
        if (fileSize >= fileMaxSize) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;// 测量模式(不会把图片读入内存, 只获取图片宽高信息)
            BitmapFactory.decodeFile(path, options);
            int height = options.outHeight;
            int width = options.outWidth;
//
            // 5000 / 200 = 25 =5
            double scale = Math.sqrt((float) fileSize / fileMaxSize);// Math.sqrt 计算平方根  eg:25-->5
            options.outHeight = (int) (height / scale);
            options.outWidth = (int) (width / scale);
            options.inSampleSize = (int) (scale + 0.5);
            options.inJustDecodeBounds = false;

            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            outputFile = new File(PhotoUtil.createImageFile().getPath());
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(outputFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
                fos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            } else {
                File tempFile = outputFile;
                outputFile = new File(PhotoUtil.createImageFile().getPath());
                PhotoUtil.copyFileUsingFileChannels(tempFile, outputFile);
            }

        }
        return outputFile;

    }

    public static Uri createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Save a file: path for use with ACTION_VIEW intents
        return Uri.fromFile(image);
    }

    public static void copyFileUsingFileChannels(File source, File dest) {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            try {
                inputChannel = new FileInputStream(source).getChannel();
                outputChannel = new FileOutputStream(dest).getChannel();
                outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } finally {
            try {
                inputChannel.close();
                outputChannel.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public boolean compressImage(String path) {

//        Log.w("压缩:", "原文件大小:" + new File(path).length() / 1024 / 1024);
        FileOutputStream fos = null;
        Bitmap src = null;
        try {
            src = BitmapFactory.decodeFile(path);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timeStamp = "JPEG_"+sdf.format(new Date());
            File dirFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);// 得到picture文件夹目录
            File tempFile = File.createTempFile(timeStamp, ".jpg", dirFile);// 在得到picture目录下创建 tempFile 文件

            String strPath = Uri.fromFile(tempFile).getPath();// 图片路径

            fos = new FileOutputStream(new File(strPath));
            boolean cp = src.compress(Bitmap.CompressFormat.JPEG, 30, fos);// 压缩70%



//            Log.w("压缩:", "压缩后文件大小:" + fos.toString().length() / 1024 / 1024);
            return cp;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (!src.isRecycled()) {

                    src.recycle();
                }
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
