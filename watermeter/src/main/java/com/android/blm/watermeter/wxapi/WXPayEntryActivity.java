package com.android.blm.watermeter.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;

import com.android.blm.watermeter.utils.CommonUntilities;
import com.android.blm.watermeter.utils.Tools;
import com.android.blm.watermeter.wxpay.Constants;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.pay_result);

        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);

        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
        String reqString = req.openId;
    }

    @Override
    public void onResp(BaseResp resp) {
        int code = resp.errCode;
        Tools.debug("小老鼠微信支付结果---》" + code);
        if (TextUtils.equals(code + "", "0")) {// 0表示支付成功
            CommonUntilities.ISWXPAY = true;
        } else if (TextUtils.equals(code + "", "-2")) {//-2支付取消
        }
        finish();
    }
}