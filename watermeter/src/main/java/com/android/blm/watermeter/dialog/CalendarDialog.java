package com.android.blm.watermeter.dialog;/**
 * Created by Administrator on 2016/6/21.
 */

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.android.blm.watermeter.R;
import com.android.blm.watermeter.widget.calendar.CalendarPickerView;

import java.util.Date;

/**
 * author:${白曌勇} on 2016/6/21
 * TODO:
 */
public class CalendarDialog extends PopupWindow {

    private View view;
    private Context context;
    private CalendarPickerView calendarView;
    private Date minDate, maxDate;
    private OnDateListener onDateListener;

    public CalendarDialog(Context context, Date minDate, Date maxDate) {
        this.context = context;
        this.maxDate = maxDate;
        this.minDate = minDate;
        initView();
    }

    private void initView() {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.calendar_pop, null);
            calendarView = (CalendarPickerView) view.findViewById(R.id.calendar);
        }
        calendarView.init(minDate, maxDate).inMode(CalendarPickerView.SelectionMode.SINGLE).withSelectedDate(new Date());
        calendarView.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                if (onDateListener != null) {
                    onDateListener.onDateListener(date);
                }
                dismiss();
            }

            @Override
            public void onDateUnselected(Date date) {

            }
        });
        this.setContentView(view);
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.setFocusable(true);
        this.setOutsideTouchable(true);

    }

    public void setOnDateListener(OnDateListener l) {
        this.onDateListener = l;
    }

    public void showPop(View parent) {
        this.showAsDropDown(parent, 0, 0);
    }

    public interface OnDateListener {
        void onDateListener(Date date);
    }

}
