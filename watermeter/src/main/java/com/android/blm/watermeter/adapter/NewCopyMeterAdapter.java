package com.android.blm.watermeter.adapter;/**
 * Created by Administrator on 2016/5/25.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.blm.watermeter.R;
import com.android.blm.watermeter.bean.Bean_CopyMeter;
import com.android.blm.watermeter.bean.Bean_OwnMoneyUser;
import com.android.blm.watermeter.manager.CopyMeterDetial;
import com.android.blm.watermeter.utils.Tools;
import com.android.blm.watermeter.widget.swipelistview.DensityUtil;
import com.android.blm.watermeter.widget.swipelistview.LJListView;
import com.android.blm.watermeter.widget.swipelistview.SwipeMenu;
import com.android.blm.watermeter.widget.swipelistview.SwipeMenuCreator;
import com.android.blm.watermeter.widget.swipelistview.SwipeMenuItem;
import com.android.blm.watermeter.widget.swipelistview.SwipeMenuListView;

import java.util.List;

/**
 * author:${白曌勇} on 2016/5/25
 * TODO:
 */
public class NewCopyMeterAdapter extends BaseAdapter {
    private Activity activity;
    private List<Bean_CopyMeter.CopyMeters> dataList;
    private OnItemClickListener onItemClickListener;

    public NewCopyMeterAdapter(Activity activity, List<Bean_CopyMeter.CopyMeters> dataList) {
        this.activity = activity;
        this.dataList = dataList;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.copy_meter_item, parent, false);
            mHolder = new ViewHolder(convertView);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        final Bean_CopyMeter.CopyMeters copyMeters = dataList.get(position);
        final CopyUserAdapter userAdapter = new CopyUserAdapter(copyMeters.User, activity);
        mHolder.listView.setAdapter(userAdapter);
        if (copyMeters.isVisiable) {
            mHolder.checkBox.setVisibility(View.VISIBLE);
            mHolder.checkBox.setChecked(false);
            userAdapter.setVisiable();
        } else {
            userAdapter.setInvisiable();
            mHolder.checkBox.setVisibility(View.GONE);
        }

        mHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                userAdapter.updateCheck(isChecked);
            }
        });

        mHolder.listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int childposition, SwipeMenu menu, int index) {
                onItemClickListener.onCopyMeterClick(menu, position, childposition);
                return false;
            }
        });

        mHolder.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int positionC, long id) {
                Bean_OwnMoneyUser.OwnUsers ownUsers = copyMeters.User.get(positionC - 1);
                Bundle bundle = new Bundle();
                bundle.putInt("parent", position);
                bundle.putInt("child", positionC - 1);
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
                activity.startActivityForResult(new Intent(activity, CopyMeterDetial.class).putExtra("data", bundle), 0);
            }
        });
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mHolder.listView.getLayoutParams();
        params.height = Tools.dip2px(activity, 46) * copyMeters.User.size();
        mHolder.listView.setLayoutParams(params);

        userAdapter.notifyDataSetChanged();
        mHolder.txtSplot.setText("所属小区:  " + copyMeters.DeptName);
        return convertView;
    }

    public interface OnItemClickListener {

        void onCopyMeterClick(SwipeMenu menu, int parentPos, int childPos);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtSplot;
        LJListView listView;
        CheckBox checkBox;

        public ViewHolder(View view) {
            super(view);
            txtSplot = (TextView) view.findViewById(R.id.txt_plot);
            SwipeMenuCreator creator = new SwipeMenuCreator() {
                @Override
                public void create(SwipeMenu menu) {
                    SwipeMenuItem openItem = new SwipeMenuItem(activity);
                    openItem.setBackground(new ColorDrawable(activity.getResources().getColor(R.color.orange)));
                    openItem.setWidth(DensityUtil.dip2px(activity, 85));
                    openItem.setTitle("抄表");
                    openItem.setTitleSize(16);
                    openItem.setTitleColor(Color.WHITE);
                    openItem.setIcon(R.mipmap.copy_meter);
                    menu.addMenuItem(openItem);
                }
            };
            listView = (LJListView) view.findViewById(R.id.list_meter);
            listView.setMenuCreator(creator);
            checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            checkBox.setVisibility(View.GONE);
        }
    }
}
