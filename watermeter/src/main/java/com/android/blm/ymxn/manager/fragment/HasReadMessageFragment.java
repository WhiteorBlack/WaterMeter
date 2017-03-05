package com.android.blm.ymxn.manager.fragment;/**
 * Created by Administrator on 2016/6/18.
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
import com.android.blm.ymxn.adapter.SystemWarningAdapter;
import com.android.blm.ymxn.bean.Bean_SystemWarning;
import com.android.blm.ymxn.db.DbManagerHelper;
import com.android.blm.ymxn.manager.SystemWarningDetial;
import com.android.blm.ymxn.utils.AppPrefrence;
import com.android.blm.ymxn.utils.PostTools;
import com.android.blm.ymxn.utils.Tools;
import com.android.blm.ymxn.widget.XListView;
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
public class HasReadMessageFragment extends Fragment implements View.OnClickListener, XListView.IXListViewListener {
    private View view;
    private XListView listUser;
    private List<Bean_SystemWarning.SystemWarnings> userList;
    private SystemWarningAdapter unreadAdapter;
    private int pageIndex = 0, pageSize = 20;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (userList == null) {
            userList = new ArrayList<>();
            unreadAdapter = new SystemWarningAdapter(userList, getActivity(), false);
            listUser.setAdapter(unreadAdapter);
        }
        if (pageIndex == 0)
            getReadData();
    }

    private boolean isLoadMore = false;

    private void getReadData() {
        List<Bean_SystemWarning.SystemWarnings> tempList = new ArrayList<>();
        tempList = DbManagerHelper.getInstance().getWarningList(AppPrefrence.getUsercode(getActivity()), pageIndex, pageSize);
        if (tempList.size() < pageSize)
            isLoadMore = false;
        else isLoadMore = true;
        userList.addAll(tempList);
        unreadAdapter.notifyDataSetChanged();
        listUser.setPullLoadEnable(isLoadMore);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    Bean_SystemWarning bean_SystemList;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String) msg.obj;
            Tools.debug("hasRead" + result);
            if (TextUtils.isEmpty(result)) {
                Tools.toastMsg(getActivity(), "请检查网络后重试");
            } else {
                bean_SystemList = new Gson().fromJson(result, Bean_SystemWarning.class);
                if (bean_SystemList != null && TextUtils.equals(bean_SystemList.Result, "1") && bean_SystemList.Data != null && bean_SystemList.Data.size() > 0) {
                    userList.addAll(bean_SystemList.Data);
                    unreadAdapter.notifyDataSetChanged();
                }
            }
        }
    };


    String type = "";

    private void getHasRead() {
        Map<String, String> loginParam = new HashMap<>();
        Map<String, String> params = new HashMap<>();
        loginParam.put("Code", AppPrefrence.getUserPhone(getActivity()));
        loginParam.put("Token", AppPrefrence.getToken(getActivity()));
        params.put("AlarmTime", DateTime.now().toString("yyyy-MM-dd"));
        params.put("Type", type);
        PostTools.postDataBySoap(getActivity(), "GetNoticeList", loginParam, params, handler, 0);
    }

    public void getSearch(String type) {
        this.type = type;
        getHasRead();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.system_warning_hasread_listview, null);
            initView();
        }
        return view;
    }

    private void initView() {
        listUser = (XListView) view.findViewById(R.id.list_has_read);
        listUser.setPullLoadEnable(false);
        listUser.setPullRefreshEnable(false);
        listUser.setXListViewListener(this);
        listUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bean_SystemWarning.SystemWarnings systemList = userList.get(position - 1);
                Bundle bundle = new Bundle();
                bundle.putString("Title", "");
                bundle.putString("Content", systemList.Content);
                bundle.putString("PublishTime", systemList.AlarmTime);
                startActivityForResult(new Intent(getActivity(), SystemWarningDetial.class).putExtra("data", bundle), 0);
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_bottom:
                //关阀

                break;
        }
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {
        pageIndex++;
        getReadData();
    }
}
