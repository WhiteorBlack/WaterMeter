package com.android.blm.watermeter.activity;/**
 * Created by Administrator on 2016/6/4.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.android.blm.watermeter.BaseActivity;
import com.android.blm.watermeter.R;

/**
 * author:${白曌勇} on 2016/6/4
 * TODO:
 */
public class SystemMsgDetial extends BaseActivity {

    private TextView txtTitle, txtDate, txtContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system_msg_detial);
        initView();
        setData();
    }

    private void setData() {
        Bundle bundle = getIntent().getBundleExtra("detial");
        txtContent.setText(bundle.getString("content"));
        txtDate.setText(bundle.getString("date"));
        txtTitle.setText(bundle.getString("title"));
    }

    private void initView() {
        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtDate = (TextView) findViewById(R.id.txt_date);
        txtContent = (TextView) findViewById(R.id.txt_content);
    }

    @Override
    public void waterClick(View v) {
        super.waterClick(v);
        finish();
    }
}
