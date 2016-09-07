package com.gz.repair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import butterknife.Bind;
import butterknife.ButterKnife;

// 反馈记录详情
public class FeedNoteActivity extends Activity {

    @Bind(R.id.progressBar)
    CircleProgressBar progressBar;
    @Bind(R.id.maintainer)
    TextView mMaintainer;
    @Bind(R.id.repair_item)
    TextView mRepairItem;
    @Bind(R.id.begin_at)
    TextView mBeginAt;
    @Bind(R.id.end_at)
    TextView mEndAt;
    @Bind(R.id.code)
    TextView mCode;
    @Bind(R.id.result)
    TextView mResult;
    @Bind(R.id.apply_name)
    TextView mApplyName;
    @Bind(R.id.create_at)
    TextView mCreateAt;
    @Bind(R.id.phone)
    TextView mPhone;
    @Bind(R.id.address)
    TextView mAddress;
    @Bind(R.id.info)
    TextView mInfo;
    @Bind(R.id.tv_zhi)
    TextView mZhi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_note);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String maintainer = intent.getStringExtra("maintainer");
        String repair_item = intent.getStringExtra("repair_item");
        String begin_at = intent.getStringExtra("begin_at");
        String end_at = intent.getStringExtra("end_at");
        String code = intent.getStringExtra("code");
        String result = intent.getStringExtra("result");
        String apply_name = intent.getStringExtra("apply_name");
        String created_at = intent.getStringExtra("created_at");
        String telephone = intent.getStringExtra("telephone");
        String address = intent.getStringExtra("address");
        String info = intent.getStringExtra("info");

        mMaintainer.setText(maintainer);
        mRepairItem.setText(repair_item);
        mBeginAt.setText(begin_at);
        mEndAt.setText(end_at);
        mCode.setText(code);
        mResult.setText(result);
        mApplyName.setText(apply_name);
        mCreateAt.setText(created_at);
        mPhone.setText(telephone);
        mAddress.setText(address);
        mInfo.setText(info);

        String startTime = mBeginAt.getText().toString().trim();
        if (TextUtils.isEmpty(startTime)){
            mZhi.setVisibility(View.GONE);
        }
    }
}
