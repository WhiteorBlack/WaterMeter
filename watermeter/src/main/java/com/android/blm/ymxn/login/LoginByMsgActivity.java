package com.android.blm.ymxn.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.blm.ymxn.BaseActivity;
import com.android.blm.ymxn.Home;
import com.android.blm.ymxn.ManagerHome;
import com.android.blm.ymxn.R;
import com.android.blm.ymxn.bean.Bean_LoginIfno;
import com.android.blm.ymxn.utils.AppPrefrence;
import com.android.blm.ymxn.utils.PostTools;
import com.android.blm.ymxn.utils.Tools;
import com.android.blm.ymxn.widget.loading.SVProgressHUD;
import com.google.gson.Gson;
import com.tencent.android.tpush.XGPushManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/24.
 */
public class LoginByMsgActivity extends BaseActivity {
    private EditText edtUserName, edtPwd;
    //    private TextInputLayout inputUserName, inputPwd;
    private String userName, password;
    private TextView txtGetCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_by_msg_activity);
        initView();
    }

    private void initView() {
        txtGetCode = (TextView) findViewById(R.id.txt_get_code);
        edtUserName = (EditText) findViewById(R.id.edt_user_name);
        edtPwd = (EditText) findViewById(R.id.edt_user_pwd);


    }

    @Override
    public void waterClick(View v) {
        super.waterClick(v);
        switch (v.getId()) {
            case R.id.btn_login:
                //登录
                userName = edtUserName.getText().toString();
                password = edtPwd.getText().toString();
                if (TextUtils.isEmpty(userName) || !Tools.isMobileNum(userName)) {
                    Tools.toastMsg(LoginByMsgActivity.this, "请输入正确的手机号码!");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Tools.toastMsg(LoginByMsgActivity.this, "验证码不能为空!");
                    return;
                }
                login();
                break;
            case R.id.fl_back:
                //短信验证登录
                startActivity(new Intent(LoginByMsgActivity.this, LoginByMsgActivity.class));
                finish();
                break;
            case R.id.txt_account_login:
                startActivity(new Intent(LoginByMsgActivity.this, LoginActivity.class));
                finish();
                break;
            case R.id.txt_get_code:
                userName = edtUserName.getText().toString();
                if (TextUtils.isEmpty(userName)) {
                    Tools.toastMsg(LoginByMsgActivity.this, "请输入正确的手机号码!");
                    return;
                }
                getCode();
                break;

        }
    }

    private void getCode() {

        Map<String, String> params = new HashMap<>();
        Map<String, String> loginInfo = new HashMap<>();
        loginInfo.put("Phone", "");
        loginInfo.put("Code", "");
        params.put("Phone", userName);
        params.put("TypeID", "1");
        PostTools.postDataBySoap(this, "SendMsgCode", loginInfo, params, handler, 1);

    }

    private Bean_LoginIfno loginIfno;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String) msg.obj;
            Tools.debug("result" + result);
            if (TextUtils.isEmpty(result)) {
                Tools.toastMsg(LoginByMsgActivity.this, "请检查网络后重试");
                return;
            }

            if (msg.what == 1) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (TextUtils.equals(object.getString("Result"), "1")) {
                        new CountDownTimer(60 * 1000, 1000) {

                            @Override
                            public void onTick(long millisUntilFinished) {
                                txtGetCode.setText(millisUntilFinished / 1000 + "秒后重试");
                            }

                            @Override
                            public void onFinish() {
                                txtGetCode.setText("重新获取");
                            }
                        }.start();
                    }
                    Tools.toastMsg(LoginByMsgActivity.this, object.getString("Message"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return;
            }

            loginIfno = new Gson().fromJson(result, Bean_LoginIfno.class);
            if (loginIfno != null && TextUtils.equals("1", loginIfno.Result) && loginIfno.Data != null) {
                AppPrefrence.setIsMsg(LoginByMsgActivity.this, true);
                AppPrefrence.setIsLogin(LoginByMsgActivity.this, true);
                AppPrefrence.setUserPhone(LoginByMsgActivity.this, userName);
                if (!TextUtils.equals(loginIfno.Data.get(0).LoginType, "1")) {
                    AppPrefrence.setIsUser(LoginByMsgActivity.this, true);

                    if (loginIfno.Data.size() > 2) {
                        AppPrefrence.setAccountCount(LoginByMsgActivity.this, loginIfno.Data.size() - 1);
                        AppPrefrence.setIsLogin(LoginByMsgActivity.this, false);
                        startActivity(new Intent(LoginByMsgActivity.this, SelectAccount.class).putExtra("info", result).putExtra("isHome", true));

                    } else {
                        AppPrefrence.setAccountCount(LoginByMsgActivity.this, 1);
                        AppPrefrence.setUserPwd(LoginByMsgActivity.this, password);
                        AppPrefrence.setToken(LoginByMsgActivity.this, loginIfno.Data.get(0).Token);
                        AppPrefrence.setUsercode(LoginByMsgActivity.this, loginIfno.Data.get(1).UserCode);
                        AppPrefrence.setDoorno(LoginByMsgActivity.this, loginIfno.Data.get(1).Doorplate);
                        AppPrefrence.setRealName(LoginByMsgActivity.this, loginIfno.Data.get(1).UserName);
                        AppPrefrence.setUserPhone(LoginByMsgActivity.this, loginIfno.Data.get(1).UserCode);
                        AppPrefrence.setUserAdd(LoginByMsgActivity.this, loginIfno.Data.get(1).Address);
                        AppPrefrence.setUserReadDate(LoginByMsgActivity.this, loginIfno.Data.get(1).LastReadDate);
                        AppPrefrence.setUserReadNo(LoginByMsgActivity.this, loginIfno.Data.get(1).LastReadNumber);
                        AppPrefrence.setUserReserve(LoginByMsgActivity.this, loginIfno.Data.get(1).Reserve);
                        AppPrefrence.setMeterNo(LoginByMsgActivity.this, loginIfno.Data.get(1).MeterAddr);
                        AppPrefrence.setPhone(LoginByMsgActivity.this,loginIfno.Data.get(1).Phone);
                        startActivity(new Intent(LoginByMsgActivity.this, Home.class));
                    }
                } else {
                    AppPrefrence.setIsUser(LoginByMsgActivity.this, false);
                    AppPrefrence.setToken(LoginByMsgActivity.this, loginIfno.Data.get(0).Token);
                    AppPrefrence.setUsercode(LoginByMsgActivity.this, loginIfno.Data.get(0).OperatorCode);
                    AppPrefrence.setUserName(LoginByMsgActivity.this, loginIfno.Data.get(0).OperatorName);
                    startActivity(new Intent(LoginByMsgActivity.this, ManagerHome.class));
                }
                XGPushManager.registerPush(getApplicationContext(), AppPrefrence.getUsercode(LoginByMsgActivity.this));
                finish();
            } else {
                Tools.toastMsg(LoginByMsgActivity.this, loginIfno.Message);
            }
            SVProgressHUD.dismiss(LoginByMsgActivity.this);
        }
    };

    private void login() {
        SVProgressHUD.showWithStatus(this, "登录中...", SVProgressHUD.SVProgressHUDMaskType.Clear);

        Map<String, String> params = new HashMap<>();
        Map<String, String> loginInfo = new HashMap<>();
//        loginInfo.put("Pwd", Tools.get32MD5Str(password));
        loginInfo.put("Pwd", password);
        loginInfo.put("Code", userName);
        PostTools.postDataBySoap(this, "Login", loginInfo, params, handler, 0);
    }


}
