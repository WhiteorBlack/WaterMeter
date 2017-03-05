package com.android.blm.ymxn.manager;/**
 * Created by Administrator on 2016/6/19.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.android.blm.ymxn.BaseActivity;
import com.android.blm.ymxn.R;

/**
 * author:${白曌勇} on 2016/6/19
 * TODO:
 */
public class SystemWarningDetial extends BaseActivity {
    private TextView txtType, txtDate, txtContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system_warning_detial);
        initView();
        initData();
    }

    private void initData() {
        Bundle bundle ;
        bundle=getIntent().getBundleExtra("data");
        if (bundle != null) {
            txtType.setText("");
            txtDate.setText(bundle.getString("PublishTime"));
            txtContent.setText(bundle.getString("Content"));
        }
    }

    private void initView() {
        txtContent = (TextView) findViewById(R.id.txt_content);
        txtDate = (TextView) findViewById(R.id.txt_date);
        txtType = (TextView) findViewById(R.id.txt_title);
    }

    @Override
    public void waterClick(View v) {
        super.waterClick(v);
        finish();
    }
}
