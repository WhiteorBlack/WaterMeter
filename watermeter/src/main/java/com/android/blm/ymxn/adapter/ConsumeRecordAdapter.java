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
import com.android.blm.ymxn.bean.Bean_ConsumeRecord;
import com.android.blm.ymxn.utils.Tools;

import java.util.List;

/**
 * author:${白曌勇} on 2016/5/25
 * TODO:
 */
public class ConsumeRecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity activity;
    private List<Bean_ConsumeRecord.ConsumeRecord> dataList;
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


    public ConsumeRecordAdapter(Activity activity, List<Bean_ConsumeRecord.ConsumeRecord> dataList) {
        this.activity = activity;
        this.dataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.consume_record_item, parent, false);
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

        Bean_ConsumeRecord.ConsumeRecord consumeRecord = dataList.get(position);
        mHolder.txtUsedCount.setText("用量: " + consumeRecord.UseNumber);
        String date = consumeRecord.OperDate;
        String dates[] = {};
        try {
            date = date.substring(0, date.indexOf(" "));
            Tools.debug("date" + date);
            dates = date.split("-");
        } catch (Exception e) {
        }

        mHolder.txtYearMonth.setText(dates[0] + "年" + dates[1] + "月");
        mHolder.txtDay.setText(dates[2]);
        mHolder.txtType.setText(consumeRecord.MeterType);
        mHolder.txtTotalFee.setText("¥" + consumeRecord.FareMoney);
        mHolder.txtStarNo.setText("起始数: " + consumeRecord.BeginNumber);
        mHolder.txtEndNo.setText("终止数: " + consumeRecord.EndNumber);
        mHolder.txtPrice.setText("单价: " + consumeRecord.Price);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtYearMonth, txtDay, txtType, txtStarNo, txtEndNo, txtUsedCount, txtPrice, txtTotalFee;

        public ViewHolder(View view) {
            super(view);
            txtDay = (TextView) view.findViewById(R.id.txt_day);
            txtEndNo = (TextView) view.findViewById(R.id.txt_end_no);
            txtPrice = (TextView) view.findViewById(R.id.txt_price);
            txtStarNo = (TextView) view.findViewById(R.id.txt_start_no);
            txtTotalFee = (TextView) view.findViewById(R.id.txt_total_money);
            txtType = (TextView) view.findViewById(R.id.txt_record_type);
            txtUsedCount = (TextView) view.findViewById(R.id.txt_used_count);
            txtYearMonth = (TextView) view.findViewById(R.id.txt_year_month);
        }
    }
}
