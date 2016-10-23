package com.android.blm.watermeter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ImageView;

import com.android.blm.watermeter.bean.Bean_LoginIfno;
import com.android.blm.watermeter.login.LoginActivity;
import com.android.blm.watermeter.login.SelectAccount;
import com.android.blm.watermeter.utils.AppPrefrence;
import com.android.blm.watermeter.utils.PostTools;
import com.android.blm.watermeter.utils.Tools;
import com.google.gson.Gson;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.service.XGPushService;

import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    private ImageView imgPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
        if (AppPrefrence.getIsLogin(this) && !AppPrefrence.getIsMsg(this)) {
            login();
            XGPushManager.registerPush(getApplicationContext(), AppPrefrence.getUsercode(this), new XGIOperateCallback() {
                @Override
                public void onSuccess(Object o, int i) {
                    Tools.debug("success----");
                }

                @Override
                public void onFail(Object o, int i, String s) {
                    Tools.debug("success----" + o.toString() + "--" + i + "--" + s);
                }
            });
        } else XGPushManager.registerPush(getApplicationContext());
        postTime();


        // 2.36（不包括）之前的版本需要调用以下2行代码
        Intent service = new Intent(getApplicationContext(), XGPushService.class);
        startService(service);
    }

    private void login() {
        Tools.debug(AppPrefrence.getUsercode(this) + "---" + AppPrefrence.getUserPwd(this));
        Map<String, String> params = new HashMap<>();
        Map<String, String> loginInfo = new HashMap<>();
        loginInfo.put("Pwd", AppPrefrence.getUserPwd(this));
        loginInfo.put("Code", AppPrefrence.getUsercode(this));
        PostTools.postDataBySoap(this, "Login", loginInfo, params, handler, 0);
    }

    private Bean_LoginIfno loginIfno;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String) msg.obj;
            Tools.debug("result" + result);
            if (TextUtils.isEmpty(result)) {
                return;
            }

            loginIfno = new Gson().fromJson(result, Bean_LoginIfno.class);
            if (loginIfno != null && TextUtils.equals("1", loginIfno.Result) && loginIfno.Data != null) {
                AppPrefrence.setIsLogin(SplashActivity.this, true);
                if (!TextUtils.equals(loginIfno.Data.get(0).LoginType, "1")) {
                    AppPrefrence.setIsUser(SplashActivity.this, true);

                    if (loginIfno.Data.size() > 2) {
                        AppPrefrence.setAccountCount(SplashActivity.this, loginIfno.Data.size() - 1);
                        startActivity(new Intent(SplashActivity.this, SelectAccount.class).putExtra("info", result).putExtra("isHome", true));
                    } else {
//                        AppPrefrence.setAccountCount(SplashActivity.this, loginIfno.Data.get(1).Reserve+"");
                        AppPrefrence.setToken(SplashActivity.this, loginIfno.Data.get(0).Token);
                        AppPrefrence.setUsercode(SplashActivity.this, loginIfno.Data.get(1).UserCode);
                        AppPrefrence.setDoorno(SplashActivity.this, loginIfno.Data.get(1).Doorplate);
                        AppPrefrence.setRealName(SplashActivity.this, loginIfno.Data.get(1).UserName);
                        AppPrefrence.setUserAdd(SplashActivity.this, loginIfno.Data.get(1).Address);
                        AppPrefrence.setUserReadDate(SplashActivity.this, loginIfno.Data.get(1).LastReadDate);
                        AppPrefrence.setUserReadNo(SplashActivity.this, loginIfno.Data.get(1).LastReadNumber);
                        AppPrefrence.setUserReserve(SplashActivity.this, loginIfno.Data.get(1).Reserve);
                        AppPrefrence.setMeterNo(SplashActivity.this, loginIfno.Data.get(1).MeterAddr);
                    }
                } else {
                    AppPrefrence.setIsUser(SplashActivity.this, false);
                    AppPrefrence.setToken(SplashActivity.this, loginIfno.Data.get(0).Token);
                    AppPrefrence.setUsercode(SplashActivity.this, loginIfno.Data.get(0).OperatorCode);
                    AppPrefrence.setUserName(SplashActivity.this, loginIfno.Data.get(0).OperatorName);
                }
            }
        }
    };


    private void postTime() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AppPrefrence.getIsLogin(SplashActivity.this) && !AppPrefrence.getIsMsg(SplashActivity.this)) {
                    if (AppPrefrence.getIsUser(SplashActivity.this)) {
                        startActivity(new Intent(SplashActivity.this, Home.class));
                    } else startActivity(new Intent(SplashActivity.this, ManagerHome.class));
                    finish();
                    return;
                }
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));

                finish();
            }
        }, 3000);
    }

    private void initView() {
        imgPhoto = (ImageView) findViewById(R.id.img_splash_pic);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        return;
    }
}
