package com.android.blm.watermeter.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.android.blm.watermeter.BaseActivity;
import com.android.blm.watermeter.Home;
import com.android.blm.watermeter.ManagerHome;
import com.android.blm.watermeter.R;
import com.android.blm.watermeter.bean.Bean_LoginIfno;
import com.android.blm.watermeter.utils.AppPrefrence;
import com.android.blm.watermeter.utils.PostTools;
import com.android.blm.watermeter.utils.Tools;
import com.android.blm.watermeter.widget.loading.SVProgressHUD;
import com.google.gson.Gson;
import com.tencent.android.tpush.XGPushManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/24.
 */
public class LoginActivity extends BaseActivity {
    private EditText edtUserName, edtPwd;
    //    private TextInputLayout inputUserName, inputPwd;
    private String userName, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        userName = getIntent().getStringExtra("userCode");
        initView();
    }

    private void initView() {
        edtUserName = (EditText) findViewById(R.id.edt_user_name);
        if (!TextUtils.isEmpty(userName)) {
            edtUserName.setText(userName);
        }
        edtUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (!TextUtils.isEmpty(s))
//                    inputUserName.setError("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtPwd = (EditText) findViewById(R.id.edt_user_pwd);
        edtPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (!TextUtils.isEmpty(s))
//                    inputPwd.setError("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        inputPwd = (TextInputLayout) findViewById(R.id.input_pwd);
//        inputPwd.setErrorEnabled(true);
//
//        inputUserName = (TextInputLayout) findViewById(R.id.input_user_name);
//        inputUserName.setErrorEnabled(true);

    }

    @Override
    public void waterClick(View v) {
        super.waterClick(v);
        switch (v.getId()) {
            case R.id.btn_login:
                //登录

                userName = edtUserName.getText().toString();
                password = edtPwd.getText().toString();
                if (TextUtils.isEmpty(userName)) {
                    Tools.toastMsg(LoginActivity.this, "用户名不能为空!");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Tools.toastMsg(LoginActivity.this, "密码不能为空!");
                    return;
                }
                login();
                break;
            case R.id.txt_msg_login:
                //短信验证登录
                startActivity(new Intent(LoginActivity.this, LoginByMsgActivity.class));
                finish();
                break;

        }
    }

    private void loginManager() {

        Map<String, String> params = new HashMap<>();
        Map<String, String> loginInfo = new HashMap<>();
        loginInfo.put("Pwd", Tools.get32MD5Str(password));
        loginInfo.put("Code", userName);
        PostTools.postDataBySoap(this, "Login", loginInfo, params, handler, 1);
    }

    private void login() {
        SVProgressHUD.showWithStatus(this, "登录中...", SVProgressHUD.SVProgressHUDMaskType.Clear);

        Map<String, String> params = new HashMap<>();
        Map<String, String> loginInfo = new HashMap<>();
        loginInfo.put("Pwd", Tools.get32MD5Str(password));
        loginInfo.put("Code", userName);
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
                Tools.toastMsg(LoginActivity.this, "请检查网络后重试");
                return;
            }
            try{

            }catch (Exception e){
                Tools.toastMsg(LoginActivity.this,"后台错误，请联系管理员");
            }
            loginIfno = new Gson().fromJson(result, Bean_LoginIfno.class);
            if (loginIfno != null && TextUtils.equals("1", loginIfno.Result) && loginIfno.Data != null) {
                AppPrefrence.setIsLogin(LoginActivity.this, true);
                AppPrefrence.setUserPhone(LoginActivity.this, userName);
                AppPrefrence.setUserPwd(LoginActivity.this, Tools.get32MD5Str(password));
                if (!TextUtils.equals(loginIfno.Data.get(0).LoginType, "1")) {
                    AppPrefrence.setIsUser(LoginActivity.this, true);
                    AppPrefrence.setIsMsg(LoginActivity.this, false);
                    if (loginIfno.Data.size() > 2) {
                        AppPrefrence.setAccountCount(LoginActivity.this, loginIfno.Data.size() - 1);
                        AppPrefrence.setIsLogin(LoginActivity.this, false);
                        startActivity(new Intent(LoginActivity.this, SelectAccount.class).putExtra("info", result).putExtra("isHome", true));

                    } else {
                        AppPrefrence.setToken(LoginActivity.this, loginIfno.Data.get(0).Token);
                        AppPrefrence.setAccountCount(LoginActivity.this, 1);
                        AppPrefrence.setUsercode(LoginActivity.this, loginIfno.Data.get(1).UserCode);
                        AppPrefrence.setDoorno(LoginActivity.this, loginIfno.Data.get(1).Doorplate);
                        AppPrefrence.setRealName(LoginActivity.this, loginIfno.Data.get(1).UserName);
                        AppPrefrence.setUserAdd(LoginActivity.this, loginIfno.Data.get(1).Address);
                        AppPrefrence.setUserReadDate(LoginActivity.this, loginIfno.Data.get(1).LastReadDate);
                        AppPrefrence.setUserReadNo(LoginActivity.this, loginIfno.Data.get(1).LastReadNumber);
                        AppPrefrence.setUserReserve(LoginActivity.this, loginIfno.Data.get(1).Reserve);
                        AppPrefrence.setMeterNo(LoginActivity.this, loginIfno.Data.get(1).MeterAddr);
                        AppPrefrence.setPhone(LoginActivity.this,loginIfno.Data.get(1).Phone);
                        startActivity(new Intent(LoginActivity.this, Home.class));
                    }
                } else {
                    AppPrefrence.setIsUser(LoginActivity.this, false);
                    AppPrefrence.setToken(LoginActivity.this, loginIfno.Data.get(0).Token);
                    AppPrefrence.setUsercode(LoginActivity.this, loginIfno.Data.get(0).OperatorCode);
                    AppPrefrence.setUserName(LoginActivity.this, loginIfno.Data.get(0).OperatorName);
                    startActivity(new Intent(LoginActivity.this, ManagerHome.class));
                }
                XGPushManager.registerPush(getApplicationContext(), AppPrefrence.getUsercode(LoginActivity.this));
                finish();
            } else {
                Tools.toastMsg(LoginActivity.this, loginIfno.Message);
            }
            SVProgressHUD.dismiss(LoginActivity.this);
        }
    };


}
