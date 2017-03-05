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
import com.android.blm.ymxn.widget.silkcalendar.DatePickerController;
import com.android.blm.ymxn.widget.silkcalendar.DayPickerView;
import com.android.blm.ymxn.widget.silkcalendar.SimpleMonthAdapter;

import java.util.Date;

/**
 * author:${白曌勇} on 2016/8/4
 * TODO:
 */
public class CalendarPopNew extends PopupWindow implements DatePickerController {
    private View view;
    private Context context;
    private DayPickerView calendarViewNew;
    private OnDateSelect onDateSelect;

    public CalendarPopNew(Context context) {
        this.context = context;
        initView();
    }

    @Override
    public int getMaxYear() {
        return 0;
    }

    @Override
    public void onDayOfMonthSelected(int year, int month, int day) {
        month += 1;
        String monthS = month + "";
        String dayS = day + "";
        if (month < 10) {
            monthS = "0" + month;
        }
        if (day < 10)
            dayS = "0" + day;
        onDateSelect.onDateSelected(year + "-" + monthS + "-" + dayS, year, month, day);
    }

    @Override
    public void onDateRangeSelected(SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> selectedDays) {

    }

    public interface OnDateSelect {
        void onDateSelect(Date selectedStartDate, Date selectedEndDate, Date downDate);

        void onDateSelected(String date, int year, int monty, int day);
    }

    private void initView() {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.slik_calendar_pop_new, null);
            calendarViewNew = (DayPickerView) view.findViewById(R.id.calendar);
            calendarViewNew.setController(this);
        }

        this.setContentView(view);
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private SimpleMonthAdapter.CalendarDay calendarDay;

    public void shopPop(View parent) {
        if (calendarDay != null)
            calendarViewNew.setSelectDay(calendarDay);
        this.showAsDropDown(parent);
    }

    public void setOnDateSelect(OnDateSelect onDateSelect) {
        this.onDateSelect = onDateSelect;
    }

    public void setSelectDay(SimpleMonthAdapter.CalendarDay calendarDay) {
        this.calendarDay = calendarDay;
        if (calendarDay != null)
            calendarViewNew.setSelectDay(calendarDay);
    }
}
