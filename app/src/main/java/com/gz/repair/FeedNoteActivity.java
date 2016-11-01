package com.gz.repair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    @Bind(R.id.iv_back_img1)
    ImageView mImg1;
    @Bind(R.id.iv_back_img2)
    ImageView mImg2;
    @Bind(R.id.iv_back_img3)
    ImageView mImg3;
    @Bind(R.id.iv_back_img4)
    ImageView mImg4;
    @Bind(R.id.isMoney)
    TextView isMoney;
    private String image_1;
    private String image_2;
    private String image_3;
    private String image_4;

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
        int is_charged = intent.getIntExtra("is_charged", 0);

        image_1 = intent.getStringExtra("image_1");
        image_2 = intent.getStringExtra("image_2");
        image_3 = intent.getStringExtra("image_3");
        image_4 = intent.getStringExtra("image_4");

        if (!TextUtils.isEmpty(image_1)) {
            Log.w("ph", "image_1==" + image_1);
            showImg(mImg1, image_1);
        }
        if (!TextUtils.isEmpty(image_2)) {
            Log.w("ph", "image_2==" + image_2);
            showImg(mImg2, image_2);
        }
        if (!TextUtils.isEmpty(image_3)) {
            Log.w("ph", "image_3==" + image_3);
            showImg(mImg3, image_3);
        }
        if (!TextUtils.isEmpty(image_4)) {
            Log.w("ph", "image_4==" + image_4);
            showImg(mImg4, image_4);
        }

        if (is_charged == 1) {
            isMoney.setText("有偿服务");
        } else {
            isMoney.setText("无偿服务");

        }

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
        if (TextUtils.isEmpty(startTime)) {
            mZhi.setVisibility(View.GONE);
        }
    }

    private void showImg(ImageView imageView, String imgName) {
        String url = "http://ceshi.sx-soft.net/repair_distribute_images/" + imgName;
        imageView.setVisibility(View.VISIBLE);
        Glide.with(this).load(url).thumbnail(0.1f).placeholder(R.drawable.load).error(R.drawable.error).into(imageView);
    }

    @OnClick({R.id.iv_back_img1, R.id.iv_back_img2, R.id.iv_back_img3, R.id.iv_back_img4})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back_img1:
                if (!TextUtils.isEmpty(image_1)) {

                    showBigImg(image_1);
                }
                break;
            case R.id.iv_back_img2:
                if (!TextUtils.isEmpty(image_2)) {

                    showBigImg(image_2);
                }
                break;
            case R.id.iv_back_img3:
                if (!TextUtils.isEmpty(image_3)) {

                    showBigImg(image_3);
                }
                break;
            case R.id.iv_back_img4:
                if (!TextUtils.isEmpty(image_4)) {

                    showBigImg(image_4);
                }
                break;
        }
    }

    private void showBigImg(String path) {
        String url = "http://ceshi.sx-soft.net/repair_distribute_images/" + path;
        // 大图页面
        Intent intent = new Intent(FeedNoteActivity.this, FeedPhotoDetailActivity.class);
        intent.putExtra("path", url);
        FeedNoteActivity.this.startActivity(intent);
    }
}
