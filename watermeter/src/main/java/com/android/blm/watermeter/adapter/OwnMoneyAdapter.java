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
import android.widget.ImageView;
import android.widget.TextView;

import com.android.blm.watermeter.R;
import com.android.blm.watermeter.bean.Bean_OwnMoneyUser;
import com.android.blm.watermeter.bean.Bean_SystemList;

import java.util.ArrayList;
import java.util.List;

/**
 * author:${白曌勇} on 2016/6/1
 * TODO:
 */
public class OwnMoneyAdapter extends BaseAdapter {
    private List<Bean_OwnMoneyUser.OwnUsers> dataList;
    private Activity activity;
    private List<Integer> positionList;
    private boolean isUnread = false;

    public OwnMoneyAdapter(List<Bean_OwnMoneyUser.OwnUsers> dataList, Activity activity) {
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

    public void setItemSelect(int pos) {
        dataList.get(pos).isSelect = true;
    }

    public void setItemUnselect(int pos) {
        dataList.get(pos).isSelect = false;
    }

    public List getSelectItems() {
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).isSelect) {
                positionList.add(i);
            }
        }
        return positionList;
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
            convertView = LayoutInflater.from(activity).inflate(R.layout.own_money_user_item, null);
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

        holder.txtUserPhone.setText(ownUsers.Phone);
        holder.txtUserName.setText(ownUsers.UserName);
        holder.txtUserNo.setText(ownUsers.UserCode);
        holder.txtUserMoney.setText(ownUsers.Reserve);
        return convertView;
    }

    class Holder {
        TextView txtUserName, txtUserNo, txtUserPhone, txtUserMoney;
        CheckBox checkBox;

        public Holder(View view) {
            super();
            txtUserMoney = (TextView) view.findViewById(R.id.txt_user_money);
            txtUserName = (TextView) view.findViewById(R.id.txt_user_name);
            txtUserNo = (TextView) view.findViewById(R.id.txt_user_no);
            txtUserPhone = (TextView) view.findViewById(R.id.txt_user_phone);
            checkBox = (CheckBox) view.findViewById(R.id.check_valve);
            checkBox.setVisibility(View.GONE);
        }
    }
}
