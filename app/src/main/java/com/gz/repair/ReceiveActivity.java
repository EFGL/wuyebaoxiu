package com.gz.repair;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.gz.repair.bean.Receive;
import com.gz.repair.utils.StringUtils;
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

// 接单
public class ReceiveActivity extends BaseActivity {

    @Bind(R.id.rcy)
    RecyclerView rcy;
    @Bind(R.id.refresh)
    MaterialRefreshLayout refresh;
    @Bind(R.id.progressBar)
    CircleProgressBar progressBar;
    private MyAdapter myAdapter;
    private ArrayList<Receive.Result> allData;
    private int rootId;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
        ButterKnife.bind(this);

        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        rootId = sp.getInt("rootId", -1);
        userName = sp.getString("userName", "");
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataFromServer();
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

    public void getDataFromServer() {

        if (allData == null) {

            progressBar.setVisibility(View.VISIBLE);
            Log.e("my", "开始请求");
            String url = MyAppcation.baseUrl + "/get_repair_distribute";
            RequestParams params = new RequestParams(url);

            params.addParameter("root_id", rootId);
            params.addParameter("user_name", userName);

            x.http().post(params, new Callback.CommonCallback<String>() {


                @Override
                public void onSuccess(String result) {

                    Log.e("my", "获取已派单的报修列表onSuccess" + result);
                    try {
                        JSONObject jo = new JSONObject(result);
                        int ret = jo.getInt("ret");
//                    T.showShort(LoginActivity.this, "jo:ret=="+ret);
                        String msg = jo.getString("msg");
                        if (ret != 0) {
                            T.showShort(ReceiveActivity.this, "获取数据失败");
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Gson gson = new Gson();
                    Receive receive = gson.fromJson(result, Receive.class);
//                Log.e("my", "login.toString==" + login.toString());
                    if (receive.ret == 0) {

//                        T.showShort(ReceiveActivity.this, receive.msg);
                        allData = receive.result;
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
                    progressBar.setVisibility(View.GONE);
                }

            });
        } else {
            refreshData();
        }
    }

    private void refreshData() {
        Log.e("my", "开始请求");

        String url = MyAppcation.baseUrl + "/get_repair_distribute";
        RequestParams params = new RequestParams(url);

        params.addParameter("root_id", rootId);
        params.addParameter("user_name", userName);

        x.http().post(params, new Callback.CommonCallback<String>() {

                    @Override
                    public void onSuccess(String result) {
                        Log.e("my", "onSuccess" + result);
                        try {
                            JSONObject jo = new JSONObject(result);
                            int ret = jo.getInt("ret");
//                    T.showShort(LoginActivity.this, "jo:ret=="+ret);
                            String msg = jo.getString("msg");
                            if (ret != 0) {
                                T.showShort(ReceiveActivity.this, "获取数据失败");
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Gson gson = new Gson();
                        Receive receive = gson.fromJson(result, Receive.class);
//                Log.e("my", "login.toString==" + login.toString());
                        if (receive.ret == 0) {

//                            T.showShort(ReceiveActivity.this, receive.msg);
                            allData.clear();
                            allData.addAll(receive.result);
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

                }

        );
    }


    private class MyAdapter extends RecyclerView.Adapter<MyHolder> {


        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(ReceiveActivity.this).inflate(R.layout.item_receive, parent, false);
            MyHolder myHolder = new MyHolder(itemView);
            return myHolder;
        }

        @Override
        public void onBindViewHolder(MyHolder holder, final int position) {

            holder.mAddress.setText("地址:" + allData.get(position).address);
            holder.mInfo.setText("内容:" + allData.get(position).info);
//            holder.mTime.setText("" + StringUtils.str2Time(allData.get(position).created_at));
            holder.mTime.setText("" + StringUtils.string2Time(allData.get(position).distribute_time));
            holder.mNumber.setText("单号:" + allData.get(position).code);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(ReceiveActivity.this, ReceiverDetailActivity.class);
                    i.putExtra("apply_name", allData.get(position).apply_name);
                    i.putExtra("telephone", allData.get(position).telephone);
                    i.putExtra("address", allData.get(position).address);
                    i.putExtra("info", allData.get(position).info);
                    i.putExtra("code", allData.get(position).code);
                    i.putExtra("created_at", StringUtils.str2Time(allData.get(position).created_at));
                    i.putExtra("operator", allData.get(position).operator);

                    ReceiveActivity.this.startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return allData.size();
        }

    }


    class MyHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_receive_address)
        TextView mAddress;
        @Bind(R.id.item_receive_info)
        TextView mInfo;
        @Bind(R.id.item_receive_time)
        TextView mTime;
        @Bind(R.id.item_receive_number)
        TextView mNumber;

        public MyHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

        }
    }
}
