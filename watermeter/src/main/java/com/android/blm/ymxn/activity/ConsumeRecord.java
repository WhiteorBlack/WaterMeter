package com.android.blm.ymxn.activity;/**
 * Created by Administrator on 2016/6/1.
 */

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.blm.ymxn.BaseActivity;
import com.android.blm.ymxn.R;
import com.android.blm.ymxn.adapter.ConsumeRecordAdapter;
import com.android.blm.ymxn.adapter.DaysAdapter;
import com.android.blm.ymxn.adapter.WeekAdapter;
import com.android.blm.ymxn.bean.Bean_ConsumeRecord;
import com.android.blm.ymxn.bean.Bean_Days;
import com.android.blm.ymxn.utils.AppPrefrence;
import com.android.blm.ymxn.utils.PostTools;
import com.android.blm.ymxn.utils.Tools;
import com.android.blm.ymxn.widget.CalendarScrollView;
import com.android.blm.ymxn.widget.CalendarView;
import com.android.blm.ymxn.widget.LinearLayoutForListView;
import com.android.blm.ymxn.widget.loading.SVProgressHUD;
import com.google.gson.Gson;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author:${白曌勇} on 2016/6/1
 * TODO:
 */
public class ConsumeRecord extends BaseActivity implements CalendarView.OnItemClickListener, CalendarScrollView.OnScrollListener {
    private RecyclerView recordView;
    private List<Bean_ConsumeRecord.ConsumeRecord> recordList;
    private TextView txtDate, txtToatleFee;
    private ConsumeRecordAdapter recordAdapter;
    private RecyclerView dateView;
    private LinearLayoutForListView weekView;
    private String[] weeks = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
    private LinearLayout llDateParent;
    private CalendarScrollView calendarScrollView;
    private CalendarView calendarView;
    private float calendarHeight;
    private List<Bean_Days> dayList;
    private DaysAdapter dayAdapter;
    private String startDate, endDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consume_record);
        initView();
        initDate(DateTime.now());

    }

    private int currentIndex;

    private void initDate(DateTime dt) {
        dayList.clear();
        DateTime begin = dt;
        startDate = begin.toString("yyyy-MM-dd");

        Bean_Days today = new Bean_Days();
        today.day = begin.dayOfMonth().getAsString();
        today.isSelect = true;
        today.type = "今天";
        dayList.add(today);
        currentIndex = begin.getDayOfWeek();
        if (currentIndex == 7) {
            currentIndex = 0;
        }
        if (currentIndex == 6) {
            for (int i = 1; i < 7; i++) {
                Bean_Days day = new Bean_Days();
                day.day = begin.minusDays(i).dayOfMonth().getAsString();
                day.isSelect = false;
                day.type = "";
                dayList.add(0, day);
            }
        } else {
            for (int i = 1; i < 7; i++) {
                if (i < currentIndex) {
                    Bean_Days day = new Bean_Days();
                    day.day = begin.minusDays(i).dayOfMonth().getAsString();
                    day.type = "";
                    day.isSelect = false;
                    dayList.add(0, day);
                } else {
                    Bean_Days day = new Bean_Days();
                    day.day = begin.plusDays(i - currentIndex).dayOfMonth().getAsString();
                    day.type = "";
                    day.isSelect = false;
                    dayList.add(day);
                }
            }
        }
        dayList.get(0).type = "开始";
        dayList.get(0).isSelect = true;
        dayList.get(6).type = "结束";
        dayList.get(6).isSelect = true;
        dayAdapter.notifyItemRangeChanged(0, dayList.size());
        startDate = begin.minusDays(currentIndex).toString("yyyy-MM-dd");
        endDate = begin.plusDays(6 - currentIndex).toString("yyyy-MM-dd");
        Tools.debug("start" + startDate + "---end" + endDate);
        int line = begin.getDayOfMonth() / 7;
        calendarView.initData(7 * line, 7 * line + 6);
        getConsumeData();
    }

    Bean_ConsumeRecord bean_ConsumeRecord;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String) msg.obj;
            Tools.debug("consume" + result);
            recordList.clear();
            recordView.removeAllViews();
            txtToatleFee.setText("总计: ¥" + "0.0");
            if (TextUtils.isEmpty(result)) {
                Tools.toastMsg(ConsumeRecord.this, "请检查网络后重试");
            } else {
                bean_ConsumeRecord = new Gson().fromJson(result, Bean_ConsumeRecord.class);
                if (bean_ConsumeRecord != null && TextUtils.equals(bean_ConsumeRecord.Result, "1")) {
                    if (bean_ConsumeRecord.Data != null && bean_ConsumeRecord.Data.size() > 0)
                        recordList.addAll(bean_ConsumeRecord.Data);
                    float totlaFee = 0.0f;
                    for (int i = 0; i < recordList.size(); i++) {
                        totlaFee += recordList.get(i).FareMoney;
                    }
                    txtToatleFee.setText("¥" + totlaFee);
                    recordAdapter.notifyItemRangeChanged(0, recordList.size());
                } else {
                    Tools.toastMsg(ConsumeRecord.this, bean_ConsumeRecord.Message);
                }
            }
            SVProgressHUD.dismiss(ConsumeRecord.this);
        }
    };

    private void getConsumeData() {
        SVProgressHUD.showWithStatus(this, "加载中...", SVProgressHUD.SVProgressHUDMaskType.Clear);
        Map<String, String> loginParams = new HashMap<>();
        Map<String, String> params = new HashMap<>();
        loginParams.put("Code", AppPrefrence.getUserPhone(this));
        loginParams.put("Token", AppPrefrence.getToken(this));
        params.put("UserCode", AppPrefrence.getUsercode(this));
        params.put("BeginDate", startDate);
        params.put("EndDate", endDate);
        PostTools.postDataBySoap(this, "GetConsumeList", loginParams, params, handler, 0);
    }

    private void initView() {
        calendarHeight = Tools.getScreenHeight(this) * 2 / 5;
        recordList = new ArrayList<>();
        dayList = new ArrayList<>();
        dayAdapter = new DaysAdapter(this, dayList);
        recordAdapter = new ConsumeRecordAdapter(this, recordList);
        recordAdapter.setOnItemClickListener(new ConsumeRecordAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                startActivity(new Intent(ConsumeRecord.this, ConsumeDetial.class).putExtra("id", recordList.get(position).OperID));
            }

            @Override
            public void onItemLongClick(View v, int position) {

            }
        });
        recordView = (RecyclerView) findViewById(R.id.recyclerView_consume_record);
        recordView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recordView.setItemAnimator(new DefaultItemAnimator());
        recordView.setAdapter(recordAdapter);
        txtDate = (TextView) findViewById(R.id.txt_date);
        txtDate.setText(Tools.getTodayData());
        txtToatleFee = (TextView) findViewById(R.id.txt_total_money);

        weekView = (LinearLayoutForListView) findViewById(R.id.recyclerView_week);
        weekView.setAdapter(new WeekAdapter(weeks, this));

        dateView = (RecyclerView) findViewById(R.id.recyclerView_date);
        dateView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        dateView.setAdapter(dayAdapter);

        llDateParent = (LinearLayout) findViewById(R.id.ll_date_bar);
        calendarScrollView = (CalendarScrollView) findViewById(R.id.calendarScrollView);
        calendarScrollView.setOnScrollListener(this);
        calendarScrollView.setVisibility(View.INVISIBLE);
        calendarView = (CalendarView) findViewById(R.id.calendar);
        calendarView.setSelectMore(true);
        calendarView.setOnItemClickListener(this);
        new CountDownTimer(200, 200) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                calendarScrollView.initView();
                calendarScrollView.setVisibility(View.VISIBLE);
            }
        }.start();

    }

    @Override
    public void waterClick(View v) {
        super.waterClick(v);
        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;

        }
    }

    @Override
    public void OnItemClick(Date selectedStartDate, Date selectedEndDate, Date downDate) {
        calendarScrollView.collospView();
        DateTime startTime = new DateTime(selectedStartDate);
        DateTime endTime = new DateTime(selectedEndDate);
        getConsumeData();
        setDate(startTime, endTime);
    }

    private void setDate(DateTime star, DateTime end) {
        dayList.clear();
        dateView.removeAllViews();
        DateTime begin = star;
        DateTime endTime = end;
        int index = begin.getDayOfWeek();
        if (index == 7) {
            index = 0;
        }

        Bean_Days dayS = new Bean_Days();
        dayS.day = begin.dayOfMonth().getAsString();
        dayS.type = "开始";
        dayS.isSelect = true;
        dayList.add(dayS);

        if (index == 6) {
            for (int i = 1; i < 7; i++) {
                Bean_Days day = new Bean_Days();
                day.day = begin.minusDays(i).dayOfMonth().getAsString();
                day.isSelect = false;
                day.type = "";
                dayList.add(0, day);
            }
        } else {
            for (int i = 1; i < 7; i++) {
                if (i < index || i == index) {
                    Bean_Days day = new Bean_Days();
                    day.day = begin.minusDays(i).dayOfMonth().getAsString();
                    day.type = "";
                    day.isSelect = false;
                    dayList.add(0, day);
                } else {
                    Bean_Days day = new Bean_Days();
                    day.day = begin.plusDays(i - index).dayOfMonth().getAsString();
                    day.type = "";
                    day.isSelect = false;
                    dayList.add(day);
                }
                if (TextUtils.equals(dayList.get(i).day, endTime.dayOfMonth().getAsString())) {
                    dayList.get(i).isSelect = true;
                    dayList.get(i).type = "结束";
                }
            }
        }

        dayAdapter.notifyItemRangeChanged(0, dayList.size());
    }

    @Override
    public void onScroll(float scrollY, boolean bottomToTop) {
        LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) llDateParent.getLayoutParams();
        param.height = (int) (Math.abs(scrollY) * Tools.dip2px(ConsumeRecord.this, 89) / calendarHeight);
        llDateParent.setLayoutParams(param);
    }
}
