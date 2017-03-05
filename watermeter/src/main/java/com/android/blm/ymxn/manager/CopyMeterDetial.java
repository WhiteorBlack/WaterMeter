package com.android.blm.ymxn.manager;/**
 * Created by Administrator on 2016/6/15.
 */

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.blm.ymxn.BaseActivity;
import com.android.blm.ymxn.R;
import com.android.blm.ymxn.bean.Bean_CopyResult;
import com.android.blm.ymxn.utils.AppPrefrence;
import com.android.blm.ymxn.utils.PostTools;
import com.android.blm.ymxn.utils.Tools;
import com.android.blm.ymxn.widget.loading.SVProgressHUD;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * author:${白曌勇} on 2016/6/15
 * TODO:
 */
public class CopyMeterDetial extends BaseActivity {
    private TextView txtUserName, txtUserPhone, txtDoorNo, txtAddress, txtSplot;
    private TextView txtMeterNo, txtMeterType, txtMeterState, txtMeterNum, txtMeterDate;
    private Bundle bundle;
    private String meterNo;
    private int parent, child;
    String meterState = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.copy_meter_detial);
        initView();
        setData();
    }

    private void setData() {
        bundle = getIntent().getBundleExtra("data");
        if (bundle != null) {
            meterNo = bundle.getString("MeterAddr");
            meterState = bundle.getString("ValveStatus");
            txtMeterType.setText(bundle.getString("MeterTypeName"));
            txtMeterState.setText(bundle.getString("ValveStatus"));
            txtMeterNo.setText(bundle.getString("MeterAddr"));
            txtAddress.setText(bundle.getString("Address"));
            txtDoorNo.setText(bundle.getString("Doorplate"));
            txtUserName.setText(bundle.getString("UserName"));
            txtUserPhone.setText(bundle.getString("Phone"));
            txtSplot.setText(bundle.getString("MergeDeptName"));
            txtMeterDate.setText(bundle.getString("LastReadDate"));
            txtMeterNum.setText(bundle.getString("LastReadNumber"));

            parent = bundle.getInt("parent");
            child = bundle.getInt("child");
            Tools.debug("parent" + parent + "child" + child);
        }

    }

    private void initView() {

        txtSplot = (TextView) findViewById(R.id.txt_splot);
        txtAddress = (TextView) findViewById(R.id.txt_user_address);
        txtDoorNo = (TextView) findViewById(R.id.txt_door_no);
        txtUserName = (TextView) findViewById(R.id.txt_user_name);
        txtUserPhone = (TextView) findViewById(R.id.txt_user_phone);
        txtMeterNo = (TextView) findViewById(R.id.txt_meter_no);
        txtMeterState = (TextView) findViewById(R.id.txt_valve_type);
        txtMeterType = (TextView) findViewById(R.id.txt_meter_type);
        txtMeterDate = (TextView) findViewById(R.id.txt_last_read_date);
        txtMeterNum = (TextView) findViewById(R.id.txt_current_num);
    }

    @Override
    public void waterClick(View v) {
        super.waterClick(v);
        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;
            case R.id.ll_close_vavle:
                break;
            case R.id.ll_open_valve:
                SVProgressHUD.showWithStatus(CopyMeterDetial.this, "读表中...", SVProgressHUD.SVProgressHUDMaskType.Clear);
                copyMeter(meterNo);
                break;
        }
    }

    private void copyMeter(String meterAddr) {
        Map<String, String> loginInfo = new HashMap<>();
        loginInfo.put("Code", AppPrefrence.getUserPhone(this));
        loginInfo.put("Token", AppPrefrence.getToken(this));
        Map<String, String> params = new HashMap<>();
        params.put("MeterAddr", meterAddr);
        PostTools.postDataBySoap(this, "ReadMeter", loginInfo, params, handler, 1);
    }

    private Bean_CopyResult bean_copyResult;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SVProgressHUD.dismiss(CopyMeterDetial.this);
            String result = (String) msg.obj;
            Tools.debug(result);
            if (TextUtils.isEmpty(result)) {
                Tools.toastMsg(CopyMeterDetial.this, "请检查网络后重试!");
                return;
            }
            bean_copyResult = new Gson().fromJson(result, Bean_CopyResult.class);
            if (bean_copyResult != null && TextUtils.equals(bean_copyResult.Result, "1") && bean_copyResult.Data != null) {
                Bundle resultBu = new Bundle();
                txtMeterDate.setText(bean_copyResult.Data.get(0).ReadDate);
                txtMeterNum.setText(bean_copyResult.Data.get(0).MeterNumber);
                resultBu.putInt("parent", parent);
                resultBu.putInt("child", child);
                setResult(1, new Intent().putExtra("result", resultBu));
            } else {
                Tools.toastMsg(CopyMeterDetial.this, bean_copyResult.Message);
            }


        }
    };

}
