package com.android.blm.watermeter.manager;/**
 * Created by Administrator on 2016/6/15.
 */

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.blm.watermeter.BaseActivity;
import com.android.blm.watermeter.R;
import com.android.blm.watermeter.adapter.FragmentAdapter;
import com.android.blm.watermeter.adapter.SearchTypeAdapter;
import com.android.blm.watermeter.adapter.WarningTypeAdapter;
import com.android.blm.watermeter.bean.Bean_WarningType;
import com.android.blm.watermeter.manager.fragment.CloseOwnMoneyFragment;
import com.android.blm.watermeter.manager.fragment.HasReadMessageFragment;
import com.android.blm.watermeter.manager.fragment.OpenOwnMoneyFragment;
import com.android.blm.watermeter.manager.fragment.UnreadMessageFragment;
import com.android.blm.watermeter.utils.AppPrefrence;
import com.android.blm.watermeter.utils.PostTools;
import com.android.blm.watermeter.utils.Tools;
import com.android.blm.watermeter.widget.NoScrollViewPager;
import com.google.gson.Gson;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author:${白曌勇} on 2016/6/15
 * TODO:
 */
public class SystemWarning extends BaseActivity {
    private TabLayout tableLayout;
    private NoScrollViewPager viewPager;
    private TextView txtType;
    private EditText edtSearch;

    private View typeView;
    private ListView listType;
    private PopupWindow typePop;
    private List<Bean_WarningType.WarningType> typeList;
    private FragmentAdapter fragmentAdapter;
    private List<Fragment> fragmentList;
    private List<String> titleList;
    private UnreadMessageFragment openFragment;
    private HasReadMessageFragment closeFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system_warning);
        initView();
        initPopView();
        getSearchType();
    }

    Bean_WarningType bean_WarningType;
    Handler handler = new Handler() {
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
        PostTools.postDataBySoap(this, "GetAlarmType", loginParam, params, handler, 0);
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
                txtType.setText(typeList.get(position).TypeName);

                if (openFragment != null && openFragment.isVisible()) {
                    openFragment.getSearch(typeList.get(position).TypeID);
                }
                if (closeFragment!=null&&closeFragment.isVisible()){
                    closeFragment.getSearch(typeList.get(position).TypeID);
                }
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
        fragmentList = new ArrayList<>();
        titleList = new ArrayList<>();
        titleList.add("未读");
        titleList.add("已读");
        openFragment = new UnreadMessageFragment();
        closeFragment = new HasReadMessageFragment();
        fragmentList.add(openFragment);
        fragmentList.add(closeFragment);
        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), fragmentList, titleList);

        tableLayout = (TabLayout) findViewById(R.id.tabLayout);
        tableLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.orange));
        tableLayout.setTabTextColors(Color.WHITE, Color.WHITE);
        tableLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tableLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    findViewById(R.id.fl_edit).setVisibility(View.VISIBLE);
                } else findViewById(R.id.fl_edit).setVisibility(View.GONE);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager = (NoScrollViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(fragmentAdapter);
        tableLayout.setupWithViewPager(viewPager);
        tableLayout.setTabsFromPagerAdapter(fragmentAdapter);
        txtType = (TextView) findViewById(R.id.txt_type);
        edtSearch = (EditText) findViewById(R.id.edt_search);
        edtSearch.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;
            }
        });
    }

    @Override
    public void waterClick(View v) {
        super.waterClick(v);
        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;
            case R.id.fl_edit:

                if (openFragment.isVisible() && viewPager.getCurrentItem() == 0) {
                    openFragment.updateBottomState();
                }
                break;
            case R.id.txt_type:
                typePop.showAsDropDown(txtType, Tools.dip2px(SystemWarning.this, 5), -5);
                break;
        }
    }
}
