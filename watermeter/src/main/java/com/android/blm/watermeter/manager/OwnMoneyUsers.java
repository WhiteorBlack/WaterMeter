package com.android.blm.watermeter.manager;/**
 * Created by Administrator on 2016/6/15.
 */

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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
import com.android.blm.watermeter.adapter.FragmentAdapter;
import com.android.blm.watermeter.adapter.SearchTypeAdapter;
import com.android.blm.watermeter.bean.Bean_OwnMoneyUser;
import com.android.blm.watermeter.manager.fragment.CloseOwnMoneyFragment;
import com.android.blm.watermeter.manager.fragment.CloseValveFragment;
import com.android.blm.watermeter.manager.fragment.OpenOwnMoneyFragment;
import com.android.blm.watermeter.manager.fragment.OpenValveFragment;
import com.android.blm.watermeter.utils.Tools;
import com.android.blm.watermeter.widget.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * author:${白曌勇} on 2016/6/15
 * TODO:
 */
public class OwnMoneyUsers extends BaseActivity implements ViewPager.OnPageChangeListener {
    private TabLayout tableLayout;
    private NoScrollViewPager viewPager;
    private TextView txtType;
    private EditText edtSearch;

    private View typeView;
    private ListView listType;
    private PopupWindow typePop;
    private String[] typeList = {"全部", "户号", "户名", "手机号码", "表编号", "门牌号"};
    private int searchType = 0;
    private FragmentAdapter fragmentAdapter;
    private List<Fragment> fragmentList;
    private List<String> titleList;
    private OpenOwnMoneyFragment openFragment;
    private CloseOwnMoneyFragment closeFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contral_valve);
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
                txtType.setText(typeList[position]);
                searchType = position;
                typePop.dismiss();
            }
        });
        if (typePop == null) {
            typePop = new PopupWindow(typeView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            typePop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            typePop.setOutsideTouchable(true);
        }
    }

    public void setArgument(List<Bean_OwnMoneyUser.OwnUsers> users) {
        if (closeFragment.isVisible() && viewPager.getCurrentItem() == 1) {
            openFragment.addData(users);
        }

        if (openFragment.isVisible() && viewPager.getCurrentItem() == 0) {
            closeFragment.addData(users);
        }
    }

    private String keyword;

    private void initView() {
        fragmentList = new ArrayList<>();
        titleList = new ArrayList<>();
        titleList.add("未关阀");
        titleList.add("已关阀");
        openFragment = new OpenOwnMoneyFragment();
        closeFragment = new CloseOwnMoneyFragment();
        fragmentList.add(openFragment);
        fragmentList.add(closeFragment);
        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), fragmentList, titleList);

        tableLayout = (TabLayout) findViewById(R.id.tabLayout);
        tableLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.orange));
        tableLayout.setTabTextColors(Color.WHITE, Color.WHITE);
        tableLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        viewPager = (NoScrollViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(fragmentAdapter);
        viewPager.setOnPageChangeListener(this);
        tableLayout.setupWithViewPager(viewPager);
        tableLayout.setTabsFromPagerAdapter(fragmentAdapter);
        txtType = (TextView) findViewById(R.id.txt_type);
        edtSearch = (EditText) findViewById(R.id.edt_search);
        edtSearch.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                keyword = edtSearch.getText().toString();
                if (!TextUtils.isEmpty(keyword) && actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (closeFragment.isVisible() && viewPager.getCurrentItem() == 1) {
                        closeFragment.searchUser(searchType, keyword);
                    }

                    if (openFragment.isVisible() && viewPager.getCurrentItem() == 0) {
                        openFragment.searchUser(searchType, keyword);
                    }
                }
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
                if (closeFragment.isVisible() && viewPager.getCurrentItem() == 1) {
                    closeFragment.updateBottomState();
                }

                if (openFragment.isVisible() && viewPager.getCurrentItem() == 0) {
                    openFragment.updateBottomState();
                }
                break;
            case R.id.txt_type:
                typePop.showAsDropDown(txtType, Tools.dip2px(OwnMoneyUsers.this, 5), -5);
                break;
            case R.id.fl_search:
                keyword = edtSearch.getText().toString();
                if (closeFragment.isVisible() && viewPager.getCurrentItem() == 1) {
                    closeFragment.searchUser(searchType, keyword);
                }

                if (openFragment.isVisible() && viewPager.getCurrentItem() == 0) {
                    openFragment.searchUser(searchType, keyword);
                }
                break;
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        edtSearch.setText("");
        txtType.setText(typeList[0]);
        if (position == 0) {
            openFragment.getData();
        }
        if (position == 1)
            closeFragment.getData();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
