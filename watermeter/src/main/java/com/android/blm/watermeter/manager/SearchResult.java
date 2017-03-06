package com.android.blm.watermeter.manager;/**
 * Created by Administrator on 2016/6/14.
 */

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.blm.watermeter.BaseActivity;
import com.android.blm.watermeter.R;
import com.android.blm.watermeter.adapter.ContralValveAdapter;
import com.android.blm.watermeter.adapter.SearchResultAdapter;
import com.android.blm.watermeter.adapter.SearchTypeAdapter;
import com.android.blm.watermeter.bean.Bean_OwnMoneyUser;
import com.android.blm.watermeter.utils.AppPrefrence;
import com.android.blm.watermeter.utils.PostTools;
import com.android.blm.watermeter.utils.Tools;
import com.android.blm.watermeter.widget.LoadMoreRecyclerView;
import com.android.blm.watermeter.widget.loading.SVProgressHUD;
import com.android.blm.watermeter.widget.swipelistview.LJListView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author:${白曌勇} on 2016/6/14
 * TODO:
 */
public class SearchResult extends BaseActivity implements LoadMoreRecyclerView.LoadMoreListener, LJListView.IXListViewListener {
    private LoadMoreRecyclerView searchView;
    private EditText edtSearch;
    private Button btnSearch;
    private TextView txtType;
    private View typeView;
    private ListView listType;
    private PopupWindow typePop;
    private String[] typeList = {"全部", "户号", "户名", "手机号码", "表编号", "门牌号"};
    private List<Bean_OwnMoneyUser.OwnUsers> resultList;
    private SearchResultAdapter resultAdapter;
    private int type = 0;
    private String userNo = "", userName = "", userPhone = "", meterNo = "", doorNo = "";
    private boolean isFirst = true;
    private int pageIndex = 1, pageSize = 20;
    private int index = 0;

    private ContralValveAdapter openAdapter;
    LJListView listUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);
        initView();
        initPopView();
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
                type = position;
                txtType.setText(typeList[position]);
                typePop.dismiss();
            }
        });
        if (typePop == null) {
            typePop = new PopupWindow(typeView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            typePop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            typePop.setOutsideTouchable(true);
        }
    }

    String keyword = "";
    private int selectPos = -1;

    private void initView() {
        resultList = new ArrayList<>();
        resultAdapter = new SearchResultAdapter(this, resultList);
        resultAdapter.setOnItemClickListener(new SearchResultAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                Bean_OwnMoneyUser.OwnUsers ownUsers = resultList.get(position);
                Bundle bundle = new Bundle();
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
                bundle.putBoolean("isOpen", true);
                startActivity(new Intent(SearchResult.this, SearchDetial.class).putExtra("data", bundle));
            }

            @Override
            public void onItemLongClick(View v, int position) {

            }
        });
        edtSearch = (EditText) findViewById(R.id.edt_search);
        edtSearch.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                keyword = edtSearch.getText().toString();
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    SVProgressHUD.showWithStatus(SearchResult.this, "加载中...", SVProgressHUD.SVProgressHUDMaskType.Clear);
                    pageIndex = 1;

                    if (type == 0) {

                        index = 0;
                        isFirst = true;
                        userName = "";
                        userPhone = "";
                        meterNo = "";
                        userNo = "";
                        searchAll(keyword);
                    } else
                        searchData(keyword);
                }

                return false;
            }
        });

        searchView = (LoadMoreRecyclerView) findViewById(R.id.recyclerView_search);
        searchView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        searchView.setItemAnimator(new DefaultItemAnimator());
        searchView.setAdapter(resultAdapter);
        searchView.setVisiableBottom(false);
        searchView.setAutoLoadMoreEnable(true);
        searchView.setLoadMoreListener(this);
        txtType = (TextView) findViewById(R.id.txt_type);

        listUser = (LJListView) findViewById(R.id.ljlistview);
        listUser.setMenuCreator(null);
        listUser.setPullLoadEnable(false, "");
        listUser.setPullRefreshEnable(false);
        listUser.setXListViewListener(this);
        listUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bean_OwnMoneyUser.OwnUsers ownUsers = resultList.get(position - 1);
                selectPos = position - 1;
                Bundle bundle = new Bundle();
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
                startActivityForResult(new Intent(SearchResult.this, SearchDetial.class).putExtra("data", bundle), 0);
            }
        });

        openAdapter = new ContralValveAdapter(resultList, this);
        listUser.setAdapter(openAdapter);
    }

    private boolean hasMore = false;
    private Bean_OwnMoneyUser bean_OwnMoneyUser;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String) msg.obj;
            Tools.debug("user" + result);

            if (TextUtils.isEmpty(result)) {
                Tools.toastMsg(SearchResult.this, "请检查网络后重试!");
                return;
            }

            if (pageIndex == 1) {
                resultList.clear();
//                searchView.removeAllViews();

            }
            bean_OwnMoneyUser = new Gson().fromJson(result, Bean_OwnMoneyUser.class);
            if (TextUtils.equals("1", bean_OwnMoneyUser.Result) && bean_OwnMoneyUser.Data != null) {
                resultList.addAll(bean_OwnMoneyUser.Data);
                if (bean_OwnMoneyUser.Data.size() < pageSize) {
                    hasMore = false;
                } else hasMore = true;
            } else hasMore = false;
            SVProgressHUD.dismiss(SearchResult.this);
            openAdapter.notifyDataSetChanged();
//            searchView.notifyMoreFinish(hasMore);
        }
    };

    private void searchAll(String keyword) {
        Map<String, String> loginInfo = new HashMap<>();
        loginInfo.put("Code", AppPrefrence.getUserPhone(SearchResult.this));
        loginInfo.put("Token", AppPrefrence.getToken(SearchResult.this));
        Map<String, String> params = new HashMap<>();
        params.put("Doorplate", doorNo);
        params.put("DataType", "1");
        params.put("ValveStatus", "");
        params.put("UserCode", "");
        params.put("UserName", "");
        params.put("Phone", "");
        params.put("MeterAddr", "");
        params.put("PageSize", pageSize + "");
        params.put("All", keyword);
        params.put("PageIndex", pageIndex + "");
        PostTools.postDataBySoap(SearchResult.this, "GetUserInfo", loginInfo, params, handler, 0);
    }

    private void searchData(String keyword) {

        Map<String, String> loginInfo = new HashMap<>();
        loginInfo.put("Code", AppPrefrence.getUserPhone(SearchResult.this));
        loginInfo.put("Token", AppPrefrence.getToken(SearchResult.this));
        Map<String, String> params = new HashMap<>();
        switch (type) {
            case 0:

                break;
            case 1:
                userName = "";
                userPhone = "";
                meterNo = "";
                doorNo = "";
                userNo = keyword;
                break;
            case 2:
                userPhone = "";
                meterNo = "";
                userNo = "";
                doorNo = "";
                userName = keyword;
                break;
            case 3:
                meterNo = "";
                userNo = "";
                userName = "";
                userPhone = keyword;
                doorNo = "";
                break;
            case 4:
                userNo = "";
                userName = "";
                userPhone = "";
                meterNo = keyword;
                doorNo = "";
                break;
            case 5:
                userNo = "";
                userName = "";
                userPhone = "";
                meterNo = "";
                doorNo = keyword;
                break;
        }
        params.put("Doorplate", doorNo);
        params.put("DataType", "1");
        params.put("ValveStatus", "");
        params.put("UserCode", userNo);
        params.put("UserName", userName);
        params.put("Phone", userPhone);
        params.put("MeterAddr", meterNo);
        params.put("PageSize", pageSize + "");
        params.put("PageIndex", pageIndex + "");
        PostTools.postDataBySoap(SearchResult.this, "GetUserInfo", loginInfo, params, handler, 0);
    }

    @Override
    public void waterClick(View v) {
        super.waterClick(v);
        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;
            case R.id.txt_type:
                typePop.showAsDropDown(txtType, Tools.dip2px(SearchResult.this, 5), -5);
                break;
            case R.id.fl_search:
                keyword = edtSearch.getText().toString();
                SVProgressHUD.showWithStatus(SearchResult.this, "加载中...", SVProgressHUD.SVProgressHUDMaskType.Clear);
                pageIndex = 1;

                if (type == 0) {

                    index = 0;
                    isFirst = true;
                    userName = "";
                    userPhone = "";
                    meterNo = "";
                    userNo = "";
                    searchAll(keyword);
                } else
                    searchData(keyword);
                break;
        }
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {
        pageIndex++;
        if (type == 0) {
            searchAll(keyword);
        } else {
            searchData(keyword);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (data.getBooleanExtra("isSucess", false)) {
                if (resultList.get(selectPos).ValveStatus.contains("开"))
                    resultList.get(selectPos).ValveStatus = "关阀(拉闸)";
                else
                    resultList.get(selectPos).ValveStatus = "开阀(合闸)";
            }
        }
        if (resultCode == 100 && data != null) {
            Bundle bundle = data.getBundleExtra("copy");
            if (bundle != null) {
                resultList.get(selectPos).LastReadDate = bundle.getString("ReadDate");
                resultList.get(selectPos).LastReadNumber = bundle.getString("ReadNum");
            }
        }
    }
}
