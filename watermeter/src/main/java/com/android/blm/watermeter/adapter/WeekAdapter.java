package com.android.blm.watermeter.adapter;/**
 * Created by Administrator on 2016/6/1.
 */

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.blm.watermeter.R;
import com.android.blm.watermeter.utils.Tools;

/**
 * author:${白曌勇} on 2016/6/1
 * TODO:
 */
public class WeekAdapter extends BaseAdapter {
    private String[] dataList;
    private Activity activity;
    private boolean isUnread = false;

    public WeekAdapter(String[] dataList, Activity activity) {
        this.dataList = dataList;
        this.activity = activity;
        this.isUnread = isUnread;
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
        Holder holder;
        if (convertView == null) {

            convertView = LayoutInflater.from(activity).inflate(R.layout.week_item, null);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.txtWeek.setText(dataList[position]);
        return convertView;
    }

    class Holder {
        public TextView txtWeek;


        public Holder(View view) {
            super();
            txtWeek = (TextView) view.findViewById(R.id.txt_week);
            txtWeek.setGravity(Gravity.CENTER);
            txtWeek.setLayoutParams(new LinearLayout.LayoutParams((int) (Tools.getScreenWide(activity) / 7), LinearLayout.LayoutParams.MATCH_PARENT));
        }
    }
}
