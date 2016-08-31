package com.gz.repair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gz.repair.bean.ReceiveDetail;
import com.gz.repair.utils.T;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReceiverDetailActivity extends Activity {

    @Bind(R.id.receiver_detail_name)
    TextView mName;
    @Bind(R.id.receuver_detail_phone)
    TextView mPhone;
    @Bind(R.id.receiver_deatail_address)
    TextView mAddress;
    @Bind(R.id.receiver_deatail_info)
    TextView mInfo;
    @Bind(R.id.receiver_deatail_ok)
    Button mOk;
    @Bind(R.id.progressBar)
    CircleProgressBar progressBar;
    @Bind(R.id.receiver_deatail_time)
    TextView mCreateTime;
    @Bind(R.id.receiver_deatail_sir)
    TextView mSir;
    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver_detail);
        ButterKnife.bind(this);

        Intent i = getIntent();
        String apply_name = i.getStringExtra("apply_name");
        String telephone = i.getStringExtra("telephone");
        String address = i.getStringExtra("address");
        String info = i.getStringExtra("info");
        String created_at = i.getStringExtra("created_at");
        String operator = i.getStringExtra("operator");
        code = i.getStringExtra("code");

        mName.setText(apply_name + "");
        mPhone.setText(telephone + "");
        mAddress.setText(address + "");
        mInfo.setText(info + "");
        mCreateTime.setText(created_at+"");
        mSir.setText(operator+"");
    }

    @OnClick(R.id.receiver_deatail_ok)
    public void onClick() {
        progressBar.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(code)) {
            T.showShort(this, "单号异常");
            return;
        }
        Log.e("my", "开始请求");
        String url = MyAppcation.baseUrl + "/repair_beingdone";
        RequestParams params = new RequestParams(url);

        params.addParameter("root_id", MyAppcation.rootId);
        params.addParameter("code", code);

        x.http().post(params, new Callback.CommonCallback<String>() {


            @Override
            public void onSuccess(String result) {
                Log.e("my", "接单onSuccess" + result);
                Gson gson = new Gson();
                ReceiveDetail receive = gson.fromJson(result, ReceiveDetail.class);
                T.showShort(ReceiverDetailActivity.this, receive.msg);
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
}
