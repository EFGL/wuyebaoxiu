package com.gz.repair;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.gz.repair.bean.Login;
import com.gz.repair.bean.UpdataBean;
import com.gz.repair.utils.T;
import com.igexin.sdk.PushManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;

public class SplashActivity extends BaseActivity {

    private String userName;
    private String password;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        PushManager.getInstance().initialize(this.getApplicationContext());
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        userName = sp.getString("userName", "");
        password = sp.getString("password", "");
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(300);

                    checkUpdate();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    public int getVersionCode() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            int versionCode = packageInfo.versionCode;
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void checkUpdate() {
        Log.e("my", "开始请求");
        String url = MyAppcation.baseUrl + "/app_update";
        RequestParams params = new RequestParams(url);

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Log.e("my", "onSuccess" + result);

                Gson gson = new Gson();
                UpdataBean up = gson.fromJson(result, UpdataBean.class);
                if (up.ret == 0) {
                    Log.e("my", "up.version=="+up.versionCode);
                    Log.e("my", "getVersionCode()=="+getVersionCode());
                    if (up.versionCode > getVersionCode()) {
                        // 有更新
                        showUpDialog(up.msg, up.versionCode, up.downlode);
                    }else{

                        startapp();
                    }

                } else {
                    startapp();
                }
            }


            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("my", "onError" + ex.toString());
                T.showShort(SplashActivity.this, "网络错误");
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e("my", "onCancelled" + cex.toString());
            }

            @Override
            public void onFinished() {
                Log.e("my", "onFinished");

            }

        });
    }

    private void showUpDialog(String msg, double versionCode, final String address) {
        builder = new AlertDialog.Builder(this);
        builder.setTitle("最新版本:" + versionCode)

                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        downloadApp(address);

                    }
                })
                .setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startapp();
                    }
                })
                .show();
    }


    private void downloadApp(String address) {
        Log.e("my", "address=="+address);
        RequestParams params = new RequestParams(address);
        //自定义保存路径，Environment.getExternalStorageDirectory()：SD卡的根目录
        params.setSaveFilePath(Environment.getExternalStorageDirectory() + "/myapp/repair.apk");
        //自动为文件命名
        params.setAutoRename(true);

        x.http().get(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onSuccess(File result) {
                //apk下载完成后，调用系统的安装方法\
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(result), "application/vnd.android.package-archive");
                SplashActivity.this.startActivityForResult(intent, 38);


            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("my", "下载错误" + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                Log.e("my", "下载onFinished");

            }

            //网络请求之前回调
            @Override
            public void onWaiting() {
            }

            //网络请求开始的时候回调
            @Override
            public void onStarted() {
                Log.e("my", "开始下载");
            }

            //下载的时候不断回调的方法
            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                //当前进度和文件总大小
                Log.e("my", "下载中...");
                Log.i("JAVA", "current：" + current + "，total：" + total);
                T.showShort(SplashActivity.this,"正在下载,请等待...");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.e("my","onActivityResult调用了..................");
        if(requestCode==38){
            startapp();
        }
    }

    private void startapp() {
        if (userName.equals("") || password.equals("")) {

            SplashActivity.this.startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        } else {

//                        SplashActivity.this.startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            autoLogin(userName, password);
        }
    }


    private void autoLogin(String userName, String password) {

        Log.e("my", "开始请求");
        String url = MyAppcation.baseUrl + "/repair_login";
        RequestParams params = new RequestParams(url);

        params.addBodyParameter("login", userName);
        params.addBodyParameter("password", password);
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Log.e("my", "onSuccess" + result);
                try {
                    JSONObject jo = new JSONObject(result);
                    int ret = jo.getInt("ret");
                    if (ret == 1) {
                        T.showShort(SplashActivity.this, "自动登录验证失败");
                        SplashActivity.this.startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Gson gson = new Gson();
                Login login = gson.fromJson(result, Login.class);
//                Log.e("my", "login.toString==" + login.toString());
                if (login.ret == 0) {


                    T.showShort(SplashActivity.this, login.msg);
                    ArrayList<Login.Result.Privileges> privileges = login.result.privileges;
                    MyAppcation.pro.clear();
                    MyAppcation.pro.addAll(privileges);
                    MyAppcation.userId = login.result.user_id;
                    MyAppcation.rootId = login.result.root_id;
                    MyAppcation.userName = login.result.user_name;

                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }

            }


            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("my", "onError" + ex.toString());
                T.showShort(SplashActivity.this,"网络错误");
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e("my", "onCancelled" + cex.toString());
            }

            @Override
            public void onFinished() {
                Log.e("my", "onFinished");
            }

        });
    }

}
