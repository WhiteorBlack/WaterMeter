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
import com.android.blm.watermeter.bean.Bean_SystemList;

import java.util.ArrayList;
import java.util.List;

/**
 * author:${白曌勇} on 2016/6/1
 * TODO:
 */
public class SystemMsgAdapter extends BaseAdapter {
    private List<Bean_SystemList.SystemList> dataList;
    private Activity activity;
    private List<Integer> positionList;
    private boolean isUnread = false;

    public SystemMsgAdapter(List<Bean_SystemList.SystemList> dataList, Activity activity, boolean isUnread) {
        this.dataList = dataList;
        this.activity = activity;
        this.isUnread = isUnread;
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
        Holder holder;
        if (convertView == null) {

            convertView = LayoutInflater.from(activity).inflate(R.layout.system_msg_list_item, null);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        final Bean_SystemList.SystemList systemList = dataList.get(position);
        holder.imgCircle.setEnabled(isUnread);
        holder.chbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                systemList.isSelect = isChecked;
            }
        });
        if (systemList.isVisiable) {
            holder.chbSelect.setVisibility(View.VISIBLE);
            holder.chbSelect.setChecked(systemList.isSelect);
            holder.imgCircle.setVisibility(View.GONE);
        } else {
            if (isUnread) {
                holder.imgCircle.setVisibility(View.VISIBLE);
            }
            holder.chbSelect.setVisibility(View.INVISIBLE);
        }

        holder.txtDate.setText(systemList.PublishTime);
        holder.txtTitle.setText(systemList.Title);
        return convertView;
    }

    class Holder {
        public TextView txtTitle, txtDate;
        public ImageView imgCircle;
        public CheckBox chbSelect;

        public Holder(View view) {
            super();
            txtDate = (TextView) view.findViewById(R.id.txt_date);
            txtTitle = (TextView) view.findViewById(R.id.txt_title);
            imgCircle = (ImageView) view.findViewById(R.id.img_circle);
            chbSelect = (CheckBox) view.findViewById(R.id.chb_select);
            chbSelect.setVisibility(View.GONE);
        }
    }
}
