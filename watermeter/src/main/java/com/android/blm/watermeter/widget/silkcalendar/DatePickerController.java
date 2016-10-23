package com.android.blm.watermeter.widget.silkcalendar;

public interface DatePickerController {
	public abstract int getMaxYear();

	public abstract void onDayOfMonthSelected(int year, int month, int day);

    public abstract void onDateRangeSelected(final SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> selectedDays);

}