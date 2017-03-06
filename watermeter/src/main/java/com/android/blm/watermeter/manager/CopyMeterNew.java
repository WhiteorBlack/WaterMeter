package com.android.blm.watermeter.manager;/**
 * Created by Administrator on 2016/6/15.
 */

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.blm.watermeter.BaseActivity;
import com.android.blm.watermeter.R;
import com.android.blm.watermeter.adapter.CopyUserNewAdapterTest;
import com.android.blm.watermeter.adapter.SearchTypeAdapter;
import com.android.blm.watermeter.bean.Bean_CopyMeter;
import com.android.blm.watermeter.bean.Bean_CopyResult;
import com.android.blm.watermeter.bean.Bean_OwnMoneyUser;
import com.android.blm.watermeter.utils.AppPrefrence;
import com.android.blm.watermeter.utils.PostTools;
import com.android.blm.watermeter.utils.Tools;
import com.android.blm.watermeter.widget.XListView;
import com.android.blm.watermeter.widget.loading.SVProgressHUD;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author:${白曌勇} on 2016/6/15
 * TODO:
 */
public class CopyMeterNew extends BaseActivity implements XListView.IXListViewListener {

    private XListView meterListView;
    private LinearLayout llBottom;
    private List<Bean_OwnMoneyUser.OwnUsers> userList;
    private int pageIndex = 1, pageSize = 20;
    private boolean isLoadMore = false;
    //    private CopyUserNewAdapter copyUserNewAdapter;
    private CopyUserNewAdapterTest copyUserNewAdapter;
    private boolean isSingle = false;
    private int selectParen = 0, selectChild = 0;
    private int selectCount, progress = 0;

    private View typeView;
    private EditText edtSearch;
    private TextView txtType;
    private ListView listType;
    private PopupWindow typePop;
    private String[] typeList = {"全部", "户号", "户名", "手机号码", "表编号", "门牌号"};
    private int type = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.copy_meter_new);
        initView();
        initPopView();
        SVProgressHUD.showWithStatus(this, "加载中...", SVProgressHUD.SVProgressHUDMaskType.Clear);
        getMeterData();
    }

    private void initPopView() {
        typeView = LayoutInflater.from(this).inflate(R.layout.search_type_pop, null);
        typeView.findViewById(R.id.ll_parent).getBackground().setAlpha(180);
        typeView.findViewById(R.id.img_top_arrow).getBackground().setAlpha(180);
        listType = (ListView) typeView.findViewById(R.id.list_type);
        listType.setAdapter(new SearchTypeAdapter(typeList));
        listType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                txtType.setText(typeList[position]);
                type = position;
                typePop.dismiss();
            }
        });
        if (typePop == null) {
            typePop = new PopupWindow(typeView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            typePop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            typePop.setOutsideTouchable(true);
        }
    }


    private Bean_OwnMoneyUser bean_ownMoneyUser;
    private int position = 0;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String) msg.obj;
            Tools.debug("user" + result);


            if (TextUtils.isEmpty(result)) {
                SVProgressHUD.dismiss(CopyMeterNew.this);
                Tools.toastMsg(CopyMeterNew.this, "请检查网络后重试!");
                return;
            }
            if (msg.what == 0) {
                if (pageIndex==1)
                    userList.clear();
                bean_ownMoneyUser = new Gson().fromJson(result, Bean_OwnMoneyUser.class);
                if (TextUtils.equals("1", bean_ownMoneyUser.Result) && bean_ownMoneyUser.Data != null) {
                    userList.addAll(bean_ownMoneyUser.Data);
                    if (bean_ownMoneyUser.Data.size() < pageSize) {
                        isLoadMore = false;
                    } else isLoadMore = true;
                } else {
                    Tools.toastMsg(CopyMeterNew.this, bean_ownMoneyUser.Message);
                    isLoadMore=false;
                }
                copyUserNewAdapter.notifyDataSetChanged();
                meterListView.setPullLoadEnable(isLoadMore);
                SVProgressHUD.dismiss(CopyMeterNew.this);
            }

            if (msg.what == 1) {
                dealResult(result);
            }

            if (msg.what == 2) {
                successPos = 0;
//                copyUserNewAdapter.notifyDataSetChanged();
                SVProgressHUD.dismiss(CopyMeterNew.this);
                SVProgressHUD.getProgressBar(CopyMeterNew.this).setProgress(0);
                progress = 0;
                selectedList.clear();
            }
            meterListView.setPullLoadEnable(isLoadMore);
        }
    };

    private void dealResult(String result) {
        Bean_CopyResult bean_copyResult = new Gson().fromJson(result, Bean_CopyResult.class);
        if (isSingle) {
            try {
                SVProgressHUD.dismiss(CopyMeterNew.this);
                if (bean_copyResult != null && TextUtils.equals(bean_copyResult.Result, "1")) {
                    userList.get(selectParen).isSelect = false;
                    userList.get(selectParen).LastReadNumber = bean_copyResult.Data.get(0).MeterNumber;
                    userList.get(selectParen).LastReadDate = bean_copyResult.Data.get(0).ReadDate;
                    copyUserNewAdapter.notifyDataSetChanged();
                } else {
                    Tools.toastMsg(CopyMeterNew.this, bean_copyResult.Message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (bean_copyResult != null && TextUtils.equals(bean_copyResult.Result, "1")) {
                    int postion = selectedList.get(successPos).position;
                    userList.get(postion).isSelect = false;
                    userList.get(postion).LastReadNumber = bean_copyResult.Data.get(0).MeterNumber;
                    userList.get(postion).LastReadDate = bean_copyResult.Data.get(0).ReadDate;
                    successPos++;
                    copyUserNewAdapter.notifyDataSetChanged();
                } else {
                    selectedList.remove(successPos);
                }
            } catch (Exception e) {
                e.printStackTrace();
                selectedList.remove(successPos);
            }
            progress++;
            SVProgressHUD.getProgressBar(CopyMeterNew.this).setProgress(progress);
            SVProgressHUD.setText(CopyMeterNew.this, "抄表" + progress + "/" + selectCount);
            if (successPos < selectedList.size()) {
                copyMeter(selectedList.get(successPos).MeterAddr);
            } else {
                //操作结束
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Message msg = new Message();
                        msg.obj = "success";
                        msg.what = 2;
                        handler.sendMessage(msg);
                    }

                }).start();
            }

        }

    }

    Bean_CopyMeter.CopyMeters copyMeters;
    List<Bean_OwnMoneyUser.OwnUsers> copyUsers;

    private boolean isFirst;


    private void getMeterData() {
        Map<String, String> loginInfo = new HashMap<>();
        loginInfo.put("Code", AppPrefrence.getUserPhone(this));
        loginInfo.put("Token", AppPrefrence.getToken(this));
        Map<String, String> params = new HashMap<>();
        params.put("Doorplate", doorNo);
        params.put("DataType", "3");
        params.put("ValveStatus", "");
        params.put("UserCode", userNo);
        params.put("UserName", userName);
        params.put("Phone", userPhone);
        params.put("MeterAddr", meterNo);
        params.put("All", all);
        params.put("PageSize", pageSize + "");
        params.put("PageIndex", pageIndex + "");
        PostTools.postDataBySoap(this, "GetUserInfo", loginInfo, params, handler, 0);
    }

    String keyword = "";

    private void initView() {
        userList = new ArrayList<>();
        copyUserNewAdapter = new CopyUserNewAdapterTest(userList, this);
        llBottom = (LinearLayout) findViewById(R.id.ll_copy_meter);
        llBottom.setVisibility(View.GONE);

        txtType = (TextView) findViewById(R.id.txt_type);
        edtSearch = (EditText) findViewById(R.id.edt_search);
        edtSearch.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                getSearchData();
                return false;
            }
        });

        meterListView = (XListView) findViewById(R.id.xlistview_copy);

        meterListView.setPullRefreshEnable(false);
        meterListView.setPullLoadEnable(false);
        meterListView.setXListViewListener(this);
        meterListView.setAdapter(copyUserNewAdapter);
        copyUserNewAdapter.setOnClickListener(new CopyUserNewAdapterTest.OnClickListener() {
            @Override
            public void onClick(View v, int position) {
                selectParen = position;
                isSingle = true;
                SVProgressHUD.showWithStatus(CopyMeterNew.this, "抄表中...", SVProgressHUD.SVProgressHUDMaskType.Clear);
                Tools.debug("adapter" + position);
                copyMeter(userList.get(position).MeterAddr);
            }
        });
        meterListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectParen = position - 1;
                Tools.debug("list" + position);
                Bean_OwnMoneyUser.OwnUsers ownUsers = userList.get(position - 1);
                Bundle bundle = new Bundle();
                bundle.putInt("parent", position);
                bundle.putString("MeterAddr", ownUsers.MeterAddr);
                bundle.putString("MeterTypeName", ownUsers.MeterType);
                bundle.putString("ValveName", ownUsers.ValveName);
                bundle.putString("UserCode", ownUsers.UserCode);
                bundle.putString("UserName", ownUsers.UserName);
                bundle.putString("Phone", ownUsers.Phone);
                bundle.putString("Doorplate", ownUsers.Doorplate);
                bundle.putString("Address", ownUsers.Address);
                bundle.putString("Reserve", ownUsers.Reserve);
                bundle.putString("MergeDeptName", ownUsers.DeptName);
                bundle.putString("ValveStatus", ownUsers.ValveStatus);
                bundle.putString("LastReadDate", ownUsers.LastReadDate);
                bundle.putString("LastReadNumber", ownUsers.LastReadNumber);
                startActivityForResult(new Intent(CopyMeterNew.this, CopyMeterDetial.class).putExtra("data", bundle), 0);
            }
        });
    }

    private void getSearchData() {
        pageIndex = 1;
        keyword = edtSearch.getText().toString();
        SVProgressHUD.showWithStatus(this, "搜索中...", SVProgressHUD.SVProgressHUDMaskType.Clear);
        clearParam();
        switch (type) {
            case 0:
                //全部
                all = keyword;
                break;
            case 1:
                //业主编号
                userNo = keyword;
                break;
            case 2:
                //姓名
                userName = keyword;
                break;
            case 3:
                // 手机
                userPhone = keyword;
                break;
            case 4:
                //仪表编号
                meterNo = keyword;
                break;
            case 5:
                doorNo = keyword;
                break;

        }
        getMeterData();
    }

    private String userName = "", userNo = "", userPhone = "", meterNo = "", all = "", doorNo = "";

    private void clearParam() {
        userName = "";
        userNo = "";
        userPhone = "";
        meterNo = "";
        all = "";
        doorNo = "";
    }

    @Override
    public void waterClick(View v) {
        super.waterClick(v);
        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;
            case R.id.fl_edit:
                updateBottomState();
                break;
            case R.id.ll_copy_meter:
                isSingle = false;
                getSelectedList();
                break;
            case R.id.txt_type:
                typePop.showAsDropDown(txtType, Tools.dip2px(CopyMeterNew.this, 5), -5);
                break;
            case R.id.fl_search:
                getSearchData();
                break;
        }
    }

    private List<Bean_OwnMoneyUser.OwnUsers> selectedList;
    int successPos = 0;

    private void getSelectedList() {
        selectedList = new ArrayList<>();
        for (int i = 0; i < userList.size(); i++) {

            Bean_OwnMoneyUser.OwnUsers ownUser = userList.get(i);
            if (ownUser.isSelect) {
                ownUser.position = i;
                selectedList.add(ownUser);

            }
        }
        if (selectedList.size() > 0) {
            selectCount = selectedList.size();
            SVProgressHUD.showWithProgress(CopyMeterNew.this, "抄表" + progress + "/" + selectCount, SVProgressHUD.SVProgressHUDMaskType.Clear);
            SVProgressHUD.getProgressBar(CopyMeterNew.this).setMax(selectCount);
            copyMeter(selectedList.get(0).MeterAddr);
        } else {
            Tools.toastMsg(CopyMeterNew.this, "请选择要抄表的用户");
        }

    }

    private void copyMeter(String meterAddr) {
        Map<String, String> loginInfo = new HashMap<>();
        loginInfo.put("Code", AppPrefrence.getUserPhone(this));
        loginInfo.put("Token", AppPrefrence.getToken(this));
        Map<String, String> params = new HashMap<>();
        params.put("MeterAddr", meterAddr);
        PostTools.postDataBySoap(this, "ReadMeter", loginInfo, params, handler, 1);
    }

    private boolean isVisiable = false;

    private void updateBottomState() {
        if (isVisiable) {
            llBottom.setVisibility(View.GONE);
            invisiable();
            meterListView.setPullLoadEnable(isLoadMore);
        } else {
            llBottom.setVisibility(View.VISIBLE);
            visiable();
            meterListView.setPullLoadEnable(false);
        }

        isVisiable = !isVisiable;
    }

    private void visiable() {
        for (int i = 0; i < userList.size(); i++) {
            userList.get(i).isVisiable = true;
        }
        copyUserNewAdapter.notifyDataSetChanged();
    }

    private void invisiable() {
        for (int i = 0; i < userList.size(); i++) {
            userList.get(i).isVisiable = false;
        }
        copyUserNewAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Bundle bundle = data.getBundleExtra("result");
            userList.get(selectParen).LastReadDate = bundle.getString("LastReadDate");
            userList.get(selectParen).LastReadNumber = bundle.getString("LastReadNumber");
            userList.get(selectParen).MeterAddr = bundle.getString("MeterAddr");
            userList.get(selectParen).MeterType = bundle.getString("MeterType");
            userList.get(selectParen).Phone = bundle.getString("Phone");
            userList.get(selectParen).Reserve = bundle.getString("Reserve");
            copyUserNewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {
        pageIndex++;
        getMeterData();
    }
}
