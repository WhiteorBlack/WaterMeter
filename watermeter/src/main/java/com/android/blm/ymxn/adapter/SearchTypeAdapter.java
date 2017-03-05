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

/**
 * author:${白曌勇} on 2016/6/13
 * TODO:
 */
public class SearchTypeAdapter extends BaseAdapter {
    private String[] dataList;
    private Context context;

    public SearchTypeAdapter(String[] dataList) {
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.length;
    }

    @Override
    public Object getItem(int position) {
        return dataList[position];
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
        holder.txtName.setText(dataList[position]);

        return convertView;
    }

    class Holder {
        public TextView txtName;


        public Holder(View view) {
            txtName = (TextView) view.findViewById(R.id.txt_type);

        }
    }
}
