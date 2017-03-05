package com.android.blm.ymxn.adapter;/**
 * Created by Administrator on 2016/5/25.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.blm.ymxn.R;
import com.android.blm.ymxn.bean.Bean_CopyMeter;
import com.android.blm.ymxn.manager.SearchResult;
import com.android.blm.ymxn.utils.Tools;
import com.android.blm.ymxn.widget.swipelistview.DensityUtil;
import com.android.blm.ymxn.widget.swipelistview.LJListView;
import com.android.blm.ymxn.widget.swipelistview.SwipeMenu;
import com.android.blm.ymxn.widget.swipelistview.SwipeMenuCreator;
import com.android.blm.ymxn.widget.swipelistview.SwipeMenuItem;
import com.android.blm.ymxn.widget.swipelistview.SwipeMenuListView;

import java.util.List;

/**
 * author:${白曌勇} on 2016/5/25
 * TODO:
 */
public class CopyMeterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity activity;
    private List<Bean_CopyMeter.CopyMeters> dataList;
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

        void onCopyMeterClick(SwipeMenu menu, int parentPos, int childPos);
    }


    public CopyMeterAdapter(Activity activity, List<Bean_CopyMeter.CopyMeters> dataList) {
        this.activity = activity;
        this.dataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.copy_meter_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
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

        Bean_CopyMeter.CopyMeters copyMeters = dataList.get(position);
        final CopyUserAdapter userAdapter = new CopyUserAdapter(copyMeters.User, activity);
        mHolder.listView.setAdapter(userAdapter);
        if (copyMeters.isVisiable) {
            mHolder.checkBox.setVisibility(View.VISIBLE);
            userAdapter.setVisiable();
        } else {
            userAdapter.setInvisiable();
            mHolder.checkBox.setVisibility(View.GONE);
        }

        mHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                userAdapter.updateCheck(isChecked);
            }
        });

        mHolder.listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int childposition, SwipeMenu menu, int index) {
                onItemClickListener.onCopyMeterClick(menu, position, childposition);
                return false;
            }
        });

        mHolder.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                activity.startActivity(new Intent(activity, SearchResult.class).putExtra("type", 1));
            }
        });
        LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) mHolder.listView.getLayoutParams();
        params.height=Tools.dip2px(activity,45)*copyMeters.User.size();
        mHolder.listView.setLayoutParams(params);

        userAdapter.notifyDataSetChanged();
        mHolder.txtSplot.setText("所属小区:  "+copyMeters.DeptName);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtSplot;
        LJListView listView;
        CheckBox checkBox;

        public ViewHolder(View view) {
            super(view);
            txtSplot = (TextView) view.findViewById(R.id.txt_plot);
            SwipeMenuCreator creator = new SwipeMenuCreator() {
                @Override
                public void create(SwipeMenu menu) {
                    SwipeMenuItem openItem = new SwipeMenuItem(activity);
                    openItem.setBackground(new ColorDrawable(activity.getResources().getColor(R.color.orange)));
                    openItem.setWidth(DensityUtil.dip2px(activity, 85));
                    openItem.setTitle("开阀");
                    openItem.setTitleSize(16);
                    openItem.setTitleColor(Color.WHITE);
                    openItem.setIcon(R.mipmap.copy_meter);
                    menu.addMenuItem(openItem);
                }
            };
            listView = (LJListView) view.findViewById(R.id.list_meter);
            checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            checkBox.setVisibility(View.GONE);
        }
    }
}
