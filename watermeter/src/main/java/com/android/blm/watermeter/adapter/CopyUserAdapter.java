package com.android.blm.watermeter.adapter;/**
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

import com.android.blm.watermeter.R;
import com.android.blm.watermeter.bean.Bean_OwnMoneyUser;

import java.util.ArrayList;
import java.util.List;

/**
 * author:${白曌勇} on 2016/6/1
 * TODO:
 */
public class CopyUserAdapter extends BaseAdapter {
    private List<Bean_OwnMoneyUser.OwnUsers> dataList;
    private Activity activity;
    private List<Integer> positionList;

    public CopyUserAdapter(List<Bean_OwnMoneyUser.OwnUsers> dataList, Activity activity) {
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

    public void updateCheck(boolean isCheck) {

        for (int i = 0; i < dataList.size(); i++) {
            dataList.get(i).isSelect = isCheck;
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
        Holder holder;
        if (convertView == null) {

            convertView = LayoutInflater.from(activity).inflate(R.layout.copy_user_item, null);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        final Bean_OwnMoneyUser.OwnUsers systemList = dataList.get(position);
        holder.chbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                systemList.isSelect = isChecked;
            }
        });
        if (systemList.isVisiable) {
            holder.chbSelect.setVisibility(View.VISIBLE);
            holder.chbSelect.setChecked(systemList.isSelect);
        } else {
            holder.chbSelect.setVisibility(View.GONE);
        }


        holder.txtMeterType.setText(systemList.MeterType);
        holder.txtMeterNo.setText(systemList.MeterAddr);
        holder.txtDoorNo.setText(systemList.Doorplate);
        holder.txtDate.setText(systemList.LastReadDate);

        return convertView;
    }

    class Holder {
        public TextView txtDoorNo, txtMeterNo, txtMeterType, txtDate;
        public CheckBox chbSelect;

        public Holder(View view) {
            super();
            txtDate = (TextView) view.findViewById(R.id.txt_copy_date);
            txtDoorNo = (TextView) view.findViewById(R.id.txt_door_no);
            txtMeterNo = (TextView) view.findViewById(R.id.txt_meter_no);
            txtMeterType = (TextView) view.findViewById(R.id.txt_meter_type);
            chbSelect = (CheckBox) view.findViewById(R.id.checkbox);
            chbSelect.setVisibility(View.GONE);
        }
    }
}
