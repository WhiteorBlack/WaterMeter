package com.android.blm.ymxn.activity;/**
 * Created by Administrator on 2016/6/5.
 */

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.android.blm.ymxn.BaseActivity;
import com.android.blm.ymxn.R;
import com.android.blm.ymxn.alipay.AliPayHelper;
import com.android.blm.ymxn.bean.Bean_ChatOrder;
import com.android.blm.ymxn.utils.AppPrefrence;
import com.android.blm.ymxn.utils.CommonUntilities;
import com.android.blm.ymxn.utils.PostTools;
import com.android.blm.ymxn.utils.Tools;
import com.android.blm.ymxn.widget.loading.SVProgressHUD;
import com.android.blm.ymxn.wxpay.WxPayHelper;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * author:${白曌勇} on 2016/6/5
 * TODO:
 */
public class RechargeOnline extends BaseActivity {
    private TextView txtCurrentMoney;
    private EditText edtMoney;
    private CheckBox chbAlipay, chbWechat;
    private String type = "1";
    private String money;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recharge_online);
        initView();
    }

    private void initView() {
        txtCurrentMoney = (TextView) findViewById(R.id.txt_current_money);
        txtCurrentMoney.setText(" " + AppPrefrence.getUserReserve(this) + "元");
        edtMoney = (EditText) findViewById(R.id.edt_recharge_money);
        chbAlipay = (CheckBox) findViewById(R.id.check_alipay);
        chbAlipay.setChecked(true);
        chbWechat = (CheckBox) findViewById(R.id.check_wechat);
    }

    @Override
    public void waterClick(View v) {
        super.waterClick(v);
        switch (v.getId()) {
            case R.id.rl_alipay:
                chbAlipay.setChecked(true);
                chbWechat.setChecked(false);

                break;
            case R.id.rl_wechat:
                chbWechat.setChecked(true);
                chbAlipay.setChecked(false);

                break;
            case R.id.btn_confirm:
                if (chbAlipay.isChecked()) {
                    type = "2";
                } else type = "1";
                money = edtMoney.getText().toString();
                if (TextUtils.isEmpty(money)) {
                    Tools.toastMsg(RechargeOnline.this, "请输入充值金额");
                    return;
                }
                SVProgressHUD.showWithStatus(RechargeOnline.this, "获取订单中...", SVProgressHUD.SVProgressHUDMaskType.Clear);
                getChatOrder();
                break;
            case R.id.fl_back:
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CommonUntilities.ISWXPAY) {
            CommonUntilities.ISWXPAY = false;
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            String accountMoney = decimalFormat.format(AppPrefrence.getUserReserve(RechargeOnline.this) + Float.parseFloat(money));
            AppPrefrence.setUserReserve(RechargeOnline.this, AppPrefrence.getUserReserve(RechargeOnline.this) + Float.parseFloat(money));
            txtCurrentMoney.setText(accountMoney + "元");
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                String accountMoney = decimalFormat.format(AppPrefrence.getUserReserve(RechargeOnline.this) + Float.parseFloat(money));
                AppPrefrence.setUserReserve(RechargeOnline.this, AppPrefrence.getUserReserve(RechargeOnline.this) + Float.parseFloat(money));
                txtCurrentMoney.setText(accountMoney + "元");
                return;
            }
            if (msg.what == 1) {
                return;
            }
            SVProgressHUD.dismiss(RechargeOnline.this);
            String result = (String) msg.obj;
            Tools.debug(result);
            if (TextUtils.isEmpty(result)) {
                Tools.toastMsg(RechargeOnline.this, "重试失败请重试");
                return;
            }
            Bean_ChatOrder bean_chatOrder = new Gson().fromJson(result, Bean_ChatOrder.class);
            if (bean_chatOrder != null && bean_chatOrder.Result > 0) {
                if (TextUtils.equals(type, "2"))
                    new AliPayHelper(RechargeOnline.this, handler).pay(bean_chatOrder.Data.get(0).OrderInfo.replace("\\", ""));
                if (TextUtils.equals(type, "1")) {
//                    new WxPayHelper(RechargeOnline.this).getPrepayInfo(bean_chatOrder.Data.get(0).OrderInfo);
                    new WxPayHelper(RechargeOnline.this).genPayRep(bean_chatOrder.Data.get(0).OrderInfo);
                }
            }

        }
    };

    private void getChatOrder() {
        Map<String, String> params = new HashMap<>();
        params.put("UserCode", AppPrefrence.getUsercode(this));
        params.put("Money", money);
        params.put("OrderType", type);
        Map<String, String> loginParams = new HashMap<>();
        loginParams.put("Code", AppPrefrence.getUserPhone(this));
        loginParams.put("Token", AppPrefrence.getToken(this));
        PostTools.postDataBySoap(this, "Pay", loginParams, params, handler, 3);
    }

    private void getAliOrder() {
    }
}
