package com.android.blm.watermeter.adapter;/**
 * Created by Administrator on 2016/6/1.
 */

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.blm.watermeter.R;
import com.android.blm.watermeter.bean.Bean_OwnMoneyUser;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * author:${白曌勇} on 2016/6/1
 * TODO:
 */
public class CopyUserNewAdapterTest extends BaseSwipeAdapter {
    private List<Bean_OwnMoneyUser.OwnUsers> dataList;
    private Activity activity;
    private List<Integer> positionList;

    public interface OnClickListener {
        void onClick(View v, int position);
    }

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener l) {
        this.onClickListener = l;
    }

    public CopyUserNewAdapterTest(List<Bean_OwnMoneyUser.OwnUsers> dataList, Activity activity) {
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
    public int getSwipeLayoutResourceId(int i) {
        return R.id.swipe;
    }

    @Override
    public View generateView(final int i, ViewGroup viewGroup) {
        View convertView = LayoutInflater.from(activity).inflate(R.layout.copy_user_item_new, null);
        SwipeLayout swipeLayout = (SwipeLayout) convertView.findViewById(getSwipeLayoutResourceId(i));
        swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
            }
        });
        swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
            }
        });
        convertView.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null)
                    onClickListener.onClick(view, i);
                closeItem(i);
            }
        });
        return convertView;
    }

    @Override
    public void fillValues(int i, View view) {
        Holder holder = new Holder(view);
        final Bean_OwnMoneyUser.OwnUsers systemList = dataList.get(i);
        holder.chbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                systemList.isSelect = isChecked;
            }
        });

        holder.chbTitle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                for (int i = 0; i < dataList.size(); i++) {
                    if (TextUtils.equals(systemList.DeptName, dataList.get(i).DeptName)) {
                        dataList.get(i).isSelect = isChecked;
                    }

                }
                notifyDataSetChanged();
            }
        });

        if (systemList.isVisiable) {
            holder.chbSelect.setVisibility(View.VISIBLE);
            holder.chbSelect.setChecked(systemList.isSelect);
            holder.llCheck.setVisibility(View.VISIBLE);
        } else {
            holder.llCheck.setVisibility(View.GONE);
            holder.chbSelect.setVisibility(View.GONE);
        }

        if (i == 0) {
            holder.llParent.setVisibility(View.VISIBLE);
            holder.txtSplot.setText(systemList.DeptName);
        } else {
            if (TextUtils.equals(systemList.DeptName, dataList.get(i - 1).DeptName)) {
                holder.llParent.setVisibility(View.GONE);
            } else {
                holder.llParent.setVisibility(View.VISIBLE);
                holder.txtSplot.setText(systemList.DeptName);
            }
        }

        holder.txtMeterType.setText(systemList.MeterType);
        holder.txtMeterNo.setText(systemList.MeterAddr);
        holder.txtDoorNo.setText(systemList.Doorplate);
        holder.txtDate.setText(systemList.LastReadDate);
    }


    class Holder {
        public TextView txtDoorNo, txtMeterNo, txtMeterType, txtDate;
        public CheckBox chbSelect, chbTitle;
        public LinearLayout llParent, llCheck;
        public TextView txtSplot;

        public Holder(View view) {
            super();
            txtDate = (TextView) view.findViewById(R.id.txt_copy_date);
            txtDoorNo = (TextView) view.findViewById(R.id.txt_door_no);
            txtMeterNo = (TextView) view.findViewById(R.id.txt_meter_no);
            txtMeterType = (TextView) view.findViewById(R.id.txt_meter_type);
            chbSelect = (CheckBox) view.findViewById(R.id.checkbox);
            chbSelect.setVisibility(View.GONE);
            llParent = (LinearLayout) view.findViewById(R.id.ll_title_parent);
            txtSplot = (TextView) view.findViewById(R.id.txt_plot);
            llCheck = (LinearLayout) view.findViewById(R.id.ll_check);
            llCheck.setVisibility(View.GONE);
            chbTitle = (CheckBox) view.findViewById(R.id.checkbox_title);
        }
    }
}
