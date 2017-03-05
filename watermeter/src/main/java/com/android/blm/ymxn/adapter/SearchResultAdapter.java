package com.android.blm.ymxn.adapter;/**
 * Created by Administrator on 2016/5/25.
 */

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.blm.ymxn.R;
import com.android.blm.ymxn.bean.Bean_OwnMoneyUser;

import java.util.List;

/**
 * author:${白曌勇} on 2016/5/25
 * TODO:
 */
public class SearchResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity activity;
    private List<Bean_OwnMoneyUser.OwnUsers> dataList;
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


    public SearchResultAdapter(Activity activity, List<Bean_OwnMoneyUser.OwnUsers> dataList) {
        this.activity = activity;
        this.dataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_item, parent, false);
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
        Bean_OwnMoneyUser.OwnUsers ownUsers = dataList.get(position);
        mHolder.txtUserPhone.setText(ownUsers.Phone);
        mHolder.txtUserNo.setText(ownUsers.UserCode);
        mHolder.txtUserName.setText(ownUsers.UserName);
        mHolder.txtMeterType.setText(ownUsers.MeterType);
        mHolder.txtMeterState.setText(ownUsers.ValveStatus);
        mHolder.txtMeterNo.setText(ownUsers.MeterAddr);
        mHolder.txtMoney.setText(ownUsers.Reserve);
        if (TextUtils.isEmpty(ownUsers.Address))
            mHolder.txtAddress.setText("暂无地址信息");
        else
            mHolder.txtAddress.setText(ownUsers.Address);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtUserName, txtUserNo, txtUserPhone, txtMeterNo, txtMeterType, txtMeterState, txtMoney, txtAddress;

        public ViewHolder(View view) {
            super(view);

            txtAddress = (TextView) view.findViewById(R.id.txt_user_address);
            txtMeterNo = (TextView) view.findViewById(R.id.txt_meter_no);
            txtMeterState = (TextView) view.findViewById(R.id.txt_meter_state);
            txtMeterType = (TextView) view.findViewById(R.id.txt_meter_type);
            txtMoney = (TextView) view.findViewById(R.id.txt_money);
            txtUserName = (TextView) view.findViewById(R.id.txt_user_name);
            txtUserNo = (TextView) view.findViewById(R.id.txt_user_no);
            txtUserPhone = (TextView) view.findViewById(R.id.txt_user_phone);
        }
    }
}
