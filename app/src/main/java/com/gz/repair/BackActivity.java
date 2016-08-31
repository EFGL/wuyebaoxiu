package com.gz.repair;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.google.gson.Gson;
import com.gz.repair.bean.FeedBack;
import com.gz.repair.utils.StringUtils;
import com.gz.repair.utils.T;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

// 反馈
public class BackActivity extends BaseActivity {

    @Bind(R.id.rcy)
    RecyclerView rcy;
    @Bind(R.id.refresh)

    MaterialRefreshLayout refresh;
    private MyAdapter myAdapter;
    private ArrayList<FeedBack.Result> allData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back);
        ButterKnife.bind(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        if (allData == null) {


            Log.e("my", "开始请求反馈");
            String url = MyAppcation.baseUrl + "/get_repair_done";
            RequestParams params = new RequestParams(url);

            params.addParameter("root_id", MyAppcation.rootId);
            params.addParameter("user_name", MyAppcation.userName);

            x.http().post(params, new Callback.CommonCallback<String>() {


                @Override
                public void onSuccess(String result) {
                    Log.e("my", "onSuccess" + result);
                    try {
                        JSONObject jo = new JSONObject(result);
                        int ret = jo.getInt("ret");
                        if (ret != 0) {
                            T.showShort(BackActivity.this, "获取数据失败");
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Gson gson = new Gson();
                    FeedBack back = gson.fromJson(result, FeedBack.class);
                    if (back.ret == 0) {

                        allData = back.result;
                        initViews();
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
        }else{
            refreshData();
        }
    }

    private void refreshData() {
        Log.e("my", "开始请求反馈");
        String url = MyAppcation.baseUrl + "/get_repair_done";
        RequestParams params = new RequestParams(url);

        params.addParameter("root_id", MyAppcation.rootId);
        params.addParameter("user_name", MyAppcation.userName);

        x.http().post(params, new Callback.CommonCallback<String>() {


            @Override
            public void onSuccess(String result) {
                Log.e("my", "onSuccess" + result);
                try {
                    JSONObject jo = new JSONObject(result);
                    int ret = jo.getInt("ret");
                    if (ret != 0) {
                        T.showShort(BackActivity.this, "获取数据失败");
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Gson gson = new Gson();
                FeedBack back = gson.fromJson(result, FeedBack.class);
                if (back.ret == 0) {
                    allData.clear();
                    allData.addAll(back.result);
                    myAdapter.notifyDataSetChanged();
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

                refresh.finishRefresh();
            }

        });
    }

    private void initViews() {

        RecyclerView.LayoutManager lm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcy.setLayoutManager(lm);
        myAdapter = new MyAdapter();
        rcy.setAdapter(myAdapter);

        refresh.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
                //下拉刷新...

               refreshData();

            }

        });
    }


    private class MyAdapter extends RecyclerView.Adapter<MyHolder> {


        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(BackActivity.this).inflate(R.layout.item_feedback, parent, false);
            MyHolder myHolder = new MyHolder(itemView);
            return myHolder;
        }

        @Override
        public void onBindViewHolder(MyHolder holder, final int position) {

            holder.mNumber.setText("单号:" + allData.get(position).code);
            holder.mAddress.setText("地址:" + allData.get(position).address);
            holder.mInfo.setText("内容:" + allData.get(position).info);
            holder.mTime.setText(StringUtils.string2Time(allData.get(position).distribute_time));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(BackActivity.this, FeedDetailActivity.class);
                    i.putExtra("apply_name", allData.get(position).apply_name);
                    i.putExtra("telephone", allData.get(position).telephone);
                    i.putExtra("address", allData.get(position).address);
                    i.putExtra("info", allData.get(position).info);
                    i.putExtra("code", allData.get(position).code);
                    i.putExtra("created_at", StringUtils.str2Time(allData.get(position).created_at));
                    i.putExtra("operator", allData.get(position).operator);

                    BackActivity.this.startActivity(i);

//                    BackActivity.this.startActivity(new Intent(BackActivity.this, DetailActivity.class));
                }
            });
        }

        @Override
        public int getItemCount() {
            return allData.size();
        }

    }


    class MyHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.item_back_number)
        TextView mNumber;
        @Bind(R.id.item_back_address)
        TextView mAddress;
        @Bind(R.id.item_back_info)
        TextView mInfo;
        @Bind(R.id.item_back_time)
        TextView mTime;

        public MyHolder(View itemView) {
            super(itemView);// 未bind 会找不到控件 会报 空指针
            ButterKnife.bind(this, itemView);// 这句话意思是 从itemView中找子view

        }
    }
}
