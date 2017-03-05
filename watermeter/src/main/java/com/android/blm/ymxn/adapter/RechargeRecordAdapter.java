package com.android.blm.ymxn.adapter;/**
 * Created by Administrator on 2016/5/25.
 */

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.blm.ymxn.R;
import com.android.blm.ymxn.bean.Bean_RechargeRecord;
import com.android.blm.ymxn.utils.Tools;

import java.util.List;

/**
 * author:${白曌勇} on 2016/5/25
 * TODO:
 */
public class RechargeRecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity activity;
    private List<Bean_RechargeRecord.RechargeRecord> dataList;
    private OnItemClickListener onItemClickListener;

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);

        void onItemLongClick(View v, int position);
    }


    public RechargeRecordAdapter(Activity activity, List<Bean_RechargeRecord.RechargeRecord> dataList) {
        this.activity = activity;
        this.dataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recharge_record_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ViewHolder mHolder = (ViewHolder) holder;
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(v, holder.getLayoutPosition());

                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemClickListener.onItemLongClick(v, holder.getLayoutPosition());
                    return false;
                }
            });
        }
        Bean_RechargeRecord.RechargeRecord rechargeRecord = dataList.get(position);
        mHolder.txtRechargeMoney.setText("¥" + Tools.formatDouble(rechargeRecord.PayMoney));
        mHolder.txtRechargeDate.setText(rechargeRecord.OperDate);

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtRechargeDate, txtRechargeMoney;

        public ViewHolder(View view) {
            super(view);
            txtRechargeDate = (TextView) view.findViewById(R.id.txt_recharge_date);
            txtRechargeMoney = (TextView) view.findViewById(R.id.txt_recharge_money);
        }
    }
}
