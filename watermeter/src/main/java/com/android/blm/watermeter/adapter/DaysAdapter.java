package com.android.blm.watermeter.adapter;/**
 * Created by Administrator on 2016/5/25.
 */

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.blm.watermeter.R;
import com.android.blm.watermeter.bean.Bean_Days;
import com.android.blm.watermeter.utils.Tools;

import java.util.List;

/**
 * author:${白曌勇} on 2016/5/25
 * TODO:
 */
public class DaysAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity activity;
    private List<Bean_Days> dataList;
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


    public DaysAdapter(Activity activity, List<Bean_Days> dataList) {
        this.activity = activity;
        this.dataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_item, parent, false);
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

        Bean_Days days = dataList.get(position);
        mHolder.txtWeek.setText(days.day + "");
        if (TextUtils.isEmpty(days.type)) {
            mHolder.txtType.setVisibility(View.GONE);
            mHolder.txtWeek.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
        } else {
            mHolder.txtType.setVisibility(View.VISIBLE);
            mHolder.txtType.setText(days.type);
        }

        if (!days.isSelect) {
            mHolder.llContent.setBackgroundColor(Color.TRANSPARENT);
            mHolder.txtWeek.setTextColor(Color.WHITE);
        } else {
            mHolder.llContent.setBackgroundResource(R.drawable.calendar_selected);
            mHolder.txtWeek.setTextColor(activity.getResources().getColor(R.color.blue));
        }
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(Tools.dip2px(activity, 40), Tools.dip2px(activity, 40));
        params.bottomMargin = Tools.dip2px(activity, 4);
        params.topMargin= Tools.dip2px(activity,4);
        mHolder.llContent.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtWeek;
        public TextView txtType;
        public LinearLayout llParent;
        public LinearLayout llContent;

        public ViewHolder(View view) {
            super(view);
            txtWeek = (TextView) view.findViewById(R.id.txt_day);
            llContent = (LinearLayout) view.findViewById(R.id.ll_day_content);
            txtType = (TextView) view.findViewById(R.id.txt_type);
            txtType.setVisibility(View.GONE);
            llParent = (LinearLayout) view.findViewById(R.id.ll_day_parent);
            llParent.setLayoutParams(new LinearLayout.LayoutParams((int) (Tools.getScreenWide(activity) / 7), LinearLayout.LayoutParams.MATCH_PARENT));
            llParent.setGravity(Gravity.CENTER);
        }
    }
}
