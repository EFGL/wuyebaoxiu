package com.gz.repair;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.google.gson.Gson;
import com.gz.repair.bean.Login;
import com.gz.repair.utils.T;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {

    @Bind(R.id.username)
    EditText mUser;
    @Bind(R.id.password)
    EditText pwd;
    @Bind(R.id.sign_in_button)
    Button sign_in;
    @Bind(R.id.progressBar)
    CircleProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

    }


    @OnClick(R.id.sign_in_button)
    public void onClick() {
        attemptLogin();
    }

    private void attemptLogin() {
        // Store values at the time of the login attempt.
        String user = mUser.getText().toString().trim();
        String password = pwd.getText().toString().trim();

        if (TextUtils.isEmpty(user)) {
            mUser.setError("用户名不能为空");

        }

        if (TextUtils.isEmpty(password)) {
            pwd.setError("密码不能为空");
            return;
        }

        login(user, password);

//        showDialog();

    }

    private void login(String user, String password) {
        Log.e("my", "开始请求");
        progressBar.setVisibility(View.VISIBLE);
        String url = MyAppcation.baseUrl + "/repair_login";
        RequestParams params = new RequestParams(url);

        params.addBodyParameter("login", user);
        params.addBodyParameter("password", password);
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Log.e("my", "onSuccess" + result);
                try {
                    JSONObject jo = new JSONObject(result);
                    int ret = jo.getInt("ret");
//                    T.showShort(LoginActivity.this, "jo:ret=="+ret);
                    String msg = jo.getString("msg");
//                    T.showShort(LoginActivity.this, "jo:msg=="+msg);
                    if (ret == 1) {
                        showDialog();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Gson gson = new Gson();
                Login login = gson.fromJson(result, Login.class);
//                Log.e("my", "login.toString==" + login.toString());
                if (login.ret == 0) {

                    T.showShort(LoginActivity.this, login.msg);
                    ArrayList<Login.Result.Privileges> privileges = login.result.privileges;
                    MyAppcation.pro.clear();
                    MyAppcation.pro.addAll(privileges);
                    MyAppcation.userId = login.result.user_id;
                    MyAppcation.rootId = login.result.root_id;
                    MyAppcation.userName = login.result.user_name;
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
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
                progressBar.setVisibility(View.GONE);
            }

        });

    }

    private void showDialog() {
        final MaterialDialog dialog = new MaterialDialog(this);
        dialog.content(
                "用户名或密码错误，请重新输入。")
                .btnNum(1)
                .btnText("确定")//
                .title("提示")
//                .contentTextColor(Color.parseColor("#0097A7"))
                .titleTextColor(Color.parseColor("#006064"))
                .showAnim(mBasIn)//
                .dismissAnim(mBasOut)//
                .show();

        dialog.setOnBtnClickL(
                new OnBtnClickL() {//left btn click listener
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                }
        );
    }
}

