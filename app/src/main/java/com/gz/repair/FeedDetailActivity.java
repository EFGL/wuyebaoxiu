package com.gz.repair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.google.gson.Gson;
import com.gz.repair.bean.FeedBackDetail;
import com.gz.repair.utils.T;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeedDetailActivity extends Activity {

    @Bind(R.id.progressBar)
    CircleProgressBar progressBar;
    @Bind(R.id.feed_detail_name)
    TextView mName;
    @Bind(R.id.feed_detail_phone)
    TextView mPhone;
    @Bind(R.id.feed_deatail_address)
    TextView mAddress;
    @Bind(R.id.feed_deatail_info)
    TextView mInfo;
    @Bind(R.id.feed_deatail_des)
    EditText mDes;
    @Bind(R.id.feed_deatail_starttime)
    TextView mStarttime;
    @Bind(R.id.feed_deatail_endtime)
    TextView mEndtime;

    private String code;
    private TimePickerView pvTime;
    private TimePickerView pvTime2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_detail);
        ButterKnife.bind(this);

        Intent i = getIntent();
        String apply_name = i.getStringExtra("apply_name");
        String telephone = i.getStringExtra("telephone");
        String address = i.getStringExtra("address");
        String info = i.getStringExtra("info");
        code = i.getStringExtra("code");

        mName.setText(apply_name + "");
        mPhone.setText(telephone + "");
        mAddress.setText(address + "");
        mInfo.setText(info + "");


        pvTime = new TimePickerView(this, TimePickerView.Type.ALL);
        pvTime2 = new TimePickerView(this, TimePickerView.Type.ALL);
        pvTime.setTime(new Date());
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);
        pvTime2.setTime(new Date());
        pvTime2.setCyclic(false);
        pvTime2.setCancelable(true);
        //时间选择后回调
        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

            @Override
            public void onTimeSelect(Date date) {
                mStarttime.setText(getTime(date));
            }
        });
        //弹出时间选择器
        mStarttime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pvTime.show();
            }
        });
        pvTime2.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

            @Override
            public void onTimeSelect(Date date) {
                mEndtime.setText(getTime(date));
            }
        });
        //弹出时间选择器
        mEndtime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pvTime2.show();
            }
        });
    }

    @OnClick(R.id.feed_deatail_ok)
    public void onClick() {
        String des = mDes.getText().toString().trim();
        if (TextUtils.isEmpty(des)){
            T.showShort(this,"请输入维修描述");
            return;
        }
        String startTime = mStarttime.getText().toString().trim();
        if (startTime.equals("起始时间")){
            T.showShort(this,"请选择起始时间");
            return;
        }
        String endTime = mEndtime.getText().toString().trim();
        if (endTime.equals("结束时间")){
            T.showShort(this,"请选择结束时间");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(code)) {
            T.showShort(this, "单号异常");
            return;
        }
        Log.e("my", "开始请求");
        String url = MyAppcation.baseUrl + "/repair_feedback";
        RequestParams params = new RequestParams(url);

        params.addParameter("user_name", MyAppcation.userName);
        params.addParameter("begin_at", startTime);
        params.addParameter("end_at", endTime);
        params.addParameter("result", des);
        params.addParameter("code", code);
        params.addParameter("root_id", MyAppcation.rootId);

//        Log.e("user_name", MyAppcation.userName);
//        Log.e("begin_at", startTime);
//        Log.e("end_at", endTime);
//        Log.e("result", des);

        x.http().post(params, new Callback.CommonCallback<String>() {


            @Override
            public void onSuccess(String result) {
                Log.e("my", "反馈onSuccess" + result);
                Gson gson = new Gson();
                FeedBackDetail fbd = gson.fromJson(result, FeedBackDetail.class);
                T.showShort(FeedDetailActivity.this, fbd.msg);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("my", "onError" + ex.toString());
                T.showShort(FeedDetailActivity.this,ex.toString());
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

    @OnClick({R.id.feed_deatail_starttime, R.id.feed_deatail_endtime})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.feed_deatail_starttime:
                break;
            case R.id.feed_deatail_endtime:
                break;
        }
    }

    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(date);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(pvTime.isShowing()){
                pvTime.dismiss();
                return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
}
