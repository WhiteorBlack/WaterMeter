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

import com.android.blm.watermeter.R;
import com.android.blm.watermeter.adapter.OwnMoneyAdapter;
import com.android.blm.watermeter.bean.Bean_OwnMoneyUser;
import com.android.blm.watermeter.manager.OwenMoneyDetial;
import com.android.blm.watermeter.manager.OwnMoneyUsers;
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
 * TODO:未关阀用户
 */
public class OpenOwnMoneyFragment extends Fragment implements View.OnClickListener, SwipeMenuListView.OnMenuItemClickListener, LJListView.IXListViewListener {
    private View view;
    LJListView listUser;
    private LinearLayout llBottom;
    private List<Bean_OwnMoneyUser.OwnUsers> userList;
    private List<Bean_OwnMoneyUser.OwnUsers> selectList;
    private OwnMoneyAdapter openAdapter;
    private int selectPos = 0;
    private int type = 0;
    private String userName = "", userNo = "", userPhone = "", meterNo = "", doorNo = "";

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (userList == null) {
            userList = new ArrayList<>();
            selectList = new ArrayList<>();
            openAdapter = new OwnMoneyAdapter(userList, getActivity());
            listUser.setAdapter(openAdapter);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        SVProgressHUD.showWithStatus(getActivity(), "加载中...", SVProgressHUD.SVProgressHUDMaskType.Clear);
        getUserData();
        Tools.debug("attach");
    }

    public void getData() {
        inVisableBottom();
        clearParam();
        pageIndex = 1;
        SVProgressHUD.showWithStatus(getActivity(), "记载中...", SVProgressHUD.SVProgressHUDMaskType.Clear);
        getUserData();
    }

    public boolean isOpen = false;

    public void updateBottomState() {
        if (isOpen) {
            llBottom.setVisibility(View.GONE);
            openAdapter.setInvisiable();
            listUser.setPullLoadEnable(isLoadMore, "");
        } else {
            openAdapter.setVisiable();
            llBottom.setVisibility(View.VISIBLE);
            listUser.setPullLoadEnable(false, "");
        }
        isOpen = !isOpen;
    }

    public void inVisableBottom(){
        isOpen=false;
        llBottom.setVisibility(View.GONE);
        openAdapter.setInvisiable();
        listUser.setPullLoadEnable(isLoadMore, "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.close_own_money_fragment, null);
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
                openItem.setTitle("关阀");
                openItem.setTitleSize(16);
                openItem.setTitleColor(Color.WHITE);
                openItem.setIcon(R.mipmap.close_valve);
                menu.addMenuItem(openItem);
            }
        };
        listUser = (LJListView) view.findViewById(R.id.list_own_user);
        listUser.setMenuCreator(creator);
        listUser.setPullLoadEnable(false, "");
        listUser.setPullRefreshEnable(false);
        listUser.setXListViewListener(this);
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
                bundle.putBoolean("isOpen", true);
                startActivityForResult(new Intent(getActivity(), OwenMoneyDetial.class).putExtra("data", bundle), 0);
            }
        });

        listUser.setOnMenuItemClickListener(this);
        llBottom = (LinearLayout) view.findViewById(R.id.ll_bottom);
        llBottom.setVisibility(View.GONE);
        llBottom.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 1 && data != null) {
            Bundle bundle = data.getBundleExtra("result");
            if (bundle != null) {
                userList.remove(selectPos);
                Bean_OwnMoneyUser.OwnUsers ownUsers = new Bean_OwnMoneyUser.OwnUsers();
                ownUsers.isSelect = false;
                ownUsers.isVisiable = false;
                ownUsers.Address = bundle.getString("Address");
                ownUsers.Doorplate = bundle.getString("Doorplate");
                ownUsers.DeptName = bundle.getString("MergeDeptName");
                ownUsers.MeterAddr = bundle.getString("MeterAddr");
                ownUsers.MeterType = bundle.getString("MeterTypeName");
                ownUsers.Phone = bundle.getString("Phone");
                ownUsers.Reserve = bundle.getString("Reserve");
                ownUsers.UserCode = bundle.getString("UserCode");
                ownUsers.UserName = bundle.getString("UserName");
                ownUsers.ValveName = bundle.getString("ValveName");
                ownUsers.ValveStatus = bundle.getString("ValveStatus");
                ownUsers.isSelect = false;
                List<Bean_OwnMoneyUser.OwnUsers> users = new ArrayList<Bean_OwnMoneyUser.OwnUsers>();
                users.add(ownUsers);
                ((OwnMoneyUsers) getActivity()).setArgument(users);
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


    private int progress;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_bottom:
                //关阀
                progress = 0;

                type = 1;
                for (int i = 0; i < userList.size(); i++) {
                    if (userList.get(i).isSelect) {
                        Bean_OwnMoneyUser.OwnUsers ownUsers = userList.get(i);
                        ownUsers.position = i;
                        selectList.add(ownUsers);
                    }
                }
                if (selectList == null || selectList.size() == 0) {
                    Tools.toastMsg(getActivity(), "请选择要关阀的用户!");
                    return;
                }
                selectCount = selectList.size();
                SVProgressHUD.showWithProgress(getActivity(), progress + "/" + selectCount, SVProgressHUD.SVProgressHUDMaskType.Clear);
                SVProgressHUD.getProgressBar(getActivity()).setMax(selectList.size());
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


    private Bean_OwnMoneyUser allUsers;
    private int index = 0;
    Handler allhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            index++;
            String result = (String) msg.obj;
            Tools.debug("open"+result);
            if (TextUtils.isEmpty(result)) {
                Tools.toastMsg(getActivity(), "请检查网络后重试!");
                SVProgressHUD.dismiss(getActivity());
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

    public void getAllData(String keyword) {

        String code = AppPrefrence.getUserPhone(getActivity());
        Tools.debug(code);
        Map<String, String> loginInfo = new HashMap<>();
        loginInfo.put("Code", AppPrefrence.getUserPhone(getActivity()));
        loginInfo.put("Token", AppPrefrence.getToken(getActivity()));
        Map<String, String> params = new HashMap<>();
        params.put("DataType", "2");
        params.put("ValveStatus", "1");
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
        doorNo="";
        meterNo = "";
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
                if (TextUtils.equals("1", bean_ownMoneyUser.Result) && bean_ownMoneyUser.Data != null) {
                    userList.addAll(bean_ownMoneyUser.Data);
                    if (bean_ownMoneyUser.Data.size() < pageSize) {
                        isLoadMore = false;
                    } else isLoadMore = true;
                } else {
                    isLoadMore = false;
                }
                SVProgressHUD.dismiss(getActivity());
            }

            if (msg.what == 1) {

                try {
                    JSONObject object = new JSONObject(result);
                    if (object != null && TextUtils.equals(object.getString("Result"), "1")) {
                        dealResult();
                    } else {
                        selectList.remove(position);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    selectList.remove(position);
                }
                if (type == 1) {
                    progress++;
                    SVProgressHUD.getProgressBar(getActivity()).setProgress(progress);
                    SVProgressHUD.setText(getActivity(), "关阀" + progress + "/" + selectCount);
                }
                if (position < selectList.size()) {
                    updateValveState(selectList.get(position).MeterAddr);
                } else {
                    //操作结束
//                    ((OwnMoneyUsers) getActivity()).setArgument(selectList);
                    progress = 0;
                    SVProgressHUD.dismiss(getActivity());
                    SVProgressHUD.getProgressBar(getActivity()).setProgress(0);
                    selectList.clear();
                    position = 0;
                }
            }
            listUser.setPullLoadEnable(isLoadMore, "");
            openAdapter.notifyDataSetChanged();

        }
    };

    /**
     * 处理执行结果,如果是单个操作则正常更改,如果为批量操作,则采用循环线程方式
     */
    private int selectCount;

    private void dealResult() {
        if (type == 0) {
//            List<Bean_OwnMoneyUser.OwnUsers> users = new ArrayList<>();
//            Bean_OwnMoneyUser.OwnUsers ownUser = userList.get(selectPos);
//            ownUser.isSelect = false;
//            ownUser.isVisiable = false;
//            users.add(ownUser);
//            ((OwnMoneyUsers) getActivity()).setArgument(users);
            userList.remove(selectPos);
            SVProgressHUD.dismiss(getActivity());
        } else {
            userList.remove(selectList.get(position).position - position);
            selectList.get(position).isSelect = false;
            selectList.get(position).isVisiable = false;
            position++;
        }

        openAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
        SVProgressHUD.showWithStatus(getActivity(), "关阀中...", SVProgressHUD.SVProgressHUDMaskType.Clear);
        type = 0;
        updateValveState(userList.get(position).MeterAddr);
        selectPos = position;
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

    private int pageIndex = 1, pageSize = 20;
    private boolean isLoadMore = false;

    public void getUserData() {

        Map<String, String> loginInfo = new HashMap<>();
        loginInfo.put("Code", AppPrefrence.getUserPhone(getActivity()));
        loginInfo.put("Token", AppPrefrence.getToken(getActivity()));
        Map<String, String> params = new HashMap<>();
        params.put("Doorplate", doorNo);
        params.put("DataType", "2");
        params.put("ValveStatus", "1");
        params.put("UserCode", userNo);
        params.put("UserName", userName);
        params.put("Phone", userPhone);
        params.put("MeterAddr", meterNo);
        params.put("PageSize", pageSize + "");
        params.put("PageIndex", pageIndex + "");
        PostTools.postDataBySoap(getContext(), "GetUserInfo", loginInfo, params, handler, 0);
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
