package com.android.blm.watermeter;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.blm.watermeter.activity.ChangePasswordUser;
import com.android.blm.watermeter.activity.ConsumeRecordNew;
import com.android.blm.watermeter.activity.RechargeOnline;
import com.android.blm.watermeter.activity.RechargeRecord;
import com.android.blm.watermeter.activity.SystemMessage;
import com.android.blm.watermeter.activity.UserInfo;
import com.android.blm.watermeter.login.LoginActivity;
import com.android.blm.watermeter.login.SelectAccount;
import com.android.blm.watermeter.utils.AppPrefrence;
import com.android.blm.watermeter.utils.Tools;
import com.tencent.android.tpush.XGPushManager;

/**
 * Created by Administrator on 2016/5/24.
 */
public class Home extends BaseActivity {

    private PopupWindow homePop;
    private View popView;
    private FrameLayout flSwitch, editUser;

    private TextView txtName, txtMoney, txtCode, txtDate, txtNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        txtNo.setText(AppPrefrence.getUserReadNo(this));
        txtName.setText(AppPrefrence.getRealName(this));
        txtMoney.setText(AppPrefrence.getUserReserve(this) + "");
        txtDate.setText(AppPrefrence.getUserReadDate(this));
        txtCode.setText(AppPrefrence.getMeterNo(this));
    }

    private void initView() {
        editUser = (FrameLayout) findViewById(R.id.fl_edit_user_info);
        popView = LayoutInflater.from(this).inflate(R.layout.home_pop, null);
        flSwitch = (FrameLayout) popView.findViewById(R.id.fl_switch_user);
        if (AppPrefrence.getAccountCount(this) > 1) {
            flSwitch.setVisibility(View.VISIBLE);
        } else flSwitch.setVisibility(View.GONE);
        homePop = new PopupWindow(popView, FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, true);
        homePop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        homePop.setOutsideTouchable(true);

        txtCode = (TextView) findViewById(R.id.txt_form_no);
        txtDate = (TextView) findViewById(R.id.txt_last_check_time);
        txtMoney = (TextView) findViewById(R.id.txt_current_money);
        txtName = (TextView) findViewById(R.id.txt_user_name);
        txtNo = (TextView) findViewById(R.id.txt_last_check_no);
    }

    @Override
    public void waterClick(View v) {
        super.waterClick(v);
        switch (v.getId()) {
            case R.id.fl_back:
                Home.this.finish();
                break;
            case R.id.fl_edit_user_info:
                if (homePop != null) {
                    homePop.showAsDropDown(editUser, -Tools.dip2px(Home.this, 6), -Tools.dip2px(Home.this, 6));
                }
                break;
            case R.id.fl_msg:
                startActivity(new Intent(Home.this, SystemMessage.class).putExtra("type", "2"));
                break;
            case R.id.fl_consume_record:
                startActivity(new Intent(Home.this, ConsumeRecordNew.class));
                break;
            case R.id.fl_recharge_record:
                startActivity(new Intent(Home.this, RechargeRecord.class));
                break;
            case R.id.fl_recharge_online:
                startActivityForResult(new Intent(Home.this, RechargeOnline.class), 0);
                break;
            case R.id.rl_user_info:
                startActivity(new Intent(Home.this, UserInfo.class));
                break;
            case R.id.fl_change_pwd:
                startActivity(new Intent(Home.this, ChangePasswordUser.class).putExtra("type", "2"));
                break;
            case R.id.fl_switch_user:
                startActivityForResult((new Intent(Home.this, SelectAccount.class).putExtra("isHome", false)), 0);
                break;
            case R.id.fl_logout:
                logout();
                break;
        }
    }


    private void logout() {
        AppPrefrence.setAccountCount(this, 1);
        AppPrefrence.setUserReadDate(this, "");
        AppPrefrence.setUserAdd(this, "");
        AppPrefrence.setRealName(this, "");
        AppPrefrence.setDoorno(this, "");
        AppPrefrence.setToken(this, "");
        AppPrefrence.setIsLogin(this, false);
        AppPrefrence.setUserName(this, "");
        AppPrefrence.setUserReadNo(this, "");
        AppPrefrence.setUserReserve(this, 0.00f);
        startActivity(new Intent(Home.this, LoginActivity.class).putExtra("userCode", AppPrefrence.getUserPhone(this)));
        AppPrefrence.setUsercode(this, "");
        AppPrefrence.setUserPhone(this, "");
        XGPushManager.registerPush(this.getApplicationContext(),"*");
        finish();
    }
}
