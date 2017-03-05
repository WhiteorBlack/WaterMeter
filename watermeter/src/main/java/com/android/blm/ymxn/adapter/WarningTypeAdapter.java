package com.android.blm.ymxn.adapter;/**
 * Created by Administrator on 2016/6/13.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.blm.ymxn.R;
import com.android.blm.ymxn.bean.Bean_WarningType;

import java.util.List;

/**
 * author:${白曌勇} on 2016/6/13
 * TODO:
 */
public class WarningTypeAdapter extends BaseAdapter {
    private List<Bean_WarningType.WarningType> dataList;
    private Context context;

    public WarningTypeAdapter(List<Bean_WarningType.WarningType> dataList) {
        this.dataList = dataList;
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
        Holder holder = null;
        if (holder == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_type_item, null);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.txtName.setText(dataList.get(position).TypeName);

        return convertView;
    }

    class Holder {
        public TextView txtName;


        public Holder(View view) {
            txtName = (TextView) view.findViewById(R.id.txt_type);

        }
    }
}
