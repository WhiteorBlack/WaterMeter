package com.android.blm.ymxn;/**
 * Created by Administrator on 2016/6/13.
 */

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.PopupWindow;

import com.android.blm.ymxn.activity.ChangePassword;
import com.android.blm.ymxn.activity.SystemMessage;
import com.android.blm.ymxn.adapter.ManagerAdapter;
import com.android.blm.ymxn.bean.Bean_Manager;
import com.android.blm.ymxn.login.LoginActivity;
import com.android.blm.ymxn.manager.ContralValve;
import com.android.blm.ymxn.manager.CopyMeterNew;
import com.android.blm.ymxn.manager.OwnMoneyUsers;
import com.android.blm.ymxn.manager.SearchResult;
import com.android.blm.ymxn.manager.SystemWarningNew;
import com.android.blm.ymxn.utils.AppPrefrence;
import com.android.blm.ymxn.utils.Tools;
import com.tencent.android.tpush.XGPushManager;

import java.util.ArrayList;
import java.util.List;

/**
 * author:${白曌勇} on 2016/6/13
 * TODO:
 */
public class ManagerHome extends BaseActivity {
    private GridView manGrid;
    private List<Bean_Manager> manList;
    private ManagerAdapter managerAdapter;
    private PopupWindow homePop;
    private View popView;
    private FrameLayout flEdit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager_home);
        initView();
        initData();
    }

    private void initData() {
        manList.add(new Bean_Manager("欠费用户", R.mipmap.own_money));
        manList.add(new Bean_Manager("系统报警", R.mipmap.system_ring));
        manList.add(new Bean_Manager("控阀", R.mipmap.manager_water));
        manList.add(new Bean_Manager("抄表", R.mipmap.read_meter));
        manList.add(new Bean_Manager("消息通知", R.mipmap.message));
        managerAdapter.notifyDataSetChanged();
    }

    private void initView() {
        flEdit = (FrameLayout) findViewById(R.id.fl_edit);
        popView = LayoutInflater.from(this).inflate(R.layout.home_pop, null);
        popView.findViewById(R.id.fl_switch_user).setVisibility(View.GONE);
        homePop = new PopupWindow(popView, FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, true);
        homePop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        homePop.setOutsideTouchable(true);

        manList = new ArrayList<>();
        managerAdapter = new ManagerAdapter(manList, this);
        manGrid = (GridView) findViewById(R.id.grid_manager);
        manGrid.setAdapter(managerAdapter);
        manGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(ManagerHome.this, OwnMoneyUsers.class));
                        break;
                    case 1:
                        startActivity(new Intent(ManagerHome.this, SystemWarningNew.class));
                        break;
                    case 2:
                        startActivity(new Intent(ManagerHome.this, ContralValve.class));
                        break;
                    case 3:
                        startActivity(new Intent(ManagerHome.this, CopyMeterNew.class));
                        break;
                    case 4:
                        startActivity(new Intent(ManagerHome.this, SystemMessage.class).putExtra("type", "1"));
                        break;
                }
            }
        });
    }

    @Override
    public void waterClick(View v) {
        super.waterClick(v);
        switch (v.getId()) {
            case R.id.fl_edit:
                if (homePop != null) {
                    homePop.showAsDropDown(flEdit, -Tools.dip2px(ManagerHome.this, 6), -Tools.dip2px(ManagerHome.this, 6));
                }
                break;
            case R.id.ll_search:
                startActivity(new Intent(ManagerHome.this, SearchResult.class));
                break;
            case R.id.fl_change_pwd:
                startActivity(new Intent(ManagerHome.this, ChangePassword.class).putExtra("type", "1"));
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
        startActivity(new Intent(ManagerHome.this, LoginActivity.class).putExtra("userCode", AppPrefrence.getUserPhone(this)));
        AppPrefrence.setUsercode(this, "");
        AppPrefrence.setUserPhone(this, "");
        XGPushManager.registerPush(this.getApplicationContext(), "*");
        finish();
    }
}
