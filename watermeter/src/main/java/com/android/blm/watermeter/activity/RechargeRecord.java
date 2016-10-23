package com.android.blm.watermeter.activity;/**
 * Created by Administrator on 2016/6/6.
 */

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
import com.android.blm.watermeter.adapter.RechargeRecordAdapter;
import com.android.blm.watermeter.bean.Bean_RechargeRecord;
import com.android.blm.watermeter.dialog.CalendarPopNew;
import com.android.blm.watermeter.utils.AppPrefrence;
import com.android.blm.watermeter.utils.PostTools;
import com.android.blm.watermeter.utils.Tools;
import com.android.blm.watermeter.widget.loading.SVProgressHUD;
import com.android.blm.watermeter.widget.silkcalendar.SimpleMonthAdapter;
import com.android.blm.watermeter.widget.xrecycleview.XRecyclerView;
import com.google.gson.Gson;

import org.joda.time.DateTime;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author:${白曌勇} on 2016/6/6
 * TODO:
 */
public class RechargeRecord extends BaseActivity implements XRecyclerView.LoadingListener {

    private TextView txtStartTime, txtEndTime, txtTotalMoney;
    private XRecyclerView recordListView;
    private List<Bean_RechargeRecord.RechargeRecord> recordList;
    private RechargeRecordAdapter recordAdapter;
    private CalendarPopNew startPop, endPop;
    private String startDate, endDate;
    private int pageIndex = 1, pageSize = 20;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recharge_record);
        initView();
        initDate();
        SVProgressHUD.showWithStatus(this, "加载中...", SVProgressHUD.SVProgressHUDMaskType.Clear);
        getRecord();
    }

    private void initDate() {
        DateTime dt = DateTime.now();
        startDate = dt.dayOfMonth().withMinimumValue().toString("yyyy-MM-dd");
        endDate = dt.toString("yyyy-MM-dd");
        txtEndTime.setText(endDate);
        txtStartTime.setText(startDate);
    }

    float totlaMoney = 0.00f;
    Bean_RechargeRecord bean_RechargeRecord;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String) msg.obj;
            Tools.debug("record" + result);
            if (pageIndex == 1) {
                recordList.clear();
                totlaMoney = 0.00f;
            }
            if (TextUtils.isEmpty(result)) {
                Tools.toastMsg(RechargeRecord.this, "请检查网络后重试");
            } else {
                bean_RechargeRecord = new Gson().fromJson(result, Bean_RechargeRecord.class);
                if (bean_RechargeRecord != null && TextUtils.equals(bean_RechargeRecord.Result, "1") && bean_RechargeRecord.Data != null && bean_RechargeRecord.Data.size() > 0) {
                    recordList.addAll(bean_RechargeRecord.Data);
                    if (bean_RechargeRecord.Data.size() < pageSize)
                        recordListView.setLoadingMoreEnabled(false);
                    else recordListView.setLoadingMoreEnabled(true);
                    for (int i = 0; i < bean_RechargeRecord.Data.size(); i++) {
                        totlaMoney += bean_RechargeRecord.Data.get(i).PayMoney;
                    }
                    DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
                    String totalString = decimalFormat.format(totlaMoney);

                    txtTotalMoney.setText("总计: ¥" + totalString);
                } else recordListView.setLoadingMoreEnabled(false);
            }

//            recordListView.loadMoreComplete();
            recordAdapter.notifyDataSetChanged();
            SVProgressHUD.dismiss(RechargeRecord.this);
        }
    };

    private void getRecord() {

        Map<String, String> loginParam = new HashMap<>();
        Map<String, String> params = new HashMap<>();
        loginParam.put("Code", AppPrefrence.getUserPhone(this));
        loginParam.put("Token", AppPrefrence.getToken(this));
        params.put("UserCode", AppPrefrence.getUsercode(this));
        params.put("BeginDate", startDate);
        params.put("EndDate", endDate);
        params.put("PageSize", "" + pageSize);
        params.put("PageIndex", "" + pageIndex);
        PostTools.postDataBySoap(this, "GetPaymentList", loginParam, params, handler, 0);
    }

    private void initView() {
        recordList = new ArrayList<>();
        recordAdapter = new RechargeRecordAdapter(this, recordList);
        txtEndTime = (TextView) findViewById(R.id.txt_end_date);
        txtStartTime = (TextView) findViewById(R.id.txt_start_date);
        txtTotalMoney = (TextView) findViewById(R.id.txt_total_money);

        recordListView = (XRecyclerView) findViewById(R.id.recyclerView_recharge);
        recordListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recordListView.setItemAnimator(new DefaultItemAnimator());
        recordListView.setPullRefreshEnabled(false);
        recordListView.setLoadingMoreEnabled(true);
        recordListView.setLoadingListener(this);
        recordListView.setAdapter(recordAdapter);

        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);

        Calendar lastYear = Calendar.getInstance();
        lastYear.add(Calendar.YEAR, -1);

    }

    private DateTime sDate = null, eDate = null;
    private String startDateString, endDateString;
    private int startYear = 0, startMonth = 0, startDay = 0, endYear = 0, endMonth = 0, endDay = 0;

    @Override
    public void waterClick(View v) {
        super.waterClick(v);
        switch (v.getId()) {
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
                                Tools.toastMsg(RechargeRecord.this, "开始时间不能大于结束时间");
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
                                Tools.toastMsg(RechargeRecord.this, "开始时间不能大于结束时间");
                                return;
                            } else {
                                startDate = date;
                                txtStartTime.setText(startDate);
                                startPop.setSelectDay(null);
                                startPop.dismiss();
                            }
                        }
                        pageIndex = 1;
                        getRecord();
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
                                Tools.toastMsg(RechargeRecord.this, "结束时间不能小于开始时间");
                                return;
                            }
                        } else {
                            if (Tools.dateStringToLong(startDateString) > Tools.dateStringToLong(endDateString)){
                                if (endYear > 0)
                                    endPop.setSelectDay(new SimpleMonthAdapter.CalendarDay(Integer.parseInt(endDate.split("-")[0]), Integer.parseInt(endDate.split("-")[1]) - 1, Integer.parseInt(endDate.split("-")[2])));
                                Tools.toastMsg(RechargeRecord.this, "结束时间不能小于开始时间");
                                return;
                            }else {
                                endDate = date;
                                endPop.setSelectDay(null);
                                txtEndTime.setText(endDate);
                                endPop.dismiss();
                            }
                        }
                        pageIndex=1;
                        getRecord();
                    }
                });
                endPop.shopPop(txtEndTime);
                break;
            case R.id.fl_back:
                finish();
                break;
        }
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {
        pageIndex++;
        getRecord();
    }
}
