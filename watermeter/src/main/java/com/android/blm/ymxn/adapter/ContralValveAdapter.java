package com.android.blm.ymxn.adapter;/**
 * Created by Administrator on 2016/6/1.
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.android.blm.ymxn.R;
import com.android.blm.ymxn.bean.Bean_OwnMoneyUser;

import java.util.ArrayList;
import java.util.List;

/**
 * author:${白曌勇} on 2016/6/1
 * TODO:
 */
public class ContralValveAdapter extends BaseAdapter {
    private List<Bean_OwnMoneyUser.OwnUsers> dataList;
    private Activity activity;
    private List<Integer> positionList;

    public ContralValveAdapter(List<Bean_OwnMoneyUser.OwnUsers> dataList, Activity activity) {
        this.dataList = dataList;
        this.activity = activity;
        positionList = new ArrayList<>();
    }

    public void setVisiable() {
        for (int i = 0; i < dataList.size(); i++) {
            dataList.get(i).isVisiable = true;
        }
        notifyDataSetChanged();
    }

    public void setInvisiable() {
        for (int i = 0; i < dataList.size(); i++) {
            dataList.get(i).isVisiable = false;
            dataList.get(i).isSelect = false;
        }
        notifyDataSetChanged();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final Holder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.open_valve_item, null);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        final Bean_OwnMoneyUser.OwnUsers ownUsers = dataList.get(position);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ownUsers.isSelect = isChecked;
            }
        });

        if (ownUsers.isVisiable) {
            holder.checkBox.setVisibility(View.VISIBLE);
        } else holder.checkBox.setVisibility(View.GONE);

        holder.checkBox.setChecked(ownUsers.isSelect);

        holder.txtDoorNo.setText(ownUsers.Doorplate);
        holder.txtMeterNo.setText(ownUsers.MeterAddr+"  "+ownUsers.MeterType);
        holder.txtUserName.setText(ownUsers.UserName+"  "+ownUsers.UserCode+"  "+ownUsers.Phone);
        holder.txtUserSplot.setText("所属小区  "+ownUsers.DeptName);
        return convertView;
    }

    class Holder {
        private TextView txtUserName;
        private TextView txtDoorNo;
        private TextView txtUserSplot;
        private TextView txtMeterNo;
        private CheckBox checkBox;

        public Holder(View view) {
            super();
            txtMeterNo = (TextView) view.findViewById(R.id.txt_meter_no);
            txtUserName = (TextView) view.findViewById(R.id.txt_name);
            txtDoorNo = (TextView) view.findViewById(R.id.txt_door_no);
            txtUserSplot = (TextView) view.findViewById(R.id.txt_user_splot);
            checkBox = (CheckBox) view.findViewById(R.id.check_valve);
            checkBox.setVisibility(View.GONE);
        }
    }
}
