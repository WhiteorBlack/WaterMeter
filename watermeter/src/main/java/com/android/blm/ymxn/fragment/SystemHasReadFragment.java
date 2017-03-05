package com.android.blm.ymxn.fragment;/**
 * Created by Administrator on 2016/6/2.
 */

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

import com.android.blm.ymxn.R;
import com.android.blm.ymxn.activity.SystemMessage;
import com.android.blm.ymxn.activity.SystemMsgDetial;
import com.android.blm.ymxn.adapter.SystemMsgAdapter;
import com.android.blm.ymxn.bean.Bean_SystemList;
import com.android.blm.ymxn.db.DbManagerHelper;
import com.android.blm.ymxn.utils.AppPrefrence;
import com.android.blm.ymxn.utils.PostTools;
import com.android.blm.ymxn.utils.Tools;
import com.android.blm.ymxn.widget.swipelistview.DensityUtil;
import com.android.blm.ymxn.widget.swipelistview.LJListView;
import com.android.blm.ymxn.widget.swipelistview.SwipeMenu;
import com.android.blm.ymxn.widget.swipelistview.SwipeMenuCreator;
import com.android.blm.ymxn.widget.swipelistview.SwipeMenuItem;
import com.android.blm.ymxn.widget.swipelistview.SwipeMenuListView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * author:${白曌勇} on 2016/6/2
 * TODO:
 */
public class SystemHasReadFragment extends Fragment implements SwipeMenuListView.OnMenuItemClickListener, LJListView.IXListViewListener {
    private View view;
    private LJListView systemListView;
    private List<Bean_SystemList.SystemList> systemList;
    private SystemMsgAdapter msgAdapter;
    private boolean isVisiable = false;
    private String type;
    private int pageIndex = 0, pageSize = 20;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (systemList == null) {
            systemList = new ArrayList<>();
            msgAdapter = new SystemMsgAdapter(systemList, getActivity(), false);
        }

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(getActivity());
                openItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F,
                        0x25)));
                openItem.setWidth(DensityUtil.dip2px(getActivity(), 85));
                openItem.setTitle("删除");
                openItem.setTitleSize(16);
                openItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(openItem);
            }
        };

        systemListView = (LJListView) view.findViewById(R.id.ljlistview);
        systemListView.setMenuCreator(creator);
        systemListView.setOnMenuItemClickListener(this);
        systemListView.setPullRefreshEnable(false);
        systemListView.setPullLoadEnable(false, "");
        systemListView.setXListViewListener(this);
        systemListView.setAdapter(msgAdapter);
        systemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("title", systemList.get(position - 1).Title);
                bundle.putString("date", systemList.get(position - 1).PublishTime);
                bundle.putString("content", systemList.get(position - 1).Content);
                startActivity(new Intent(getActivity(), SystemMsgDetial.class).putExtra("detial", bundle));
            }
        });
        if (pageIndex == 0)
            getReadList();
    }

    public void setType(String type) {
        this.type = type;
    }

    Bean_SystemList bean_SystemList;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String) msg.obj;
            Tools.debug("hasRead" + result);
            if (TextUtils.isEmpty(result)) {
                Tools.toastMsg(getActivity(), "请检查网络后重试");
            } else {
                bean_SystemList = new Gson().fromJson(result, Bean_SystemList.class);
                if (bean_SystemList != null && TextUtils.equals(bean_SystemList.Result, "1") && bean_SystemList.Data != null && bean_SystemList.Data.size() > 0) {
                    systemList.addAll(bean_SystemList.Data);
                    setHasRead(systemList.size());
                    msgAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    private void setHasRead(int count) {
        ((SystemMessage) getActivity()).setTxtHasRead(count);
    }

    private boolean isLoadMore = false;

    private void getReadList() {
        if (pageIndex == 0)
            systemList.clear();
        List<Bean_SystemList.SystemList> tempList = new ArrayList<>();
        tempList = DbManagerHelper.getInstance().getSystemList(AppPrefrence.getUsercode(getActivity()), pageIndex, pageSize);
        systemList.addAll(tempList);
        if (tempList.size() < pageSize) {
            isLoadMore = false;
        } else {
            isLoadMore = true;
        }
        systemListView.setPullLoadEnable(isLoadMore, "");
        msgAdapter.notifyDataSetChanged();
    }

    private void getHasRead() {
        Map<String, String> loginParam = new HashMap<>();
        Map<String, String> params = new HashMap<>();
        loginParam.put("Code", AppPrefrence.getUserPhone(getActivity()));
        loginParam.put("Token", AppPrefrence.getToken(getActivity()));
        params.put("PublishTime", "");
        params.put("Type", type);
        PostTools.postDataBySoap(getActivity(), "GetNoticeList", loginParam, params, handler, 0);
    }


    public void setSelectState() {
        if (isVisiable) {
            msgAdapter.setInvisiable();
            systemListView.setPullLoadEnable(isLoadMore, "");
        } else {
            msgAdapter.setVisiable();
            systemListView.setPullLoadEnable(false, "");
        }
        isVisiable = !isVisiable;
    }

    public void deleteItem() {
        List<String> delete = new ArrayList<>();
        if (isVisiable) {
            Iterator<Bean_SystemList.SystemList> iterator = systemList.iterator();
            while (iterator.hasNext()) {
                Bean_SystemList.SystemList system = iterator.next();
                delete.add(system.ID + "");
                if (system.isSelect) {
                    iterator.remove();
                }
            }
            DbManagerHelper.getInstance().deleteSystem(delete);
            setHasRead(DbManagerHelper.getInstance().getSystemCount(AppPrefrence.getUsercode(getActivity())));
            msgAdapter.notifyDataSetChanged();
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.system_msg_listview, container, false);
        }
        return view;
    }

    @Override
    public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

        List<String> delete = new ArrayList<>();
        delete.add(systemList.get(position).ID + "");
        DbManagerHelper.getInstance().deleteSystem(delete);
        setHasRead(DbManagerHelper.getInstance().getSystemCount(AppPrefrence.getUsercode(getActivity())));
        systemList.remove(position);
        msgAdapter.notifyDataSetChanged();
        return false;
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {
        pageIndex++;
        getReadList();
    }
}
