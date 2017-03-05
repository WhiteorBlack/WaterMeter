package com.android.blm.ymxn.activity;/**
 * Created by Administrator on 2016/6/5.
 */

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.blm.ymxn.BaseActivity;
import com.android.blm.ymxn.R;
import com.android.blm.ymxn.utils.AppPrefrence;
import com.android.blm.ymxn.utils.PostTools;
import com.android.blm.ymxn.utils.Tools;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * author:${白曌勇} on 2016/6/5
 * TODO:管理员修改密码
 */
public class ChangePassword extends BaseActivity {
    //    private TextInputLayout inputOldPwd, inputNewPwd, inputCheckPwd;
    private EditText edtOldPwd, edtNewPwd, edtCheckPwd, edtCode;
    private TextView txtGetCode;
    private String oldPwd, newPwd, checkPwd, phoneString;
    private String type = "1";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_pwd);
//        type = getIntent().getStringExtra("type");
        initView();
    }

    private void initView() {
        txtGetCode = (TextView) findViewById(R.id.txt_get_code);

        edtCheckPwd = (EditText) findViewById(R.id.edi_new_check_pwd);
        edtNewPwd = (EditText) findViewById(R.id.edi_new_pwd);
        edtOldPwd = (EditText) findViewById(R.id.edi_old_pwd);
        edtCode = (EditText) findViewById(R.id.edit_code);

    }

    @Override
    public void waterClick(View v) {
        super.waterClick(v);
        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;
            case R.id.btn_confirm:
                oldPwd = edtCode.getText().toString();
                newPwd = edtNewPwd.getText().toString();
                checkPwd = edtCheckPwd.getText().toString();
                if (TextUtils.isEmpty(oldPwd)) {
                    Tools.toastMsg(ChangePassword.this, "旧密码不能为空!");
                    return;
                }

                if (TextUtils.isEmpty(newPwd)) {
                    Tools.toastMsg(ChangePassword.this, "新密码不能为空!");
                    return;
                }
                if (TextUtils.isEmpty(checkPwd)) {
                    Tools.toastMsg(ChangePassword.this, "确认密码不能为空!");
                    return;
                }
                if (!TextUtils.equals(newPwd, checkPwd)) {
                    Tools.toastMsg(ChangePassword.this, "两次输入密码不一致!");
                    return;
                }
                changePwd();
                break;
            case R.id.txt_get_code:
                phoneString = edtOldPwd.getText().toString();
                if (TextUtils.isEmpty(phoneString) || !Tools.isMobileNum(phoneString)) {
                    Tools.toastMsg(ChangePassword.this, "请输入正确的手机号码!");
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
        params.put("Phone", phoneString);
        PostTools.postDataBySoap(this, "SendMsgCode", loginInfo, params, handler, 1);

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String) msg.obj;
            Tools.debug("modefy" + result);
            if (TextUtils.isEmpty(result)) {
                Tools.toastMsg(ChangePassword.this, "请检查网络后重试");
            } else {
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
                        } Tools.toastMsg(ChangePassword.this, object.getString("Message"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }
                try {
                    JSONObject object = new JSONObject(result);
                    if (TextUtils.equals(object.getString("Result"), "1")) {
                        Tools.toastMsg(ChangePassword.this, "密码修改成功");
                        AppPrefrence.setUserPwd(ChangePassword.this, Tools.get32MD5Str(newPwd));
                        finish();
                    } else {
                        Tools.toastMsg(ChangePassword.this, object.getString("Message"));
                    }
                } catch (Exception e) {
                }
            }
        }
    };

    private void changePwd() {
        Map<String, String> loginParams = new HashMap<>();
        loginParams.put("Code", AppPrefrence.getUsercode(this));
        loginParams.put("Token", AppPrefrence.getToken(this));
        Map<String, String> params = new HashMap<>();
        params.put("OldPwd", Tools.get32MD5Str(oldPwd));
        params.put("NewPwd", Tools.get32MD5Str(newPwd));
        params.put("LoginType", type);
        PostTools.postDataBySoap(this, "ModifyPwd", loginParams, params, handler, 0);
    }
}
