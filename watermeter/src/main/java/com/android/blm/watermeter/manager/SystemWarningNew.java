package com.android.blm.watermeter.manager;/**
 * Created by Administrator on 2016/7/18.
 */

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.blm.watermeter.BaseActivity;
import com.android.blm.watermeter.R;
import com.android.blm.watermeter.adapter.SystemWarningNewAdapter;
import com.android.blm.watermeter.adapter.WarningTypeAdapter;
import com.android.blm.watermeter.bean.Bean_SystemWarning;
import com.android.blm.watermeter.bean.Bean_WarningType;
import com.android.blm.watermeter.db.DbManagerHelper;
import com.android.blm.watermeter.dialog.CalendarDialog;
import com.android.blm.watermeter.dialog.CalendarPopNew;
import com.android.blm.watermeter.fragment.SystemUnreadFragment;
import com.android.blm.watermeter.utils.AppPrefrence;
import com.android.blm.watermeter.utils.PostTools;
import com.android.blm.watermeter.utils.Tools;
import com.android.blm.watermeter.widget.LoadMoreRecyclerView;
import com.android.blm.watermeter.widget.loading.SVProgressHUD;
import com.android.blm.watermeter.widget.silkcalendar.SimpleMonthAdapter;
import com.google.gson.Gson;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author:${白曌勇} on 2016/7/18
 * TODO:
 */
public class SystemWarningNew extends BaseActivity {
    private List<Bean_SystemWarning.SystemWarnings> systemList;
    private RecyclerView systemListView;
    private SystemWarningNewAdapter newAdapter;
    private TextView txtStartTime, txtEndTime;
    private int pageIndex = 0, pageSize = 20;
    //    private CalendarDialog startDialog, endDialog;
    private CalendarPopNew startPop, endPop;
    private String startDate, endDate;
    private boolean isStart = false, isEnd = false;

    private TextView txtType;
    private PopupWindow typePop;
    private List<Bean_WarningType.WarningType> typeList;
    private View typeView;
    private ListView listType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system_warning_new);
        initView();
        initDate();
        initPopView();
        getSearchType();
    }

    private void initDate() {
        DateTime dt = DateTime.now();
        startDate = endDate = dt.toString("yyyy-MM-dd");
        txtEndTime.setText(endDate);
        txtStartTime.setText(startDate);
        getHasRead();
    }

    Bean_WarningType bean_WarningType;
    Handler typeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String) msg.obj;
            if (!TextUtils.isEmpty(result)) {
                bean_WarningType = new Gson().fromJson(result, Bean_WarningType.class);
                if (bean_WarningType != null && TextUtils.equals(bean_WarningType.Result, "1") && bean_WarningType.Data != null) {
                    typeList.addAll(bean_WarningType.Data);
                }
            }
            Bean_WarningType.WarningType warningType = new Bean_WarningType.WarningType();
            warningType.TypeID = "";
            warningType.TypeName = "全部";
            typeList.add(0, warningType);
            warningAdapter.notifyDataSetChanged();
        }
    };

    private void getSearchType() {
        Map<String, String> loginParam = new HashMap<>();
        Map<String, String> params = new HashMap<>();
        loginParam.put("Code", AppPrefrence.getUserPhone(this));
        loginParam.put("Token", AppPrefrence.getToken(this));
        PostTools.postDataBySoap(this, "GetAlarmType", loginParam, params, typeHandler, 0);
    }

    WarningTypeAdapter warningAdapter;

    private void initPopView() {
        typeList = new ArrayList<>();
        warningAdapter = new WarningTypeAdapter(typeList);
        typeView = LayoutInflater.from(this).inflate(R.layout.search_type_pop, null);
        typeView.findViewById(R.id.ll_parent).getBackground().setAlpha(180);
        typeView.findViewById(R.id.img_top_arrow).getBackground().setAlpha(180);
        listType = (ListView) typeView.findViewById(R.id.list_type);
        listType.setAdapter(warningAdapter);
        listType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                type = typeList.get(position).TypeID;
                txtType.setText(typeList.get(position).TypeName);
                getHasRead();
                typePop.dismiss();
            }
        });
        if (typePop == null) {
            typePop = new PopupWindow(typeView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            typePop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            typePop.setOutsideTouchable(true);
        }
    }


    private void initView() {
        systemList = new ArrayList<>();
        newAdapter = new SystemWarningNewAdapter(systemList, this, false);
        systemListView = (RecyclerView) findViewById(R.id.recyclerView);
        systemListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        systemListView.setItemAnimator(new DefaultItemAnimator());
        systemListView.setAdapter(newAdapter);
        newAdapter.setOnItemClickListener(new SystemWarningNewAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                Bean_SystemWarning.SystemWarnings systemListItem = systemList.get(position);
//                updateState(systemListItem.ID);
                Bundle bundle = new Bundle();
                bundle.putString("Title", "");
                bundle.putString("Content", systemListItem.Content);
                bundle.putString("PublishTime", systemListItem.AlarmTime);
                startActivityForResult(new Intent(SystemWarningNew.this, SystemWarningDetial.class).putExtra("data", bundle), 0);

            }
        });

        txtEndTime = (TextView) findViewById(R.id.txt_end_date);
        txtStartTime = (TextView) findViewById(R.id.txt_start_date);

        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);

        Calendar lastYear = Calendar.getInstance();
        lastYear.add(Calendar.YEAR, -1);

        txtType = (TextView) findViewById(R.id.txt_type);
    }

    Bean_SystemWarning bean_SystemList;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            String result = (String) msg.obj;
            Tools.debug("hasRead" + result);

            systemList.clear();
            if (TextUtils.isEmpty(result)) {
                Tools.toastMsg(SystemWarningNew.this, "请检查网络后重试");
            } else {
                bean_SystemList = new Gson().fromJson(result, Bean_SystemWarning.class);
                if (bean_SystemList != null && TextUtils.equals(bean_SystemList.Result, "1") && bean_SystemList.Data != null) {
                    systemList.addAll(bean_SystemList.Data);
                } else Tools.toastMsg(SystemWarningNew.this, bean_SystemList.Message);
            }
//            DbManagerHelper.getInstance().saveWarningList(systemList, AppPrefrence.getUsercode(SystemWarningNew.this));
//            newAdapter.notifyItemRangeChanged(0, systemList.size());
            newAdapter.notifyDataSetChanged();
            SVProgressHUD.dismiss(SystemWarningNew.this);
        }
    };

    private String type = "";

    private void getHasRead() {
        SVProgressHUD.showWithStatus(SystemWarningNew.this, "加载中...", SVProgressHUD.SVProgressHUDMaskType.Clear);
        Map<String, String> loginParam = new HashMap<>();
        Map<String, String> params = new HashMap<>();
        loginParam.put("Code", AppPrefrence.getUserPhone(SystemWarningNew.this));
        loginParam.put("Token", AppPrefrence.getToken(SystemWarningNew.this));
        params.put("AlarmTime", startDate);
        params.put("EndAlarmTime", endDate);
        params.put("Type", type);
        PostTools.postDataBySoap(SystemWarningNew.this, "GetSystemAlarmList", loginParam, params, handler, 0);
    }

    private DateTime sDate = null, eDate = null;
    private String startDateString, endDateString;
    private int startYear = 0, startMonth = 0, startDay = 0, endYear = 0, endMonth = 0, endDay = 0;
    @TargetApi(Build.VERSION_CODES.KITKAT)
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
                                Tools.toastMsg(SystemWarningNew.this, "开始时间不能大于结束时间");
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
                                Tools.toastMsg(SystemWarningNew.this, "开始时间不能大于结束时间");
                                return;
                            } else {
                                startDate = date;
                                txtStartTime.setText(startDate);
                                startPop.setSelectDay(null);
                                startPop.dismiss();
                            }
                        }
                        getHasRead();
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
                                Tools.toastMsg(SystemWarningNew.this, "结束时间不能小于开始时间");
                                return;
                            }
                        } else {
                            if (Tools.dateStringToLong(startDateString) > Tools.dateStringToLong(endDateString)){
                                if (endYear > 0)
                                    endPop.setSelectDay(new SimpleMonthAdapter.CalendarDay(Integer.parseInt(endDate.split("-")[0]), Integer.parseInt(endDate.split("-")[1]) - 1, Integer.parseInt(endDate.split("-")[2])));
                                Tools.toastMsg(SystemWarningNew.this, "结束时间不能小于开始时间");
                                return;
                            }else {
                                endDate = date;
                                endPop.setSelectDay(null);
                                txtEndTime.setText(endDate);
                                endPop.dismiss();
                            }
                        }
                        getHasRead();
                    }
                });
                endPop.shopPop(txtEndTime);
                break;
            case R.id.txt_type:
//                typePop.showAsDropDown(txtType, 0, 0);
                typePop.showAsDropDown(txtType, Tools.dip2px(SystemWarningNew.this, -50), 0, Gravity.CENTER_HORIZONTAL);
                break;
        }
    }


}
