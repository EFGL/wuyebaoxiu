package com.gz.repair;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.gz.repair.bean.Login;
import com.igexin.sdk.PushManager;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @Bind(R.id.t1)
    TextView t1;
    @Bind(R.id.t2)
    TextView t2;
    @Bind(R.id.t3)
    TextView t3;
    @Bind(R.id.t4)
    TextView t4;
    @Bind(R.id.t5)
    TextView t5;
    @Bind(R.id.c1)
    CardView c1;
    @Bind(R.id.c2)
    CardView c2;
    @Bind(R.id.c3)
    CardView c3;
    @Bind(R.id.c4)
    CardView c4;
    @Bind(R.id.c5)
    CardView c5;
    @Bind(R.id.progressBar)
    CircleProgressBar progressBar;
    @Bind(R.id.tv_user)
    TextView tvUser;
    @Bind(R.id.t6)
    TextView t6;
    @Bind(R.id.c6)
    CardView c6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        tvUser.setText(MyAppcation.userName);

    }

    @Override
    protected void onResume() {
        PushManager.getInstance().initialize(this.getApplicationContext());
        super.onResume();
        List<Login.Result.Privileges> pro = MyAppcation.pro;
        List<String> ids = new ArrayList<String>();

        for (int i = 0; i < pro.size(); i++) {
            Login.Result.Privileges privileges = pro.get(i);
            String privilege_id = privileges.privilege_id;
            ids.add(privilege_id);
        }
        if (ids.contains("95")) {
            c1.setVisibility(View.VISIBLE);
        }
        if (ids.contains("99")) {
            c2.setVisibility(View.VISIBLE);

        }
        if (ids.contains("386")) {
            c3.setVisibility(View.VISIBLE);

        }
        if (ids.contains("97")) {
            c4.setVisibility(View.VISIBLE);

        }
        if (ids.contains("96")) {
            c6.setVisibility(View.VISIBLE);

        }

    }

    @OnClick({R.id.t1, R.id.t2, R.id.t3, R.id.t4, R.id.t5, R.id.t6})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.t1:
                break;
            case R.id.t2:
                progressBar.setVisibility(View.VISIBLE);
                startActivity(new Intent(this, ProjectActivity.class));
                break;
            case R.id.t3:
                progressBar.setVisibility(View.VISIBLE);
                startActivity(new Intent(this, ReceiveActivity.class));
                break;
            case R.id.t4:
                progressBar.setVisibility(View.VISIBLE);
                startActivity(new Intent(this, BackActivity.class));
                break;
            case R.id.t5:
                progressBar.setVisibility(View.VISIBLE);
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.t6:
                progressBar.setVisibility(View.VISIBLE);
                startActivity(new Intent(this, FeedbackActivity.class));
                break;

        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        progressBar.setVisibility(View.GONE);
    }

    // 返回键
    @Override
    public void onBackPressed() {
        final NormalDialog dialog = new NormalDialog(this);
        dialog.content("亲,真的要走吗?再看会儿吧~")//
                .style(NormalDialog.STYLE_TWO)//
                .title("温馨提示")
                .titleTextSize(23)//
                .btnText("继续看看", "残忍退出")//
//                .contentTextColor(Color.parseColor("#0097A7"))
                .titleTextColor(Color.parseColor("#006064"))
                .btnTextColor(Color.parseColor("#00796B"), Color.parseColor("#D4D4D4"))//
                .btnTextSize(16f, 16f)//
                .showAnim(mBasIn)//
                .dismissAnim(mBasOut)//
                .show();

        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                },
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.superDismiss();
                        PushManager.getInstance().initialize(MainActivity.this.getApplicationContext());
                        // 最小化
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        startActivity(intent);

//                        finish();
                    }
                });
    }

}
