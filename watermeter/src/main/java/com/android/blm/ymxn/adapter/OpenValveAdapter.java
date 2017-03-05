package com.android.blm.ymxn.adapter;/**
 * Created by Administrator on 2016/5/25.
 */

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.blm.ymxn.R;
import com.android.blm.ymxn.bean.Bean_OwnMoneyUser;

import java.util.List;

/**
 * author:${白曌勇} on 2016/5/25
 * TODO:
 */
public class OpenValveAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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


    public OpenValveAdapter(Activity activity, List<Bean_OwnMoneyUser.OwnUsers> dataList) {
        this.activity = activity;
        this.dataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.open_valve_item, parent, false);
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


    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtUserName;
        private TextView txtDoorNo;
        private TextView txtUserSplot;
        private TextView txtMeterNo;
        private CheckBox checkBox;

        public ViewHolder(View view) {
            super(view);
            txtMeterNo = (TextView) view.findViewById(R.id.txt_meter_no);
            txtUserName = (TextView) view.findViewById(R.id.txt_name);
            txtDoorNo = (TextView) view.findViewById(R.id.txt_door_no);
            txtUserSplot = (TextView) view.findViewById(R.id.txt_user_splot);
            checkBox = (CheckBox) view.findViewById(R.id.check_valve);
            checkBox.setVisibility(View.GONE);
        }
    }
}
