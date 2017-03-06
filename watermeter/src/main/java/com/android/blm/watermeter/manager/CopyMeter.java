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
import com.android.blm.watermeter.adapter.NewCopyMeterAdapter;
import com.android.blm.watermeter.adapter.SearchTypeAdapter;
import com.android.blm.watermeter.bean.Bean_CopyMeter;
import com.android.blm.watermeter.bean.Bean_OwnMoneyUser;
import com.android.blm.watermeter.utils.AppPrefrence;
import com.android.blm.watermeter.utils.PostTools;
import com.android.blm.watermeter.utils.Tools;
import com.android.blm.watermeter.widget.XListView;
import com.android.blm.watermeter.widget.loading.SVProgressHUD;
import com.android.blm.watermeter.widget.swipelistview.SwipeMenu;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author:${白曌勇} on 2016/6/15
 * TODO:
 */
public class CopyMeter extends BaseActivity implements XListView.IXListViewListener {

    //    private LoadMoreRecyclerView meterView;
    private XListView meterListView;
    private LinearLayout llBottom;
    private List<Bean_CopyMeter.CopyMeters> meterList;
    //    private CopyMeterAdapter meterAdapter;
    private int pageIndex = 1, pageSize = 20;
    private boolean isLoadMore = false;
    private NewCopyMeterAdapter newCopyMeterAdapter;
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
        setContentView(R.layout.copy_meter);
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
                SVProgressHUD.dismiss(CopyMeter.this);
                Tools.toastMsg(CopyMeter.this, "请检查网络后重试!");
                return;
            }
            if (msg.what == 0) {
                if (pageIndex == 1) {
                    if (copyUsers != null)
                        copyUsers.clear();
                    copyMeters = null;
                    meterList.clear();
                    newCopyMeterAdapter.notifyDataSetChanged();
                }

                bean_ownMoneyUser = new Gson().fromJson(result, Bean_OwnMoneyUser.class);
                if (TextUtils.equals("1", bean_ownMoneyUser.Result) && bean_ownMoneyUser.Data != null) {
//                    meterList.addAll(bean_ownMoneyUser.Data);
                    if (bean_ownMoneyUser.Data.size() < pageSize) {
                        isLoadMore = false;
                    } else isLoadMore = true;
                    dealData();
                } else {
                    Tools.toastMsg(CopyMeter.this, bean_ownMoneyUser.Message);
                }
                meterListView.setPullLoadEnable(isLoadMore);
                SVProgressHUD.dismiss(CopyMeter.this);
            }

            if (msg.what == 1) {
                dealResult(result);
            }

            if (msg.what == 2) {
                successPos = 0;
                newCopyMeterAdapter.notifyDataSetChanged();
                SVProgressHUD.dismiss(CopyMeter.this);
                SVProgressHUD.getProgressBar(CopyMeter.this).setProgress(0);
                progress = 0;
                selectedList.clear();
            }
            meterListView.setPullLoadEnable(isLoadMore);
//            SVProgressHUD.dismiss(CopyMeter.this);
        }
    };

    private void dealResult(String result) {
        if (isSingle) {
            try {
                JSONObject object = new JSONObject(result);
                if (object != null && TextUtils.equals(object.getString("Result"), "1")) {
                    meterList.get(selectParen).User.remove(selectChild);
                    if (meterList.get(selectParen).User == null || meterList.get(selectParen).User.size() == 0) {
                        meterList.remove(selectParen);
                    }
                    newCopyMeterAdapter.notifyDataSetChanged();
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                JSONObject object = new JSONObject(result);
                if (object != null && TextUtils.equals(object.getString("Result"), "1")) {

                    meterList.get(selectedList.get(successPos).parentPos).User.get(selectedList.get(successPos).position).isCopy = true;
                    successPos++;

                } else {
                    selectedList.remove(successPos);
                }
            } catch (Exception e) {
                e.printStackTrace();
                selectedList.remove(successPos);
            }
            progress++;
            SVProgressHUD.getProgressBar(CopyMeter.this).setProgress(progress);
            SVProgressHUD.setText(CopyMeter.this, "抄表" + progress + "/" + selectCount);
            if (successPos < selectedList.size()) {
                copyMeter(selectedList.get(successPos).MeterAddr);
            } else {
                //操作结束
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = meterList.size() - 1; !(i < 0); i--) {
                            for (int j = meterList.get(i).User.size() - 1; !(j < 0); j--) {
                                if (meterList.get(i).User.get(j).isCopy) {
                                    meterList.get(i).User.remove(j);
                                }
                            }

                            if (meterList.get(i).User.size() == 0) {
                                meterList.remove(i);
                            }
                        }
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

    private void dealData() {
        String DeptName = "-1";
        if (meterList != null && meterList.size() > 0) {
            DeptName = meterList.get(meterList.size() - 1).DeptName;
            isFirst = true;
        }else {
            DeptName="-1";
            isFirst=false;
        }
        for (int i = 0; i < bean_ownMoneyUser.Data.size(); i++) {
            if (TextUtils.equals(DeptName, bean_ownMoneyUser.Data.get(i).DeptName)) {
                if (isFirst) {
                    meterList.get(meterList.size() - 1).User.add(bean_ownMoneyUser.Data.get(i));
                } else
                    copyUsers.add(bean_ownMoneyUser.Data.get(i));
            } else {
                isFirst = false;
                if (copyMeters != null) {
                    copyMeters.User = copyUsers;
                    meterList.add(copyMeters);
                }
                DeptName = bean_ownMoneyUser.Data.get(i).DeptName;
                copyMeters = new Bean_CopyMeter.CopyMeters();
                copyUsers = new ArrayList<>();
                copyMeters.DeptName = DeptName;
                if (i == 0) {
                    copyUsers.add(bean_ownMoneyUser.Data.get(i));
                    copyMeters.User = copyUsers;

                }
            }
        }
        if (!isFirst && copyUsers != null && copyMeters != null) {
            copyMeters.User = copyUsers;
            meterList.add(copyMeters);
        }
        newCopyMeterAdapter.notifyDataSetChanged();
    }

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
        meterList = new ArrayList<>();
//        meterAdapter = new CopyMeterAdapter(this, meterList);
        newCopyMeterAdapter = new NewCopyMeterAdapter(this, meterList);
        llBottom = (LinearLayout) findViewById(R.id.ll_copy_meter);
        llBottom.setVisibility(View.GONE);

        txtType = (TextView) findViewById(R.id.txt_type);
        edtSearch = (EditText) findViewById(R.id.edt_search);
        edtSearch.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                pageIndex=1;
                getSearchData();
                return false;
            }
        });

        meterListView = (XListView) findViewById(R.id.xlistview_copy);
        meterListView.setPullRefreshEnable(false);
        meterListView.setXListViewListener(this);
        meterListView.setPullLoadEnable(true);
        meterListView.setAdapter(newCopyMeterAdapter);
        newCopyMeterAdapter.setOnItemClickListener(new NewCopyMeterAdapter.OnItemClickListener() {
            @Override
            public void onCopyMeterClick(SwipeMenu menu, int parentPos, int childPos) {
                SVProgressHUD.showWithStatus(CopyMeter.this, "抄表中...", SVProgressHUD.SVProgressHUDMaskType.Clear);
                isSingle = true;
                copyMeter(meterList.get(parentPos).User.get(childPos).MeterAddr);
                selectChild = childPos;
                selectParen = parentPos;
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
                typePop.showAsDropDown(txtType, Tools.dip2px(CopyMeter.this, 5), -5);
                break;
            case R.id.fl_search:
                pageIndex=1;
                getSearchData();
                break;
        }
    }

    private List<Bean_OwnMoneyUser.OwnUsers> selectedList;
    int successPos = 0;

    private void getSelectedList() {
        selectedList = new ArrayList<>();
        for (int i = 0; i < meterList.size(); i++) {
            for (int j = 0; j < meterList.get(i).User.size(); j++) {
                Bean_OwnMoneyUser.OwnUsers ownUser = meterList.get(i).User.get(j);
                if (ownUser.isSelect) {
                    ownUser.parentPos = i;
                    ownUser.position = j;
                    selectedList.add(ownUser);
                }
            }
        }
        if (selectedList.size() > 0) {
            selectCount = selectedList.size();
            SVProgressHUD.showWithProgress(CopyMeter.this, "抄表" + progress + "/" + selectCount, SVProgressHUD.SVProgressHUDMaskType.Clear);
            SVProgressHUD.getProgressBar(CopyMeter.this).setMax(selectCount);
            copyMeter(selectedList.get(0).MeterAddr);
        } else {
            Tools.toastMsg(CopyMeter.this, "请选择要抄表的用户");
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
//            meterView.setAutoLoadMoreEnable(isLoadMore );
            meterListView.setPullLoadEnable(isLoadMore);
        } else {
            llBottom.setVisibility(View.VISIBLE);
            visiable();
            meterListView.setPullLoadEnable(false);
//            meterView.setAutoLoadMoreEnable(false);
        }

        isVisiable = !isVisiable;
    }

    private void visiable() {
        for (int i = 0; i < meterList.size(); i++) {
            meterList.get(i).isVisiable = true;
            if (meterList.get(i).User != null && meterList.get(i).User.size() > 0) {
                for (int j = 0; j < meterList.get(i).User.size(); j++) {
                    meterList.get(i).User.get(j).isVisiable = true;
                }
            }
        }
        newCopyMeterAdapter.notifyDataSetChanged();
//        meterAdapter.notifyItemRangeChanged(0, meterList.size());
    }

    private void invisiable() {
        for (int i = 0; i < meterList.size(); i++) {
            meterList.get(i).isVisiable = false;
            if (meterList.get(i).User != null && meterList.get(i).User.size() > 0) {
                for (int j = 0; j < meterList.get(i).User.size(); j++) {
                    meterList.get(i).User.get(j).isVisiable = false;
                }
            }
        }
        newCopyMeterAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Bundle bundle = data.getBundleExtra("result");
            int parent = bundle.getInt("parent");
            int child = bundle.getInt("child");

            meterList.get(parent).User.remove(child);
            if (meterList.get(parent).User.size() == 0) {
                meterList.remove(parent);
            }
            newCopyMeterAdapter.notifyDataSetChanged();
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
