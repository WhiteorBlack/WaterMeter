package com.android.blm.watermeter.manager.fragment;/**
 * Created by Administrator on 2016/6/18.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.blm.watermeter.R;
import com.android.blm.watermeter.adapter.SystemWarningAdapter;
import com.android.blm.watermeter.bean.Bean_SystemWarning;
import com.android.blm.watermeter.db.DbManagerHelper;
import com.android.blm.watermeter.manager.SystemWarningDetial;
import com.android.blm.watermeter.utils.AppPrefrence;
import com.android.blm.watermeter.utils.PostTools;
import com.android.blm.watermeter.utils.Tools;
import com.android.blm.watermeter.widget.loading.SVProgressHUD;
import com.android.blm.watermeter.widget.swipelistview.DensityUtil;
import com.android.blm.watermeter.widget.swipelistview.LJListView;
import com.android.blm.watermeter.widget.swipelistview.SwipeMenu;
import com.android.blm.watermeter.widget.swipelistview.SwipeMenuCreator;
import com.android.blm.watermeter.widget.swipelistview.SwipeMenuItem;
import com.android.blm.watermeter.widget.swipelistview.SwipeMenuListView;
import com.google.gson.Gson;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author:${白曌勇} on 2016/6/18
 * TODO:已关阀用户
 */
public class UnreadMessageFragment extends Fragment implements View.OnClickListener, SwipeMenuListView.OnMenuItemClickListener {
    private View view;
    LJListView listUser;
    private LinearLayout llBottom;
    private List<Bean_SystemWarning.SystemWarnings> userList;
    private SystemWarningAdapter unreadAdapter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (userList == null) {
            userList = new ArrayList<>();
            unreadAdapter = new SystemWarningAdapter(userList, getActivity(), true);
            listUser.setAdapter(unreadAdapter);
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        getHasRead();
    }

    Bean_SystemWarning bean_SystemList;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            String result = (String) msg.obj;
            Tools.debug("hasRead" + result);
            if (msg.what == 1) return;
            userList.clear();
            if (TextUtils.isEmpty(result)) {
                Tools.toastMsg(getActivity(), "请检查网络后重试");
            } else {
                bean_SystemList = new Gson().fromJson(result, Bean_SystemWarning.class);
                if (bean_SystemList != null && TextUtils.equals(bean_SystemList.Result, "1") && bean_SystemList.Data != null && bean_SystemList.Data.size() > 0) {
                    userList.addAll(bean_SystemList.Data);
                } else Tools.toastMsg(getActivity(), bean_SystemList.Message);
            }
            DbManagerHelper.getInstance().saveWarningList(userList, AppPrefrence.getUsercode(getActivity()));
            unreadAdapter.notifyDataSetChanged();
            SVProgressHUD.dismiss(getActivity());
        }
    };

    private String type = "";

    private void getHasRead() {
        SVProgressHUD.showWithStatus(getActivity(), "加载中...", SVProgressHUD.SVProgressHUDMaskType.Clear);
        Map<String, String> loginParam = new HashMap<>();
        Map<String, String> params = new HashMap<>();
        loginParam.put("Code", AppPrefrence.getUserPhone(getActivity()));
        loginParam.put("Token", AppPrefrence.getToken(getActivity()));
        params.put("AlarmTime", DateTime.now().toString("yyyy-MM-dd"));
        params.put("Type", type);
        PostTools.postDataBySoap(getActivity(), "GetSystemAlarmList", loginParam, params, handler, 0);
    }


    public boolean isOpen = false;

    public void updateBottomState() {
        if (isOpen) {
            llBottom.setVisibility(View.GONE);
            unreadAdapter.setInvisiable();
        } else {
            unreadAdapter.setVisiable();
            llBottom.setVisibility(View.VISIBLE);
        }
        isOpen = !isOpen;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.system_warning_unread_listview, null);
            initView();
        }
        return view;
    }

    private void initView() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(getActivity());
                openItem.setBackground(new ColorDrawable(getActivity().getResources().getColor(R.color.orange)));
                openItem.setWidth(DensityUtil.dip2px(getActivity(), 85));
                openItem.setTitle("已读");
                openItem.setTitleSize(16);
                openItem.setTitleColor(Color.WHITE);
                openItem.setIcon(R.mipmap.open_valve);
                menu.addMenuItem(openItem);
            }
        };
        listUser = (LJListView) view.findViewById(R.id.ljlistview);
        listUser.setMenuCreator(creator);
        listUser.setPullLoadEnable(false, "");
        listUser.setPullRefreshEnable(false);
        listUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bean_SystemWarning.SystemWarnings systemList = userList.get(position - 1);
                updateState(systemList.ID);
                Bundle bundle = new Bundle();
                bundle.putString("Title", "");
                bundle.putString("Content", systemList.Content);
                bundle.putString("PublishTime", systemList.AlarmTime);
                startActivityForResult(new Intent(getActivity(), SystemWarningDetial.class).putExtra("data", bundle), 0);
                userList.remove(position - 1);

                unreadAdapter.notifyDataSetChanged();
            }
        });

        listUser.setOnMenuItemClickListener(this);
        llBottom = (LinearLayout) view.findViewById(R.id.ll_bottom);
        llBottom.setVisibility(View.GONE);
        llBottom.setOnClickListener(this);

        ((TextView) view.findViewById(R.id.txt_bottom)).setText("标记已读");
        ((ImageView) view.findViewById(R.id.img_bottom)).setBackgroundResource(R.mipmap.set_read);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void getSearch(String type) {
        this.type = type;
        getHasRead();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_bottom:
                //标记已读
                for (int i = userList.size() - 1; !(i < 0); i--) {
                    if (userList.get(i).isSelect) {
                        updateState(userList.get(i).ID);
                        userList.remove(i);
                    }
                }
                unreadAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
        updateState(userList.get(position).ID);
        userList.remove(position);
        unreadAdapter.notifyDataSetChanged();
        return false;
    }

    private String flag = "1";

    private void updateState(String id) {
        Map<String, String> loginParam = new HashMap<>();
        Map<String, String> params = new HashMap<>();
        loginParam.put("Code", AppPrefrence.getUserPhone(getActivity()));
        loginParam.put("Token", AppPrefrence.getToken(getActivity()));
        params.put("ID", id);
        params.put("Flag", flag);
        PostTools.postDataBySoap(getActivity(), "ModifyNotice", loginParam, params, handler, 1);
    }
}
