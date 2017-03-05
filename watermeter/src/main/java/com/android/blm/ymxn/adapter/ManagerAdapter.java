package com.android.blm.ymxn.adapter;/**
 * Created by Administrator on 2016/6/13.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.blm.ymxn.R;
import com.android.blm.ymxn.bean.Bean_Manager;
import com.android.blm.ymxn.utils.Tools;

import java.util.List;

/**
 * author:${白曌勇} on 2016/6/13
 * TODO:
 */
public class ManagerAdapter extends BaseAdapter {
    private List<Bean_Manager> dataList;
    private Context context;
    public ManagerAdapter(List<Bean_Manager> dataList,Context context) {
        this.dataList = dataList;
        this.context=context;
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.manager_item, null);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.txtName.setText(dataList.get(position).name);
        holder.imgIcon.setBackgroundResource(dataList.get(position).resId);
        return convertView;
    }

    class Holder {
        public TextView txtName;
        public ImageView imgIcon;
        public LinearLayout llParent;

        public Holder(View view) {
            txtName = (TextView) view.findViewById(R.id.txt_name);
            imgIcon = (ImageView) view.findViewById(R.id.img_icon);
            llParent=(LinearLayout)view.findViewById(R.id.ll_parent);
            LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) llParent.getLayoutParams();
            params.height= (int) (Tools.getScreenWide(context)/4*0.7);
            llParent.setLayoutParams(params);
        }
    }
}
