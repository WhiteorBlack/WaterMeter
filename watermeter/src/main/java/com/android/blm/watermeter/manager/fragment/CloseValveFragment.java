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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.blm.watermeter.R;
import com.android.blm.watermeter.adapter.ContralValveAdapter;
import com.android.blm.watermeter.bean.Bean_OwnMoneyUser;
import com.android.blm.watermeter.manager.ContralValve;
import com.android.blm.watermeter.manager.OwenMoneyDetial;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author:${白曌勇} on 2016/6/18
 * TODO:
 */
public class CloseValveFragment extends Fragment implements View.OnClickListener, SwipeMenuListView.OnMenuItemClickListener, LJListView.IXListViewListener {
    private View view;
    LJListView listUser;
    private LinearLayout llBottom;
    private List<Bean_OwnMoneyUser.OwnUsers> userList;
    //    private List<Bean_OwnMoneyUser.OwnUsers> totalList;
    private List<Bean_OwnMoneyUser.OwnUsers> selectList;
    private ContralValveAdapter openAdapter;
    private int selectPos = 0;
    private int type = 0;
    private int pageIndex = 1, pageSize = 20;
    private boolean isLoadMore = false;
    private int progress = 0, selectCount;
    private String userName = "", userNo = "", userPhone = "", meterNo = "", doorNo = "";

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (userList == null) {
            userList = new ArrayList<>();
//            totalList = new ArrayList<>();
            selectList = new ArrayList<>();
            openAdapter = new ContralValveAdapter(userList, getActivity());
            listUser.setAdapter(openAdapter);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//
//        getUserData();
//        Tools.debug("attach");
    }

    public void getData() {
        llBottom.setVisibility(View.GONE);
        setInvisiable();
        listUser.setPullLoadEnable(isLoadMore, "");
        isOpen = false;
        clearParam();
        pageIndex = 1;
        SVProgressHUD.showWithStatus(getActivity(), "记载中...", SVProgressHUD.SVProgressHUDMaskType.Clear);
        getUserData();
    }

    public boolean isOpen = false;

    public void updateBottomState() {
        if (isOpen) {
            llBottom.setVisibility(View.GONE);
            setInvisiable();
            listUser.setPullLoadEnable(isLoadMore, "");
        } else {
            setVisiable();
            llBottom.setVisibility(View.VISIBLE);
            if (isLoadMore) {
                listUser.setPullLoadEnable(false, "");
            }
        }
        isOpen = !isOpen;
    }

    private void setVisiable() {
        for (int i = 0; i < userList.size(); i++) {
            userList.get(i).isVisiable = true;
        }
        llBottom.setVisibility(View.VISIBLE);
        openAdapter.notifyDataSetChanged();
    }

    private void setInvisiable() {
        for (int i = 0; i < userList.size(); i++) {
            userList.get(i).isVisiable = false;
        }
        llBottom.setVisibility(View.GONE);
        openAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.recyclerview_button, null);
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
                openItem.setTitle("开阀");
                openItem.setTitleSize(16);
                openItem.setTitleColor(Color.WHITE);
                openItem.setIcon(R.mipmap.open_valve);
                menu.addMenuItem(openItem);
            }
        };
        listUser = (LJListView) view.findViewById(R.id.recyclerView);
        listUser.setMenuCreator(creator);
        listUser.setPullLoadEnable(false, "");
        listUser.setXListViewListener(this);
        listUser.setPullRefreshEnable(false);
        listUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                type = 0;
                selectPos = position - 1;
                Bean_OwnMoneyUser.OwnUsers ownUsers = userList.get(position - 1);
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

                bundle.putBoolean("isOpen", false);
                startActivityForResult(new Intent(getActivity(), OwenMoneyDetial.class).putExtra("data", bundle).putExtra("isContral", true), 0);
            }
        });
        listUser.getmListView().setDivider(null);
        listUser.setOnMenuItemClickListener(this);
        ((TextView) view.findViewById(R.id.txt_bottom)).setText("开阀");
        view.findViewById(R.id.img_bottom).setBackgroundResource(R.mipmap.open_valve);
        llBottom = (LinearLayout) view.findViewById(R.id.ll_bottom);
        llBottom.setVisibility(View.GONE);
        llBottom.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_bottom:
                //关阀
                type = 1;
                for (int i = 0; i < userList.size(); i++) {
                    if (userList.get(i).isSelect) {
                        Bean_OwnMoneyUser.OwnUsers ownUsers = userList.get(i);
                        ownUsers.position = i;
                        selectList.add(ownUsers);
                    }
                }
                if (selectList == null || selectList.size() == 0) {
                    Tools.toastMsg(getActivity(), "请选择要开阀的用户!");
                    return;
                }
                selectCount = selectList.size();
                SVProgressHUD.showWithProgress(getActivity(), "开阀" + progress + "/" + selectCount, SVProgressHUD.SVProgressHUDMaskType.Clear);
                SVProgressHUD.getProgressBar(getActivity()).setMax(selectCount);
                updateValveState(selectList.get(position).MeterAddr);
                break;
        }
    }

    private String keyword="";
    private boolean isFirst = true;

    public void searchUser(int type, String keyword) {
        this.type = type;
        this.keyword = keyword;
        pageIndex = 1;
        SVProgressHUD.showWithStatus(getActivity(), "搜索中...", SVProgressHUD.SVProgressHUDMaskType.Clear);
        clearParam();
        switch (type) {
            case 0:
                //全部
                isFirst = true;
                getAllData(keyword);
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
                doorNo=keyword;
                break;

        }
        if (type != 0)
            getUserData();
    }

    private int index = 0;
    private Bean_OwnMoneyUser allUsers;
    Handler allhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            index++;
            String result = (String) msg.obj;
            Tools.debug("close" + result);
            if (TextUtils.isEmpty(result)) {
                SVProgressHUD.dismiss(getActivity());
                Tools.toastMsg(getActivity(), "请检查网络后重试!");
                return;
            }
            if (pageIndex == 1) {
                userList.clear();
            }
            allUsers = new Gson().fromJson(result, Bean_OwnMoneyUser.class);
            if (allUsers != null && TextUtils.equals(allUsers.Result, "1") && allUsers.Data != null) {
                userList.addAll(allUsers.Data);
                if (allUsers.Data.size() < pageSize) {

                } else isLoadMore = true;
            }
            openAdapter.notifyDataSetChanged();
            SVProgressHUD.dismiss(getActivity());
            listUser.setPullLoadEnable(isLoadMore, "");

        }
    };

//    private void getAllData(String keyword) {
//        index = 0;
//        isLoadMore = false;
//        getAllData(keyword, "", "", "");
//        getAllData("", keyword, "", "");
//        getAllData("", "", keyword, "");
//        getAllData("", "", "", keyword);
//    }

    public void getAllData(String keyword) {

        String code = AppPrefrence.getUserPhone(getActivity());
        Tools.debug(code);
        Map<String, String> loginInfo = new HashMap<>();
        loginInfo.put("Code", AppPrefrence.getUserPhone(getActivity()));
        loginInfo.put("Token", AppPrefrence.getToken(getActivity()));
        Map<String, String> params = new HashMap<>();
        params.put("Doorplate", "");
        params.put("DataType", "1");
        params.put("ValveStatus", "2");
        params.put("UserCode", "");
        params.put("UserName", "");
        params.put("Phone", "");
        params.put("MeterAddr", "");
        params.put("All", keyword);
        params.put("PageSize", "" + pageSize);
        params.put("PageIndex", pageIndex + "");
        PostTools.postDataBySoap(getContext(), "GetUserInfo", loginInfo, params, allhandler, 0);
    }

    private void clearParam() {
        userName = "";
        userNo = "";
        userPhone = "";
        meterNo = "";
        doorNo = "";
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
                Tools.toastMsg(getActivity(), "请检查网络后重试!");
                return;
            }
            if (msg.what == 0) {
                if (pageIndex == 1) {
                    userList.clear();
                }

                bean_ownMoneyUser = new Gson().fromJson(result, Bean_OwnMoneyUser.class);
                if (TextUtils.equals("1", bean_ownMoneyUser.Result) && bean_ownMoneyUser.Data != null && bean_ownMoneyUser.Data.size() > 0) {
                    userList.addAll(bean_ownMoneyUser.Data);
                    if (bean_ownMoneyUser.Data.size() < pageSize) {
                        isLoadMore = false;
                    } else isLoadMore = true;
                } else {
                    isLoadMore = false;
                }
                listUser.setPullLoadEnable(isLoadMore, "");
                SVProgressHUD.dismiss(getActivity());
            }

            if (msg.what == 1) {

                try {
                    JSONObject object = new JSONObject(result);
                    if (object != null && TextUtils.equals(object.getString("Result"), "1")) {
                        dealResult();
                    } else {
                        Tools.toastMsg(getActivity(), object.getString("Message"));
                        selectList.remove(position);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Tools.debug(e.toString());
                    if (selectList != null && selectList.size() > 0)
                        selectList.remove(position);
                }
                if (type == 1) {
                    progress++;
                    SVProgressHUD.getProgressBar(getActivity()).setProgress(progress);
                    SVProgressHUD.setText(getActivity(), "开阀" + progress + "/" + selectCount);
                }
                if (position < selectList.size()) {
                    updateValveState(selectList.get(position).MeterAddr);
                } else {
                    //操作结束
//                    ((ContralValve) getActivity()).setArgument(selectList);
                    SVProgressHUD.dismiss(getActivity());
                    SVProgressHUD.getProgressBar(getActivity()).setProgress(0);
                    progress = 0;
                    position = 0;
                    selectList.clear();
                }
            }
            openAdapter.notifyDataSetChanged();
        }
    };

    /**
     * 处理执行结果,如果是单个操作则正常更改,如果为批量操作,则采用循环线程方式
     */

    private void dealResult() {
        if (type == 0) {
//            List<Bean_OwnMoneyUser.OwnUsers> users = new ArrayList<>();
//            Bean_OwnMoneyUser.OwnUsers ownUser = userList.get(selectPos);
//
//            users.add(ownUser);
//            ((ContralValve) getActivity()).setArgument(users);
            userList.remove(selectPos);
        } else {
            userList.remove(selectList.get(position).position - position);

            position++;
        }

        openAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
        type = 0;
        SVProgressHUD.showWithStatus(getActivity(), "开阀中...", SVProgressHUD.SVProgressHUDMaskType.Clear);
        updateValveState(userList.get(position).MeterAddr);
        return false;
    }

    private void updateValveState(String meterAddr) {
        Map<String, String> loginInfo = new HashMap<>();
        loginInfo.put("Code", AppPrefrence.getUserPhone(getActivity()));
        loginInfo.put("Token", AppPrefrence.getToken(getActivity()));

        Map<String, String> params = new HashMap<>();
        params.put("MeterAddr", meterAddr);
        params.put("ValveStatus", "1");
        PostTools.postDataBySoap(getContext(), "ControlValve", loginInfo, params, handler, 1);
    }

    public void getUserData() {
        Map<String, String> loginInfo = new HashMap<>();
        loginInfo.put("Code", AppPrefrence.getUserPhone(getActivity()));
        loginInfo.put("Token", AppPrefrence.getToken(getActivity()));
        Map<String, String> params = new HashMap<>();
        params.put("Doorplate", doorNo);
        params.put("DataType", "1");
        params.put("ValveStatus", "2");
        params.put("UserCode", userNo);
        params.put("UserName", userName);
        params.put("Phone", userPhone);
        params.put("MeterAddr", meterNo);
        params.put("PageSize", pageSize + "");
        params.put("PageIndex", pageIndex + "");
        PostTools.postDataBySoap(getContext(), "GetUserInfo", loginInfo, params, handler, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 1 && data != null) {
            Bundle bundle = data.getBundleExtra("result");
            if (bundle != null) {

//                for (int i = 0; i < totalList.size(); i++) {
//                    if (TextUtils.equals(bundle.getString("MeterAddr"), totalList.get(i).MeterAddr)) {
//                        totalList.remove(i);
//                        break;
//                    }
//                }
                userList.remove(selectPos);
//                Bean_OwnMoneyUser.OwnUsers ownUsers = new Bean_OwnMoneyUser.OwnUsers();
//                ownUsers.isSelect = false;
//                ownUsers.isVisiable = false;
//                ownUsers.Address = bundle.getString("Address");
//                ownUsers.Doorplate = bundle.getString("Doorplate");
//                ownUsers.DeptName = bundle.getString("MergeDeptName");
//                ownUsers.MeterAddr = bundle.getString("MeterAddr");
//                ownUsers.MeterType = bundle.getString("MeterTypeName");
//                ownUsers.Phone = bundle.getString("Phone");
//                ownUsers.Reserve = bundle.getString("Reserve");
//                ownUsers.UserCode = bundle.getString("UserCode");
//                ownUsers.UserName = bundle.getString("UserName");
//                ownUsers.ValveName = bundle.getString("ValveName");
//                ownUsers.ValveStatus = bundle.getString("ValveStatus");
//                ownUsers.isSelect = false;
//                List<Bean_OwnMoneyUser.OwnUsers> users = new ArrayList<Bean_OwnMoneyUser.OwnUsers>();
//                users.add(ownUsers);
//                ((ContralValve) getActivity()).setArgument(users);
                openAdapter.notifyDataSetChanged();
            }
        }
    }

    public void addData(List<Bean_OwnMoneyUser.OwnUsers> ownUserses) {
        if (userList != null && openAdapter != null) {
            for (int i = 0; i < ownUserses.size(); i++) {
                ownUserses.get(i).isSelect = isOpen;
                ownUserses.get(i).isVisiable = isOpen;
            }
            userList.addAll(ownUserses);
            openAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {
        pageIndex++;
        if (type == 0)
            getAllData(keyword);
        else getUserData();
    }
}
