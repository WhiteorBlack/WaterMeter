package com.android.blm.ymxn.fragment;/**
 * Created by Administrator on 2016/6/2.
 */

import android.content.Context;
import android.content.Intent;
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
import com.android.blm.ymxn.widget.loading.SVProgressHUD;
import com.android.blm.ymxn.widget.swipelistview.LJListView;
import com.android.blm.ymxn.widget.swipelistview.SwipeMenu;
import com.android.blm.ymxn.widget.swipelistview.SwipeMenuListView;
import com.google.gson.Gson;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * author:${白曌勇} on 2016/6/2
 * TODO:
 */
public class SystemUnreadFragment extends Fragment implements SwipeMenuListView.OnMenuItemClickListener {
    private View view;
    private LJListView systemListView;
    private List<Bean_SystemList.SystemList> systemList;
    private SystemMsgAdapter msgAdapter;
    private boolean isVisiable = false;
    private String type;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (systemList == null) {
            systemList = new ArrayList<>();
            msgAdapter = new SystemMsgAdapter(systemList, getActivity(), true);
        }

        systemListView = (LJListView) view.findViewById(R.id.ljlistview);
        systemListView.setOnMenuItemClickListener(this);
        systemListView.setPullLoadEnable(false, "");
        systemListView.setPullRefreshEnable(false);
        systemListView.setAdapter(msgAdapter);
        systemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateState(systemList.get(position - 1).ID + "");
                Bundle bundle = new Bundle();
                bundle.putString("title", systemList.get(position - 1).Title);
                bundle.putString("date", systemList.get(position - 1).PublishTime);
                bundle.putString("content", systemList.get(position - 1).Content);
                startActivity(new Intent(getActivity(), SystemMsgDetial.class).putExtra("detial", bundle));
                systemList.remove(position - 1);
                setUnread(systemList.size());
                msgAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        SVProgressHUD.showWithStatus(getActivity(), "加载中...", SVProgressHUD.SVProgressHUDMaskType.Clear);
        getHasRead();
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
                    for (int i = 0; i < bean_SystemList.Data.size(); i++) {
                        Tools.debug("sys" + DbManagerHelper.getInstance().getMsgTime(AppPrefrence.getUsercode(getContext())) + "getdata" + Tools.dateToLong(bean_SystemList.Data.get(i).PublishTime));
                        if (DbManagerHelper.getInstance().getMsgTime(AppPrefrence.getUsercode(getContext())) < Tools.dateToLong(bean_SystemList.Data.get(i).PublishTime)) {
                            systemList.add(bean_SystemList.Data.get(i));
                        }
                    }
//                    systemList.addAll(bean_SystemList.Data);
                    setUnread(systemList.size());
                    DbManagerHelper.getInstance().saveSystemList(systemList, AppPrefrence.getUsercode(getActivity()));
                }
            }
            setHasRead(DbManagerHelper.getInstance().getSystemCount(AppPrefrence.getUsercode(getActivity())));
            msgAdapter.notifyDataSetChanged();
            SVProgressHUD.dismiss(getActivity());
        }
    };


    private void getHasRead() {
        Map<String, String> loginParam = new HashMap<>();
        Map<String, String> params = new HashMap<>();
        loginParam.put("Code", AppPrefrence.getUserPhone(getActivity()));
        loginParam.put("Token", AppPrefrence.getToken(getActivity()));
        params.put("Type", type);
        params.put("Flag", "1");
        params.put("PublishTime", DateTime.now().toString("yyyy-MM-dd"));
        PostTools.postDataBySoap(getActivity(), "GetNoticeList", loginParam, params, handler, 0);
    }


    public void setSelectState() {
        if (isVisiable) {
            msgAdapter.setInvisiable();
        } else {
            msgAdapter.setVisiable();
        }
        isVisiable = !isVisiable;
    }

    public void deleteItem() {
        List<String> delete = new ArrayList<>();
        if (isVisiable) {
            Iterator<Bean_SystemList.SystemList> iter = systemList.iterator();
            while (iter.hasNext()) {
                Bean_SystemList.SystemList system = iter.next();
                updateState(system.ID + "");
                delete.add(system.ID + "");
                if (system.isSelect) {
                    iter.remove();
                }
            }
//            DbManagerHelper.getInstance().deleteSystem(delete);
            setUnread(systemList.size());
//            setHasRead(DbManagerHelper.getInstance().getSystemCount(AppPrefrence.getUsercode(getActivity())));
            msgAdapter.notifyDataSetChanged();
        }
    }

    public void readItem() {
        if (isVisiable) {
            Iterator<Bean_SystemList.SystemList> iter = systemList.iterator();
            while (iter.hasNext()) {
                Bean_SystemList.SystemList system = iter.next();
                updateState(system.ID + "");
                if (system.isSelect) {
                    iter.remove();
                }
            }
            setUnread(systemList.size());
            setHasRead(DbManagerHelper.getInstance().getSystemCount(AppPrefrence.getUsercode(getActivity())));
            msgAdapter.notifyDataSetChanged();
        }
    }

    private void setHasRead(int count) {
        ((SystemMessage) getActivity()).setTxtHasRead(count);
    }

    private void setUnread(int count) {
        ((SystemMessage) getActivity()).setTxtUnread(count);
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
        updateState(systemList.get(position).ID + "");

        systemList.remove(position);
        msgAdapter.notifyDataSetChanged();
        setUnread(systemList.size());
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
