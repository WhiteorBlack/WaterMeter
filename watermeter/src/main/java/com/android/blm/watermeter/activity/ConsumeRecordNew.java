package com.android.blm.watermeter.activity;/**
 * Created by Administrator on 2016/6/1.
 */

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.blm.watermeter.BaseActivity;
import com.android.blm.watermeter.R;
import com.android.blm.watermeter.adapter.ConsumeRecordAdapter;
import com.android.blm.watermeter.bean.Bean_ConsumeRecord;
import com.android.blm.watermeter.dialog.CalendarPopNew;
import com.android.blm.watermeter.utils.AppPrefrence;
import com.android.blm.watermeter.utils.PostTools;
import com.android.blm.watermeter.utils.Tools;
import com.android.blm.watermeter.widget.loading.SVProgressHUD;
import com.android.blm.watermeter.widget.silkcalendar.SimpleMonthAdapter;
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
public class ConsumeRecordNew extends BaseActivity {
    private RecyclerView recordView;
    private List<Bean_ConsumeRecord.ConsumeRecord> recordList;
    private TextView txtStartTime, txtEndTime, txtDate, txtToatleFee;
    private ConsumeRecordAdapter recordAdapter;
    private String startDate, endDate;
    private CalendarPopNew startPop, endPop;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consume_record_new);
        initView();
        initDate(DateTime.now());
    }

    private void initDate(DateTime dt) {
        DateTime begin = dt;
        startDate = begin.dayOfMonth().withMinimumValue().toString("yyyy-MM-dd");
        endDate = begin.toString("yyyy-MM-dd");
        endDay = begin.getDayOfMonth();
        txtEndTime.setText(endDate);
//        startDate = begin.minusDays(7).toString("yyyy-MM-dd");
        txtStartTime.setText(startDate);
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
//            recordView.removeAllViews();
            recordAdapter.notifyDataSetChanged();
            txtToatleFee.setText("总计: ¥" + "0.0");
            if (TextUtils.isEmpty(result)) {
                Tools.toastMsg(ConsumeRecordNew.this, "请检查网络后重试");
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
                    Tools.toastMsg(ConsumeRecordNew.this, bean_ConsumeRecord.Message);
                }
            }
            SVProgressHUD.dismiss(ConsumeRecordNew.this);
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

        txtEndTime = (TextView) findViewById(R.id.txt_end_date);
        txtStartTime = (TextView) findViewById(R.id.txt_start_date);

        recordList = new ArrayList<>();
        recordAdapter = new ConsumeRecordAdapter(this, recordList);
        recordAdapter.setOnItemClickListener(new ConsumeRecordAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                startActivity(new Intent(ConsumeRecordNew.this, ConsumeDetial.class).putExtra("id", recordList.get(position).OperID));
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

    }

    private DateTime sDate = null, eDate = null;
    private String startDateString, endDateString;
    private int startYear = 0, startMonth = 0, startDay = 0, endYear = 0, endMonth = 0, endDay = 0;

    @Override
    public void waterClick(View v) {
        super.waterClick(v);
        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;
            case R.id.ll_start_time:
                if (startPop == null) {
                    startPop = new CalendarPopNew(this);
                    startPop.setSelectDay(new SimpleMonthAdapter.CalendarDay(DateTime.now().getYear(), DateTime.now().getMonthOfYear() - 1, 1));
                }

                startPop.setOnDateSelect(new CalendarPopNew.OnDateSelect() {
                    @Override
                    public void onDateSelect(Date selectedStartDate, Date selectedEndDate, Date downDate) {

                    }

                    @Override
                    public void onDateSelected(String date, int year, int monty, int day) {
                        startDateString = date;
                        startDay = day;
                        startMonth = monty;
                        startYear = year;
                        if (TextUtils.isEmpty(endDateString)) {
                            if (Tools.dateStringToLong(startDateString) > Tools.dateStringToLong(DateTime.now().toString("yyyy-MM-dd"))) {
                                if (startYear > 0)
                                    startPop.setSelectDay(new SimpleMonthAdapter.CalendarDay(Integer.parseInt(startDate.split("-")[0]), Integer.parseInt(startDate.split("-")[1]) - 1, Integer.parseInt(startDate.split("-")[2])));
                                Tools.toastMsg(ConsumeRecordNew.this, "开始时间不能大于结束时间");
                                return;
                            } else {
                                startDate = date;
                                txtStartTime.setText(startDate);
                                startPop.setSelectDay(null);
                                startPop.dismiss();
                            }

                        } else {
                            if (Tools.dateStringToLong(startDateString) > Tools.dateStringToLong(endDateString)) {
                                if (startYear > 0)
                                    startPop.setSelectDay(new SimpleMonthAdapter.CalendarDay(Integer.parseInt(startDate.split("-")[0]), Integer.parseInt(startDate.split("-")[1]) - 1, Integer.parseInt(startDate.split("-")[2])));
                                Tools.toastMsg(ConsumeRecordNew.this, "开始时间不能大于结束时间");
                                return;
                            } else {
                                startDate = date;
                                txtStartTime.setText(startDate);
                                startPop.setSelectDay(null);
                                startPop.dismiss();
                            }
                        }
                        getConsumeData();
                    }
                });
                startPop.shopPop(txtStartTime);
                break;
            case R.id.ll_end_time:
                if (endPop == null) {
                    endPop = new CalendarPopNew(this);
                    endPop.setSelectDay(new SimpleMonthAdapter.CalendarDay(DateTime.now().getYear(), DateTime.now().getMonthOfYear() - 1, DateTime.now().getDayOfMonth()));
                }

                endPop.setOnDateSelect(new CalendarPopNew.OnDateSelect() {
                    @Override
                    public void onDateSelect(Date selectedStartDate, Date selectedEndDate, Date downDate) {
                    }

                    @Override
                    public void onDateSelected(String date, int year, int monty, int day) {
                        endDateString = date;
                        endDay = day;
                        endMonth = monty;
                        endYear = year;
                        if (TextUtils.isEmpty(startDateString)) {
                            if (Tools.dateStringToLong(endDateString) > Tools.dateStringToLong(DateTime.now().toString("yyyy-MM-dd"))) {
                                endDate = date;
                                endPop.setSelectDay(null);
                                txtEndTime.setText(endDate);
                                endPop.dismiss();
                            } else {
                                if (endYear > 0)
                                    endPop.setSelectDay(new SimpleMonthAdapter.CalendarDay(Integer.parseInt(endDate.split("-")[0]), Integer.parseInt(endDate.split("-")[1]) - 1, Integer.parseInt(endDate.split("-")[2])));
                                Tools.toastMsg(ConsumeRecordNew.this, "结束时间不能小于开始时间");
                                return;
                            }
                        } else {
                            if (Tools.dateStringToLong(startDateString) > Tools.dateStringToLong(endDateString)){
                                if (endYear > 0)
                                    endPop.setSelectDay(new SimpleMonthAdapter.CalendarDay(Integer.parseInt(endDate.split("-")[0]), Integer.parseInt(endDate.split("-")[1]) - 1, Integer.parseInt(endDate.split("-")[2])));
                                Tools.toastMsg(ConsumeRecordNew.this, "结束时间不能小于开始时间");
                                return;
                            }else {
                                endDate = date;
                                endPop.setSelectDay(null);
                                txtEndTime.setText(endDate);
                                endPop.dismiss();
                            }
                        }
                        getConsumeData();
                    }
                });
                endPop.shopPop(txtEndTime);
                break;
        }
    }

}
