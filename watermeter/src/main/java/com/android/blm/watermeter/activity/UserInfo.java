package com.android.blm.watermeter.activity;/**
 * Created by Administrator on 2016/5/31.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.android.blm.watermeter.BaseActivity;
import com.android.blm.watermeter.R;
import com.android.blm.watermeter.utils.AppPrefrence;

/**
 * author:${白曌勇} on 2016/5/31
 * TODO:
 */
public class UserInfo extends BaseActivity {
    private TextView txtCode, txtAdd, txtDoorNo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info);
        initView();
    }

    private void initView() {
        txtAdd = (TextView) findViewById(R.id.txt_detial_address);
        txtAdd.setText(AppPrefrence.getUserAdd(this));

        txtCode = (TextView) findViewById(R.id.txt_user_name);
        txtCode.setText(AppPrefrence.getUsercode(this));

        txtDoorNo = (TextView) findViewById(R.id.txt_house_no);
        txtDoorNo.setText(AppPrefrence.getDoorNo(this));
    }

    @Override
    public void waterClick(View v) {
        super.waterClick(v);
        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;
            case R.id.fl_edit_user_info:

                break;
        }
    }
}
