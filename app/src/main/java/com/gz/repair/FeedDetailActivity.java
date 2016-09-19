package com.gz.repair;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.blankj.utilcode.utils.FileUtils;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.gz.repair.bean.FeedBackDetail;
import com.gz.repair.utils.T;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

// 反馈详情
/*
 * 反馈页面 增加图片上传功能 图片删除功能 查看详情功能
 * 实现类似微信点击选择本地图片/拍照图片
 * 实现图片详情页面 实现沉浸式状态栏
 * 实现大图手势识别 类似微信 双击放大 单击退出
 *
 */
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
    @Bind(R.id.rcy_photo)
    RecyclerView rcyPhoto;

    private String code;
    private TimePickerView pvTime;
    private TimePickerView pvTime2;
    private MyAdapter myAdapter;
    private ArrayList<String> allPhotos;

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

        initView();
    }


    private void initView() {
        allPhotos = new ArrayList<String>();
        allPhotos.add("");
        RecyclerView.LayoutManager lm = new GridLayoutManager(this, 4);
        rcyPhoto.setLayoutManager(lm);
        myAdapter = new MyAdapter();
        rcyPhoto.setAdapter(myAdapter);

    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemview = LayoutInflater.from(FeedDetailActivity.this).inflate(R.layout.item_feeddetail_photo, parent, false);

            MyViewHolder myViewHolder = new MyViewHolder(itemview);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {


            if (position == allPhotos.size() - 1) {

                Glide.with(FeedDetailActivity.this).load(R.drawable.ic_add).into(holder.photo);
                holder.detele.setVisibility(View.GONE);
            } else {
                Glide.with(FeedDetailActivity.this).load(allPhotos.get(position)).thumbnail(0.1f).into(holder.photo);
                holder.detele.setVisibility(View.VISIBLE);
            }

            holder.detele.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    allPhotos.remove(position);
                    notifyDataSetChanged();
                }
            });
            //
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position == allPhotos.size() - 1) {
                        if (allPhotos.size() < 5) {

                            MultiImageSelector.create().count(4).start(FeedDetailActivity.this, 0);
                        } else {
                            T.showShort(FeedDetailActivity.this, "已达最大数量");
                        }
                    } else {
                        // 大图页面
                        Intent intent = new Intent(FeedDetailActivity.this, FeedPhotoDetailActivity.class);
                        intent.putExtra("path", allPhotos.get(position));
                        FeedDetailActivity.this.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            Log.w("my", "allPhotos.size()==" + allPhotos.size());
            return allPhotos.size();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                // 获取返回的图片列表
                List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                // 处理你自己的逻辑 ....

                Log.e("FeedDetailActivity", "new path ==" + allPhotos.toString());
                if (path.size() + allPhotos.size() <= 5) {

                    allPhotos.addAll(0, path);
                } else {
                    //              2       +   4 -5 =1
                    int offset = 5 - allPhotos.size();
                    for (int i = 0; i < offset; i++) {
                        allPhotos.add(0, path.get(i));
                    }

                }
                Log.e("FeedDetailActivity", "old path =="+path.toString());
                compressImage();//压缩图片 替换路径
                myAdapter.notifyDataSetChanged();


            }
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.photo)
        ImageView photo;

        @Bind(R.id.detele)
        ImageView detele;

        public MyViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    @OnClick(R.id.feed_deatail_ok)
    public void onClick() {
        String des = mDes.getText().toString().trim();
        if (TextUtils.isEmpty(des)) {
            T.showShort(this, "请输入维修描述");
            return;
        }
        String startTime = mStarttime.getText().toString().trim();
        if (startTime.equals("起始时间")) {
            T.showShort(this, "请选择起始时间");
            return;
        }
        String endTime = mEndtime.getText().toString().trim();
        if (endTime.equals("结束时间")) {
            T.showShort(this, "请选择结束时间");
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

        if (allPhotos.size() > 1) {
            for (int i = 0; i < allPhotos.size() - 1; i++) {

                if (i == 0) {

                    params.addParameter("image_1", FileUtils.getFileName(allPhotos.get(i)));
                    Log.e("反馈", "file_name==" + FileUtils.getFileName(allPhotos.get(i)));
                } else if (i == 1) {
                    params.addParameter("image_2", FileUtils.getFileName(allPhotos.get(i)));
                    Log.e("反馈", "file_name==" + FileUtils.getFileName(allPhotos.get(i)));
                } else if (i == 2) {
                    params.addParameter("image_3", FileUtils.getFileName(allPhotos.get(i)));
                    Log.e("反馈", "file_name==" + FileUtils.getFileName(allPhotos.get(i)));
                } else if (i == 3) {
                    params.addParameter("image_4", FileUtils.getFileName(allPhotos.get(i)));
                    Log.e("反馈", "file_name==" + FileUtils.getFileName(allPhotos.get(i)));
                }

            }
        }

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
                if (fbd.ret == 0 && fbd.msg.equals("反馈成功")) {

                    image();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("my", "onError" + ex.toString());
                T.showShort(FeedDetailActivity.this, ex.toString());
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

    // 上传图片
    public void image() {
//        progressBar.setVisibility(View.VISIBLE);


        if (allPhotos.size() > 1) {
            T.showShort(FeedDetailActivity.this, "正在上传图片...");

            for (int i = 0; i < allPhotos.size() - 1; i++) {

                subImg(i);
            }
        }
//        progressBar.setVisibility(View.GONE);
    }

    // 测试方法
    public void subimg(View v) {
        String url = MyAppcation.baseUrl + "/repair_distribute_image_upload";
        for ( int i = 0; i < allPhotos.size() - 1; i++) {


            RequestParams params = new RequestParams(url);
            params.addQueryStringParameter("file_name", FileUtils.getFileName(allPhotos.get(i)));
            params.setMultipart(true);
            params.addBodyParameter("file", new File(allPhotos.get(i)));
//        params.addBodyParameter("file", PhotoUtil.scal(allPhotos.get(0)));


            x.http().post(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    Log.w("phone", "onSuccess:" + result);

                    try {
                        JSONObject jo = new JSONObject(result);
                        String msg = jo.getString("msg");

                        T.showShort(FeedDetailActivity.this, msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Log.w("phone", "onError:" + ex.toString());
//                T.showLong(FeedDetailActivity.this, "onError:" + ex.toString());
                }

                @Override
                public void onCancelled(CancelledException cex) {
                    Log.w("phone", "onCancelled:" + cex.toString());
                }

                @Override
                public void onFinished() {
                    Log.w("phone", "onFinished:");
                }
            });
        }
    }

    private void subImg(final int i) {

        String url = MyAppcation.baseUrl + "/repair_distribute_image_upload";

        RequestParams params = new RequestParams(url);

        // 字符串参数用 addQueryStringParameter 方法  否则在设置表单上传后addBodyParameter/addParameter方法传都是文件形式
        params.addQueryStringParameter("file_name", FileUtils.getFileName(allPhotos.get(i)));
        Log.e("subImage","file_name=="+FileUtils.getFileName(allPhotos.get(i)));
        // 使用multipart表单上传文件
        params.setMultipart(true);
        params.addBodyParameter("file", new File(allPhotos.get(i)));

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.w("phone", "onSuccess:" + result);

                try {
                    JSONObject jo = new JSONObject(result);
                    String msg = jo.getString("msg");
                    T.showShort(FeedDetailActivity.this, "第" + (i + 1) + "张" + msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.w("phone", "onError:" + ex.toString());
//                T.showLong(FeedDetailActivity.this, "onError:" + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.w("phone", "onCancelled:" + cex.toString());
            }

            @Override
            public void onFinished() {
                Log.w("phone", "onFinished:");
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
            if (pvTime.isShowing()) {
                pvTime.dismiss();
                return true;
            }
            if (pvTime2.isShowing()) {
                pvTime2.dismiss();
                return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    // 压缩图片 = 替换allphoto 集合
    public void compressImage() {

        if (allPhotos == null || allPhotos.size() < 2) {
            return;
        } else {
            FileOutputStream fos = null;
            Bitmap src = null;
            for (int i = 0; i < allPhotos.size() - 1; i++) {
                try {
                    src = BitmapFactory.decodeFile(allPhotos.get(i));// 获取原图的位图对象
//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String timeStamp = "JPEG_" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                    File dirFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);// 得到picture文件夹目录
                    File tempFile = File.createTempFile(timeStamp, ".jpg", dirFile);// 在得到picture目录下创建 tempFile 文件

                    String strPath = Uri.fromFile(tempFile).getPath();// 图片路径

                    fos = new FileOutputStream(new File(strPath));// 根据图片路径得到输出流

                    boolean cp = src.compress(Bitmap.CompressFormat.JPEG, 30, fos);// 压缩70%

                    if (cp) {
                        allPhotos.remove(i);
                        allPhotos.add(i, strPath);
                    }

                } catch (IOException e) {
                    e.printStackTrace();

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

    }

}
