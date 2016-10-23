package com.android.blm.watermeter.adapter;/**
 * Created by Administrator on 2016/6/1.
 */

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.blm.watermeter.R;
import com.android.blm.watermeter.bean.Bean_SystemWarning;

import java.util.ArrayList;
import java.util.List;

/**
 * author:${白曌勇} on 2016/6/1
 * TODO:
 */
public class SystemWarningNewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Bean_SystemWarning.SystemWarnings> dataList;
    private Activity activity;
    private List<Integer> positionList;
    private boolean isUnread = false;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener l) {
        this.onItemClickListener = l;
    }

    public interface OnItemClickListener

    {
        void onItemClickListener(View view, int position);
    }

    public SystemWarningNewAdapter(List<Bean_SystemWarning.SystemWarnings> dataList, Activity activity, boolean isUnread) {
        this.dataList = dataList;
        this.activity = activity;
        this.isUnread = isUnread;
        positionList = new ArrayList<>();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.system_warning_list_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Holder mHolder = (Holder) holder;
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClickListener(v, holder.getLayoutPosition());
                }
            });
        }

        final Bean_SystemWarning.SystemWarnings systemWarnings = dataList.get(holder.getLayoutPosition());
        mHolder.txtType.setText(systemWarnings.Type);
        mHolder.txtDate.setText(systemWarnings.AlarmTime);
        mHolder.txtTitle.setText(systemWarnings.Content);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        public TextView txtTitle, txtDate, txtType;
        public ImageView imgCircle;
        public CheckBox chbSelect;

        public Holder(View view) {
            super(view);
            txtType = (TextView) view.findViewById(R.id.txt_type);
            txtDate = (TextView) view.findViewById(R.id.txt_date);
            txtTitle = (TextView) view.findViewById(R.id.txt_title);
            imgCircle = (ImageView) view.findViewById(R.id.img_circle);
            imgCircle.setVisibility(View.INVISIBLE);
            chbSelect = (CheckBox) view.findViewById(R.id.chb_select);
            chbSelect.setVisibility(View.GONE);
        }
    }
}
