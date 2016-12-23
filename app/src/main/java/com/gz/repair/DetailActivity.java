package com.gz.repair;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalListDialog;
import com.google.gson.Gson;
import com.gz.repair.bean.RepairPerson;
import com.gz.repair.bean.SubProject;
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
import butterknife.OnClick;

/*
 * 派工详情
 */
public class DetailActivity extends Activity {


    @Bind(R.id.detail_name)
    TextView detailName;
    @Bind(R.id.detail_phone)
    TextView detailPhone;
    @Bind(R.id.address)
    TextView mAddress;
    @Bind(R.id.tv_person)
    TextView mPerson;
    @Bind(R.id.preson)
    RelativeLayout rlPreson;
    @Bind(R.id.detail)
    TextView detail;
    @Bind(R.id.tv_wxf)
    TextView tvWxf;
    @Bind(R.id.rl_wxf)
    RelativeLayout rlWxf;
    @Bind(R.id.progressBar)
    CircleProgressBar progressBar;
    @Bind(R.id.et_repair_preson)
    EditText mRepairPreson;
    @Bind(R.id.rb_free)
    RadioButton rbFree;
    @Bind(R.id.rb_money)
    RadioButton rbMoney;
    private String code;
    private String maintainer;
    private int maintainer_id;
    private ArrayList<RepairPerson.Result> person;
    private ArrayList<String> pName;
    private int repair_type = 0;
    private boolean threeIsShow = false;// 第三方输入框是否显示

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String apply_name = intent.getStringExtra("apply_name");
        String telephone = intent.getStringExtra("telephone");
        String address = intent.getStringExtra("address");
        String info = intent.getStringExtra("info");
        code = intent.getStringExtra("code");

        detailName.setText(apply_name);
        detailPhone.setText(telephone);
        mAddress.setText(address);
        detail.setText(info);

        tvWxf.setText(wxfStringItems[0]);

        getPersonFromServer();


    }

    @OnClick({R.id.preson, R.id.ok, R.id.rl_wxf})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.preson:
                if (mStringItems != null) {

                    NormalListDialogCustomAttr();
                } else {
                    T.showShort(DetailActivity.this, "派工人员获取失败");
                }

                break;
            case R.id.rl_wxf:
                wxfDialogCustomAttr();
                break;
            case R.id.ok:

                subForServer();
                break;
        }
    }

    private String[] wxfStringItems = {"物业", "第三方"};

    private void wxfDialogCustomAttr() {
        final NormalListDialog dialog = new NormalListDialog(this, wxfStringItems);
        dialog.title("请选择")//
                .titleTextSize_SP(18)//
                .titleBgColor(Color.parseColor("#0097A7"))//
                .itemPressColor(Color.parseColor("#00BCD4"))//
                .itemTextColor(Color.parseColor("#303030"))//
                .itemTextSize(14)//
                .cornerRadius(3)//
                .widthScale(0.7f)//
                .show();

        dialog.setOnOperItemClickL(new OnOperItemClickL() {

            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
//                T.showShort(DetailActivity.this, mStringItems[position]);
                tvWxf.setText(wxfStringItems[position]);
                repair_type = position;
                if (position == 1) {
                    rlPreson.setVisibility(View.GONE);
                    mRepairPreson.setVisibility(View.VISIBLE);
                    threeIsShow = true;
                }
                if (position == 0) {
                    mRepairPreson.setVisibility(View.GONE);
                    rlPreson.setVisibility(View.VISIBLE);
                    threeIsShow = false;
                }
                dialog.dismiss();
            }
        });
    }


    // 确认派工
    private void subForServer() {
        if (threeIsShow) {
            String name = mRepairPreson.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                T.showShort(DetailActivity.this, "请输入维修人员姓名");
                return;
            }
        }
        progressBar.setVisibility(View.VISIBLE);
        Log.e("my", "开始请求");
        String url = MyAppcation.baseUrl + "/repair_distribute";
        RequestParams params = new RequestParams(url);

        params.addParameter("code", code);

        if (threeIsShow) {
            String name = mRepairPreson.getText().toString().trim();
            params.addParameter("maintainer", name);
            params.addParameter("maintainer_id", "");
        } else {

            params.addParameter("maintainer", maintainer);
            params.addParameter("maintainer_id", maintainer_id);
        }
        params.addParameter("repair_type", repair_type);
        params.addParameter("user_name", MyAppcation.userName);

        if (rbFree.isChecked()) {
            params.addParameter("is_charged", 0);
        } else if (rbMoney.isChecked()) {
            params.addParameter("is_charged", 1);
        } else {
            params.addParameter("is_charged", 0);
        }

        Log.e("ende","params=="+params);
        x.http().post(params, new Callback.CommonCallback<String>() {


            @Override
            public void onSuccess(String result) {
                Log.e("my", "onSuccess" + result);

                SubProject sub = new Gson().fromJson(result, SubProject.class);
                if (sub.msg != null) {

                    T.showShort(DetailActivity.this, sub.msg);
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


    }

    public void getPersonFromServer() {
        Log.e("my", "开始请求");
        String url = MyAppcation.baseUrl + "/get_repair_person";
        RequestParams params = new RequestParams(url);
//        params.addBodyParameter("root_id", MyAppcation.rootId);
        params.addParameter("user_id", MyAppcation.userId);
        x.http().post(params, new Callback.CommonCallback<String>() {


            @Override
            public void onSuccess(String result) {
                Log.e("my", "onSuccess" + result);
                try {
                    JSONObject jo = new JSONObject(result);
                    int ret = jo.getInt("ret");
                    String msg = jo.getString("msg");
                    if (ret != 0) {
                        T.showShort(DetailActivity.this, "获取派工人员失败");
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Gson gson = new Gson();
                RepairPerson repairPerson = gson.fromJson(result, RepairPerson.class);
                if (repairPerson.ret == 0) {

//                    T.showShort(DetailActivity.this, repairPerson.msg);

                    person = repairPerson.result;
                    pName = new ArrayList<String>();


                    for (int i = 0; i < person.size(); i++) {
                        pName.add(person.get(i).name);

                    }

                    if (pName.size() > 0) {

                        mStringItems = pName.toArray(new String[pName.size()]);
                        mPerson.setText(mStringItems[0]);
                        maintainer = mStringItems[0];
                        maintainer_id = person.get(0).id;
                    }
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
//                Log.e("code==", code);
//                Log.e("maintainer==", maintainer);
//                Log.e("maintainer_id==", String.valueOf(maintainer_id));
//                Log.e("repair_type==", String.valueOf(repair_type));
//                Log.e("user_name==", MyAppcation.userName);
            }

        });
    }

    //    private String[] mStringItems = {"张三", "李四", "王5", "赵六"};
    private String[] mStringItems;

    // NormalListDialog
    private void NormalListDialogCustomAttr() {
        final NormalListDialog dialog = new NormalListDialog(this, mStringItems);
        dialog.title("请选择")//
                .titleTextSize_SP(18)//
                .titleBgColor(Color.parseColor("#0097A7"))//
                .itemPressColor(Color.parseColor("#00BCD4"))//
                .itemTextColor(Color.parseColor("#303030"))//
                .itemTextSize(14)//
                .cornerRadius(3)//
                .widthScale(0.7f)//
                .show();

        dialog.setOnOperItemClickL(new OnOperItemClickL() {

            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
//                T.showShort(DetailActivity.this, mStringItems[position]);
                mPerson.setText(mStringItems[position]);
                maintainer = mStringItems[position];
                maintainer_id = person.get(position).id;
                dialog.dismiss();
            }
        });
    }


}
