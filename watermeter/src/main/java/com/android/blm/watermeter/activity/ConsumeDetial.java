package com.android.blm.watermeter.activity;/**
 * Created by Administrator on 2016/6/1.
 */

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.blm.watermeter.BaseActivity;
import com.android.blm.watermeter.R;
import com.android.blm.watermeter.adapter.ConsumeDetialAdapter;
import com.android.blm.watermeter.adapter.ConsumeDetialFeeAdapter;
import com.android.blm.watermeter.bean.Bean_ConsumeDetial;
import com.android.blm.watermeter.utils.AppPrefrence;
import com.android.blm.watermeter.utils.PostTools;
import com.android.blm.watermeter.utils.Tools;
import com.android.blm.watermeter.widget.ListViewNestification;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * author:${白曌勇} on 2016/6/1
 * TODO:消费详情
 */
public class ConsumeDetial extends BaseActivity {
    private ListViewNestification listViewDetial, listViewFee;
    private TextView txtPreLeft, txtCurrentLeft;
    private String id;
    private ConsumeDetialAdapter detialAdapter;
    private ConsumeDetialFeeAdapter feeAdapter;
    private List<Bean_ConsumeDetial.UsedDetial> consumeList;
    private List<Bean_ConsumeDetial.FareDetial> feeList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consume_detial);
        id = getIntent().getStringExtra("id");
        initView();
        getDetialData();
    }

    Bean_ConsumeDetial bean_ConsumeDetial;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String) msg.obj;
            Tools.debug("detial" + result);
            if (TextUtils.isEmpty(result)) {
                Tools.toastMsg(ConsumeDetial.this, "请检查网络后重试");
            } else {
                bean_ConsumeDetial = new Gson().fromJson(result, Bean_ConsumeDetial.class);
                if (bean_ConsumeDetial != null && TextUtils.equals(bean_ConsumeDetial.Result, "1") && bean_ConsumeDetial.Data != null && bean_ConsumeDetial.Data.size() > 0) {
                    feeList.addAll(bean_ConsumeDetial.Data.get(0).FareDetail);
                    feeAdapter.notifyDataSetChanged();
                    Bean_ConsumeDetial.UsedDetial usedDetial = new Bean_ConsumeDetial.UsedDetial();
                    usedDetial.UseType = "第一阶梯";
                    usedDetial.UseCount = bean_ConsumeDetial.Data.get(0).UseNumber1;
                    consumeList.add(usedDetial);

                    Bean_ConsumeDetial.UsedDetial usedDetial2 = new Bean_ConsumeDetial.UsedDetial();
                    usedDetial2.UseType = "第二阶梯";
                    usedDetial2.UseCount = bean_ConsumeDetial.Data.get(0).UseNumber2;
                    consumeList.add(usedDetial2);

                    Bean_ConsumeDetial.UsedDetial usedDetial3 = new Bean_ConsumeDetial.UsedDetial();
                    usedDetial3.UseType = "第三阶梯";
                    usedDetial3.UseCount = bean_ConsumeDetial.Data.get(0).UseNumber3;
                    consumeList.add(usedDetial3);
                    detialAdapter.notifyDataSetChanged();

                    txtPreLeft.setText("¥" + Tools.formatDouble(bean_ConsumeDetial.Data.get(0).LastBalance));
                    txtCurrentLeft.setText("¥" + Tools.formatDouble(bean_ConsumeDetial.Data.get(0).ThisBalance));
                } else
                    Tools.toastMsg(ConsumeDetial.this, bean_ConsumeDetial.Message);
            }
        }
    };

    private void getDetialData() {
        Map<String, String> loginParam = new HashMap<>();
        Map<String, String> params = new HashMap<>();
        loginParam.put("Code", AppPrefrence.getUsercode(this));
        loginParam.put("Token", AppPrefrence.getToken(this));
        params.put("OperID", id);
        PostTools.postDataBySoap(this, "GetConsumeDetail", loginParam, params, handler, 0);
    }


    private void initView() {
        consumeList = new ArrayList<>();
        feeList = new ArrayList<>();
        feeAdapter = new ConsumeDetialFeeAdapter(feeList, this);
        detialAdapter = new ConsumeDetialAdapter(consumeList, this);
        listViewDetial = (ListViewNestification) findViewById(R.id.listview_used_detial);

        listViewDetial.setAdapter(detialAdapter);
        listViewFee = (ListViewNestification) findViewById(R.id.listview_fee_detial);
        listViewFee.setAdapter(feeAdapter);
        txtCurrentLeft = (TextView) findViewById(R.id.txt_current_left);
        txtPreLeft = (TextView) findViewById(R.id.txt_pre_left);
    }

    @Override
    public void waterClick(View v) {
        super.waterClick(v);
        finish();
    }


}
