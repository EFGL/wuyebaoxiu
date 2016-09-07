package com.gz.repair;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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
import com.gz.repair.bean.FeedNote;
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

public class FeedbackActivity extends Activity {

    @Bind(R.id.rcy)
    RecyclerView rcy;
    @Bind(R.id.refresh)
    MaterialRefreshLayout refresh;
    private ArrayList<FeedNote.Result> allData;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        InitData();
    }

    private void InitData() {
        if (allData == null) {
            Log.e("my", "开始请求");
            String url = MyAppcation.baseUrl + "/get_repair_feedback";
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
                            T.showShort(FeedbackActivity.this, "获取数据失败");
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Gson gson = new Gson();
                    FeedNote feedNote = gson.fromJson(result, FeedNote.class);
                    if (feedNote.ret == 0) {

                        allData = feedNote.result;
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
        } else {
            refreshData();
        }
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

    private void refreshData() {
        Log.e("my", "开始请求");
        String url = MyAppcation.baseUrl + "/get_repair_feedback";
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
                        T.showShort(FeedbackActivity.this, "获取数据失败");
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Gson gson = new Gson();
                FeedNote feedNote = gson.fromJson(result, FeedNote.class);
//                Log.e("my", "login.toString==" + login.toString());
                if (feedNote.ret == 0) {

//                    T.showShort(ProjectActivity.this, project.msg);
                    allData.clear();
                    allData.addAll(feedNote.result);
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

    private class MyAdapter extends RecyclerView.Adapter<MyHolder> {


        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View view = View.inflate(getActivity(), R.layout.search_car_info, false);
            View itemView = LayoutInflater.from(FeedbackActivity.this).inflate(R.layout.item_feednote, parent, false);
            MyHolder myHolder = new MyHolder(itemView);
            return myHolder;
        }

        @Override
        public void onBindViewHolder(final MyHolder holder, final int position) {
            holder.mAddress.setText("地址:" + allData.get(position).address);
            String state = allData.get(position).status;
            if (state.equals("已反馈")) {
                holder.mState.setTextColor(Color.GREEN);
            } else if (state.equals("处理中")) {
                holder.mState.setTextColor(Color.RED);

            }
            holder.mState.setText(allData.get(position).status);
            holder.mInfo.setText("内容:" + allData.get(position).info);
            holder.mTime.setText("" + StringUtils.str2Time(allData.get(position).created_at));
            holder.mCode.setText("单号:" + allData.get(position).code);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String state = holder.mState.getText().toString().trim();

                        Intent i = new Intent(FeedbackActivity.this, FeedNoteActivity.class);
                        i.putExtra("maintainer", allData.get(position).maintainer);
                        i.putExtra("repair_item", allData.get(position).repair_item);
                        i.putExtra("begin_at", StringUtils.string2Time(allData.get(position).begin_at));
                        i.putExtra("end_at", StringUtils.string2Time(allData.get(position).end_at));
                        i.putExtra("code", allData.get(position).code);
                        i.putExtra("result", allData.get(position).result);

                        i.putExtra("apply_name", allData.get(position).apply_name);
                        i.putExtra("created_at", StringUtils.str2Time(allData.get(position).created_at));
                        i.putExtra("telephone", allData.get(position).telephone);
                        i.putExtra("address", allData.get(position).address);
                        i.putExtra("info", allData.get(position).info);
                        FeedbackActivity.this.startActivity(i);


                }
            });
        }

        @Override
        public int getItemCount() {
            return allData.size();
        }

    }


    class MyHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_note_state)
        TextView mState;
        @Bind(R.id.item_note_code)
        TextView mCode;
        @Bind(R.id.item_note_address)
        TextView mAddress;
        @Bind(R.id.item_note_info)
        TextView mInfo;
        @Bind(R.id.item_note_time)
        TextView mTime;

        public MyHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
