package com.gz.repair;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.gz.repair.bean.Project;
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

// 派工
public class ProjectActivity extends BaseActivity {

    @Bind(R.id.rcy)
    RecyclerView rcy;
    @Bind(R.id.refresh)
    MaterialRefreshLayout refresh;
    @Bind(R.id.progressBar)
    CircleProgressBar progressBar;
    private MyAdapter myAdapter;
    private ArrayList<Project.Result> alldata;
    private int rootId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        ButterKnife.bind(this);

//        sry.setOnRefreshListener(this);
//        sry.setColorSchemeColors(Color.parseColor("#00BCD4"));
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        rootId = sp.getInt("rootId", -1);

    }

    @Override
    protected void onResume() {
        super.onResume();
        InitData();
    }

    private void InitData() {
        if (alldata == null) {
            progressBar.setVisibility(View.VISIBLE);
            Log.e("my", "开始请求");
            String url = MyAppcation.baseUrl + "/get_repair_apply_tobedone";
            RequestParams params = new RequestParams(url);
            params.addParameter("root_id", rootId);
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
                            T.showShort(ProjectActivity.this, "获取数据失败");
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Gson gson = new Gson();
                    Project project = gson.fromJson(result, Project.class);
//                Log.e("my", "login.toString==" + login.toString());
                    if (project.ret == 0) {

//                        T.showShort(ProjectActivity.this, project.msg);
                        alldata = project.result;
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
                    progressBar.setVisibility(View.GONE);
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
        String url = MyAppcation.baseUrl + "/get_repair_apply_tobedone";
        RequestParams params = new RequestParams(url);

        params.addParameter("root_id", rootId);
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
                        T.showShort(ProjectActivity.this, "获取数据失败");
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Gson gson = new Gson();
                Project project = gson.fromJson(result, Project.class);
//                Log.e("my", "login.toString==" + login.toString());
                if (project.ret == 0) {

//                    T.showShort(ProjectActivity.this, project.msg);
                    alldata.clear();
                    alldata.addAll(project.result);
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
            View itemView = LayoutInflater.from(ProjectActivity.this).inflate(R.layout.item_project, parent, false);
            MyHolder myHolder = new MyHolder(itemView);
            return myHolder;
        }

        @Override
        public void onBindViewHolder(MyHolder holder, final int position) {

            holder.mAddress.setText("地址:" + alldata.get(position).address);
            String state = alldata.get(position).status;
            if (state.equals("待处理")) {
                holder.mState.setTextColor(Color.RED);
            } else if (state.equals("已派单")) {
                holder.mState.setTextColor(Color.GREEN);

            }
            holder.mState.setText(alldata.get(position).status);
            holder.mInfo.setText("内容:" + alldata.get(position).info);
            holder.mTime.setText("" + StringUtils.str2Time(alldata.get(position).created_at));
            holder.mNumber.setText("单号:" + alldata.get(position).code);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(ProjectActivity.this, DetailActivity.class);
                    i.putExtra("apply_name", alldata.get(position).apply_name);
                    i.putExtra("telephone", alldata.get(position).telephone);
                    i.putExtra("address", alldata.get(position).address);
                    i.putExtra("info", alldata.get(position).info);
                    i.putExtra("code", alldata.get(position).code);
                    ProjectActivity.this.startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return alldata.size();
        }

    }


    class MyHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_project_state)
        TextView mState;
        @Bind(R.id.item_project_address)
        TextView mAddress;
        @Bind(R.id.item_project_info)
        TextView mInfo;
        @Bind(R.id.item_project_time)
        TextView mTime;
        @Bind(R.id.item_project_number)
        TextView mNumber;

        public MyHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
