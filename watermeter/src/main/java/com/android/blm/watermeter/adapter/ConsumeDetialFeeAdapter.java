package com.android.blm.watermeter.adapter;/**
 * Created by Administrator on 2016/6/1.
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.blm.watermeter.R;
import com.android.blm.watermeter.bean.Bean_ConsumeDetial;
import com.android.blm.watermeter.utils.Tools;

import java.util.List;

/**
 * author:${白曌勇} on 2016/6/1
 * TODO:
 */
public class ConsumeDetialFeeAdapter extends BaseAdapter {
    private List<Bean_ConsumeDetial.FareDetial> dataList;
    private Activity activity;

    public ConsumeDetialFeeAdapter(List<Bean_ConsumeDetial.FareDetial> dataList, Activity activity) {
        this.dataList = dataList;
        this.activity = activity;
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

            convertView = LayoutInflater.from(activity).inflate(R.layout.consume_detial_item, null);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        Bean_ConsumeDetial.FareDetial fareDetial = dataList.get(position);
        holder.txtFee.setText("¥" + Tools.formatDouble(fareDetial.FareMoney));
        holder.txtType.setText(fareDetial.FareName);
        return convertView;
    }

    class Holder {
        public TextView txtType, txtUsedCount, txtFee;

        public Holder(View view) {
            super();
            txtFee = (TextView) view.findViewById(R.id.txt_fee);
            txtType = (TextView) view.findViewById(R.id.txt_type);
            txtUsedCount = (TextView) view.findViewById(R.id.txt_used_count);
        }
    }
}
