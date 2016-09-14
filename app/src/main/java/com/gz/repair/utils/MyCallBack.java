package com.gz.repair.utils;

import android.util.Log;

import org.xutils.common.Callback;

/**
 * Created by Endeavor on 2016/9/13.
 */
public class MyCallBack<ResultType> implements Callback.CommonCallback<ResultType>{

    @Override
    public void onSuccess(ResultType result) {
        //可以根据公司的需求进行统一的请求成功的逻辑处理
    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {
        //可以根据公司的需求进行统一的请求网络失败的逻辑处理
    }

    @Override
    public void onCancelled(CancelledException cex) {
        Log.e("my", "onCancelled");
    }

    @Override
    public void onFinished() {
        Log.e("my", "onFinished");
//        InputStream inputStream = new FileInputStream(new File(mImgCapturePath));
//        byte[] bytes = readInputStream(inputStream);
//        String string = com.loopj.android.http.Base64.encodeToString(bytes, com.loopj.android.http.Base64.DEFAULT);
//        ChoosePhotoInfo info = new ChoosePhotoInfo();
//        info.setPath(string);
//        info.setUrl(et_receiver_phone.getText().toString().trim()+mImgCapturePath.substring(mImgCapturePath.lastIndexOf("/")+1));
//        info.setBendipath(mImgCapturePath);
//        info.setFilepath("file://" + mImgCapturePath);
//        imgsinfo.add(info);
    }


//    private static byte[] readInputStream(InputStream inputStream)throws IOException {
//        byte[] buffer = new byte[4096];
//        int len = 0;
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        while ((len = inputStream.read(buffer)) != -1) {
//            bos.write(buffer, 0, len);
//        }
//        bos.close();
//        return bos.toByteArray();
//    }

}
