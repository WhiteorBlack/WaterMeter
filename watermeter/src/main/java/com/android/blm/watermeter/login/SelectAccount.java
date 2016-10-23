package com.android.blm.watermeter.login;/**
 * Created by Administrator on 2016/5/25.
 */

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.android.blm.watermeter.BaseActivity;
import com.android.blm.watermeter.Home;
import com.android.blm.watermeter.R;
import com.android.blm.watermeter.adapter.SelectAccountAdapter;
import com.android.blm.watermeter.adapter.SelectAccountAdapter.OnItemClickListener;
import com.android.blm.watermeter.bean.Bean_LoginIfno;
import com.android.blm.watermeter.utils.AppPrefrence;
import com.android.blm.watermeter.utils.PostTools;
import com.android.blm.watermeter.utils.Tools;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author:${白曌勇} on 2016/5/25
 * TODO: 选择账号
 */
public class SelectAccount extends BaseActivity {
    private RecyclerView recyAccount;
    private List<Bean_LoginIfno.LoginData> accountList;
    private Toolbar toolBar;
    private SelectAccountAdapter accountAdapter;
    private boolean isHome = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_account);
        isHome = getIntent().getBooleanExtra("isHome", false);
        initView();
        if (isHome)
            getData();
        else getUserData();
    }

    private Bean_LoginIfno bean_loginIfno;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String) msg.obj;
            Tools.debug("result" + result);
            if (TextUtils.isEmpty(result)) {
                return;
            }
            bean_loginIfno = new Gson().fromJson(result, Bean_LoginIfno.class);
            if (bean_loginIfno != null && TextUtils.equals("1", bean_loginIfno.Result) && bean_loginIfno.Data != null) {

                if (bean_loginIfno.Data.size() > 1) {
                    AppPrefrence.setAccountCount(SelectAccount.this, bean_loginIfno.Data.size());
                    accountList.addAll(bean_loginIfno.Data);
                    AppPrefrence.setToken(SelectAccount.this, accountList.get(0).Token);
                    accountList.remove(0);
                    accountAdapter.notifyItemRangeChanged(0, accountList.size());
                }
            } else {
                Tools.toastMsg(SelectAccount.this, "请检查网络后重试");
            }
        }
    };

    private void getUserData() {
        Map<String, String> params = new HashMap<>();
        Map<String, String> loginInfo = new HashMap<>();
        loginInfo.put("Pwd", Tools.get32MD5Str(AppPrefrence.getUserPwd(this)));
        loginInfo.put("Code", AppPrefrence.getUserPhone(this));
        PostTools.postDataBySoap(this, "Login", loginInfo, params, handler, 0);
    }

    String result;

    private void getData() {

        result = getIntent().getStringExtra("info");
        bean_loginIfno = new Gson().fromJson(result, Bean_LoginIfno.class);
        accountList.addAll(bean_loginIfno.Data);
        accountList.remove(0);
        AppPrefrence.setAccountCount(this, accountList.size());
        accountAdapter.notifyItemRangeChanged(0, accountList.size());
    }

    private void initView() {
        accountList = new ArrayList<>();
        accountAdapter = new SelectAccountAdapter(this, accountList);
        accountAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Bean_LoginIfno.LoginData loginIfno = accountList.get(position);
                AppPrefrence.setIsLogin(SelectAccount.this, true);
                AppPrefrence.setToken(SelectAccount.this, bean_loginIfno.Data.get(0).Token);
                AppPrefrence.setUsercode(SelectAccount.this, loginIfno.UserCode);
                AppPrefrence.setDoorno(SelectAccount.this, loginIfno.Doorplate);
                AppPrefrence.setRealName(SelectAccount.this, loginIfno.UserName);
                AppPrefrence.setUserAdd(SelectAccount.this, loginIfno.Address);
                AppPrefrence.setUserReadDate(SelectAccount.this, loginIfno.LastReadDate);
                AppPrefrence.setUserReadNo(SelectAccount.this, loginIfno.LastReadNumber);
                AppPrefrence.setUserReserve(SelectAccount.this, loginIfno.Reserve);
                AppPrefrence.setMeterNo(SelectAccount.this, loginIfno.MeterAddr);
                startActivity(new Intent(SelectAccount.this, Home.class));
                finish();
            }

            @Override
            public void onItemLongClick(View v, int position) {

            }
        });
        toolBar = (Toolbar) findViewById(R.id.toolbar);
//        toolBar.setTitle("选择账号");
//        toolBar.setTitleTextColor(getResources().getColor(R.color.white));
        toolBar.setBackgroundColor(getResources().getColor(R.color.blue));
        setSupportActionBar(toolBar);

        recyAccount = (RecyclerView) findViewById(R.id.recyclerView);
        recyAccount.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyAccount.setItemAnimator(new DefaultItemAnimator());
        recyAccount.setAdapter(accountAdapter);
    }
}
