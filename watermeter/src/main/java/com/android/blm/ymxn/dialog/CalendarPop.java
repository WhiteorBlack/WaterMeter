package com.android.blm.ymxn.dialog;/**
 * Created by Administrator on 2016/8/4.
 */

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.android.blm.ymxn.R;
import com.android.blm.ymxn.widget.CalendarViewNew;

import java.util.Date;

/**
 * author:${白曌勇} on 2016/8/4
 * TODO:
 */
public class CalendarPop extends PopupWindow {
    private View view;
    private Context context;
    private CalendarViewNew calendarViewNew;
    private OnDateSelect onDateSelect;

    public CalendarPop(Context context) {
        this.context = context;
        initView();
    }

    public interface OnDateSelect {
        void onDateSelect(Date selectedStartDate, Date selectedEndDate, Date downDate);
    }

    private void initView() {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.calendar_pop_new, null);
            calendarViewNew = (CalendarViewNew) view.findViewById(R.id.calendar);
            calendarViewNew.setSelectMore(false);
            calendarViewNew.setOnItemClickListener(new CalendarViewNew.OnItemClickListener() {
                @Override
                public void OnItemClick(Date selectedStartDate, Date selectedEndDate, Date downDate) {
                    if (onDateSelect != null)
                        onDateSelect.onDateSelect(selectedStartDate, selectedEndDate, downDate);
//                    dismiss();
                }
            });
        }

        this.setContentView(view);
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void shopPop(View parent) {
        this.showAsDropDown(parent);
    }

    public void setOnDateSelect(OnDateSelect onDateSelect) {
        this.onDateSelect = onDateSelect;
    }

}
