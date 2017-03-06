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
import android.widget.ImageView;
import android.widget.TextView;

import com.android.blm.watermeter.BaseActivity;
import com.android.blm.watermeter.R;
import com.android.blm.watermeter.bean.Bean_CopyResult;
import com.android.blm.watermeter.utils.AppPrefrence;
import com.android.blm.watermeter.utils.PostTools;
import com.android.blm.watermeter.utils.Tools;
import com.android.blm.watermeter.widget.ListViewNestification;
import com.android.blm.watermeter.widget.loading.SVProgressHUD;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * author:${白曌勇} on 2016/6/15
 * TODO:
 */
public class SearchDetial extends BaseActivity {
    private TextView txtUserName, txtUserPhone, txtDoorNo, txtAddress, txtSplot;
    private TextView txtMeterNo, txtMeterType, txtMeterState, txtReadNo, txtReadDate;
    private ListViewNestification listMeter;
    private Bundle bundle;
    private boolean isOpen = false;
    private String meterNo;
    private ImageView imgType;
    private TextView txtStatue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_detial);
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
            txtAddress.setText(bundle.getString("Address"));
            txtDoorNo.setText(bundle.getString("Doorplate"));
            txtUserName.setText(bundle.getString("UserName"));
            txtUserPhone.setText(bundle.getString("Phone"));
            txtSplot.setText(bundle.getString("MergeDeptName"));
            txtReadNo.setText(bundle.getString("LastReadNumber"));
            txtReadDate.setText(bundle.getString("LastReadDate"));
            if (bundle.getString("ValveStatus").contains("开")) {
                isOpen = true;
                txtStatue.setText("关阀");
                imgType.setBackgroundResource(R.mipmap.close_valve);
            } else {
                txtStatue.setText("开阀");
                imgType.setBackgroundResource(R.mipmap.open_valve);
                isOpen = false;
            }
        }

    }

    private void initView() {
        ((TextView) findViewById(R.id.txt_title)).setText("用户详情");
        txtStatue = (TextView) findViewById(R.id.txt_statue);
        imgType = (ImageView) findViewById(R.id.img_contral_type);
        txtSplot = (TextView) findViewById(R.id.txt_splot);
        txtAddress = (TextView) findViewById(R.id.txt_user_address);
        txtDoorNo = (TextView) findViewById(R.id.txt_door_no);
        txtUserName = (TextView) findViewById(R.id.txt_user_name);
        txtUserPhone = (TextView) findViewById(R.id.txt_user_phone);
        listMeter = (ListViewNestification) findViewById(R.id.list_meter);
        txtMeterNo = (TextView) findViewById(R.id.txt_meter_no);
        txtMeterState = (TextView) findViewById(R.id.txt_valve_type);
        txtMeterType = (TextView) findViewById(R.id.txt_meter_type);
        txtReadDate = (TextView) findViewById(R.id.txt_last_read_date);
        txtReadNo = (TextView) findViewById(R.id.txt_read_no);
    }

    @Override
    public void waterClick(View v) {
        super.waterClick(v);
        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;
            case R.id.ll_close_vavle:
                if (isOpen) {
                    SVProgressHUD.showWithStatus(SearchDetial.this, "关阀中...", SVProgressHUD.SVProgressHUDMaskType.Clear);
                } else
                    SVProgressHUD.showWithStatus(SearchDetial.this, "开阀中...", SVProgressHUD.SVProgressHUDMaskType.Clear);
                updateValveState(meterNo);
                break;
            case R.id.ll_open_valve:
                //抄表
                SVProgressHUD.showWithStatus(SearchDetial.this, "抄表中...", SVProgressHUD.SVProgressHUDMaskType.Clear);
                copyMeter(meterNo);
                break;
        }
    }

    private Bean_CopyResult bean_copyResult;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SVProgressHUD.dismiss(SearchDetial.this);
            String result = (String) msg.obj;
            if (TextUtils.isEmpty(result)) {
                Tools.toastMsg(SearchDetial.this, "请检查网络后重试!");
                return;
            }

            if (msg.what == 0) {

                try {
                    JSONObject object = new JSONObject(result);
                    if (object != null && TextUtils.equals(object.getString("Result"), "1")) {

                        Tools.toastMsg(SearchDetial.this, "操作完成");
                        setResult(RESULT_OK, new Intent().putExtra("isSucess", true));

                        finish();
                    } else Tools.toastMsg(SearchDetial.this, object.getString("Message"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (msg.what == 1) {
                bean_copyResult = new Gson().fromJson(result, Bean_CopyResult.class);
                if (bean_copyResult != null && TextUtils.equals(bean_copyResult.Result, "1") && bean_copyResult.Data != null) {
                    Bundle resultBu = new Bundle();
                    txtReadDate.setText(bean_copyResult.Data.get(0).ReadDate);
                    txtReadNo.setText(bean_copyResult.Data.get(0).MeterNumber);
                    resultBu.putString("ReadDate", bean_copyResult.Data.get(0).ReadDate);
                    resultBu.putString("ReadNum", bean_copyResult.Data.get(0).MeterNumber);
                    setResult(100, new Intent().putExtra("copy", resultBu));
                    Tools.toastMsg(SearchDetial.this, "操作完成");
                } else {
                    Tools.toastMsg(SearchDetial.this, bean_copyResult.Message);
                }
            }
        }
    };

    private void copyMeter(String meterAddr) {
        Map<String, String> loginInfo = new HashMap<>();
        loginInfo.put("Code", AppPrefrence.getUserPhone(this));
        loginInfo.put("Token", AppPrefrence.getToken(this));

        Map<String, String> params = new HashMap<>();
        params.put("MeterAddr", meterAddr);
        PostTools.postDataBySoap(this, "ReadMeter", loginInfo, params, handler, 1);
    }

    private void updateValveState(String meterAddr) {
        Map<String, String> loginInfo = new HashMap<>();
        loginInfo.put("Code", AppPrefrence.getUserPhone(this));
        loginInfo.put("Token", AppPrefrence.getToken(this));

        Map<String, String> params = new HashMap<>();
        params.put("MeterAddr", meterAddr);
        if (isOpen) {
            params.put("ValveStatus", "2");
        } else params.put("ValveStatus", "1");
        PostTools.postDataBySoap(this, "ControlValve", loginInfo, params, handler, 0);
    }
}
