package com.android.blm.watermeter.manager;/**
 * Created by Administrator on 2016/6/15.
 */

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.blm.watermeter.BaseActivity;
import com.android.blm.watermeter.R;
import com.android.blm.watermeter.utils.AppPrefrence;
import com.android.blm.watermeter.utils.PostTools;
import com.android.blm.watermeter.utils.Tools;
import com.android.blm.watermeter.widget.ListViewNestification;
import com.android.blm.watermeter.widget.loading.SVProgressHUD;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * author:${白曌勇} on 2016/6/15
 * TODO:
 */
public class OwenMoneyDetial extends BaseActivity {
    private TextView txtUserName, txtUserPhone, txtDoorNo, txtAddress, txtSplot;
    private TextView txtMeterNo, txtMeterType, txtMeterState;
    private ListViewNestification listMeter;
    private Bundle bundle;
    private boolean isOpen = false;
    private String meterNo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.owen_money_detial);
        initView();
        setData();
    }

    private void setData() {
        bundle = getIntent().getBundleExtra("data");
        if (bundle != null) {
            meterNo = bundle.getString("MeterAddr");
            txtMeterType.setText(bundle.getString("MeterTypeName"));
            txtMeterState.setText(bundle.getString("ValveStatus"));
            txtMeterNo.setText(bundle.getString("MeterAddr"));
            isOpen = bundle.getBoolean("isOpen");
            if (!isOpen) {
                ((LinearLayout) findViewById(R.id.ll_close_vavle)).setVisibility(View.GONE);
            } else {
                ((LinearLayout) findViewById(R.id.ll_open_valve)).setVisibility(View.GONE);
            }
            txtAddress.setText(bundle.getString("Address"));
            txtDoorNo.setText(bundle.getString("Doorplate"));
            txtUserName.setText(bundle.getString("UserCode"));
            txtUserPhone.setText(bundle.getString("Phone"));
            txtSplot.setText(bundle.getString("MergeDeptName"));
        }

    }

    private void initView() {
        if (getIntent().getBooleanExtra("isContral", false)) {
            ((TextView) findViewById(R.id.txt_title)).setText("用户详情");
        }
        txtSplot = (TextView) findViewById(R.id.txt_splot);
        txtAddress = (TextView) findViewById(R.id.txt_user_address);
        txtDoorNo = (TextView) findViewById(R.id.txt_door_no);
        txtUserName = (TextView) findViewById(R.id.txt_user_name);
        txtUserPhone = (TextView) findViewById(R.id.txt_user_phone);
        listMeter = (ListViewNestification) findViewById(R.id.list_meter);
        txtMeterNo = (TextView) findViewById(R.id.txt_meter_no);
        txtMeterState = (TextView) findViewById(R.id.txt_valve_type);
        txtMeterType = (TextView) findViewById(R.id.txt_meter_type);
    }

    @Override
    public void waterClick(View v) {
        super.waterClick(v);
        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;
            case R.id.ll_close_vavle:
                updateValveState(meterNo);
                break;
            case R.id.ll_open_valve:
                updateValveState(meterNo);
                break;
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SVProgressHUD.dismiss(OwenMoneyDetial.this);
            String result = (String) msg.obj;
            if (TextUtils.isEmpty(result)) {
                Tools.toastMsg(OwenMoneyDetial.this, "请检查网络后重试!");
                return;
            }
            try {
                JSONObject object = new JSONObject(result);
                if (object != null && TextUtils.equals(object.getString("Result"), "1")) {
                    setResult(1, new Intent().putExtra("result", bundle));
                    finish();
                }
                Tools.toastMsg(OwenMoneyDetial.this, object.getString("Message"));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    private String statue;

    private void updateValveState(String meterAddr) {
        if (isOpen) {
            SVProgressHUD.showWithStatus(this, "关阀中...", SVProgressHUD.SVProgressHUDMaskType.Clear);
            statue = "2";
        } else {
            statue = "1";
            SVProgressHUD.showWithStatus(this, "开阀中...", SVProgressHUD.SVProgressHUDMaskType.Clear);
        }
        Map<String, String> loginInfo = new HashMap<>();
        loginInfo.put("Code", AppPrefrence.getUserPhone(this));
        loginInfo.put("Token", AppPrefrence.getToken(this));

        Map<String, String> params = new HashMap<>();
        params.put("MeterAddr", meterAddr);
        params.put("ValveStatus", statue);
        PostTools.postDataBySoap(this, "ControlValve", loginInfo, params, handler, 1);
    }
}
