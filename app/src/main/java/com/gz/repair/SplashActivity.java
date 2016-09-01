package com.gz.repair;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.gz.repair.bean.Login;
import com.gz.repair.utils.T;
import com.igexin.sdk.PushManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        PushManager.getInstance().initialize(this.getApplicationContext());
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        final String userName = sp.getString("userName", "");
        final String password = sp.getString("password", "");
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(2000);
                    if (userName.equals("")||password.equals("")){

                        SplashActivity.this.startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                    }else{

//                        SplashActivity.this.startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        autoLogin(userName,password);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void autoLogin(String userName ,String password) {

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
                        T.showShort(SplashActivity.this,"自动登录验证失败");
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
