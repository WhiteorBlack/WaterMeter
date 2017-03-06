package com.android.blm.watermeter.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

public class LinearLayoutForListView extends LinearLayout {
	private BaseAdapter adapter;
	private OnItemClickListener onItemClickListener;
	private GestureDetector gestureDetector;
	private AdapterDataSetObserver mDataSetObserver;

	public LinearLayoutForListView(Context context) {
		super(context);
	}

	public LinearLayoutForListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setAdapter(BaseAdapter adapter) {
		this.adapter = adapter;
		// setAdapter 时添加 view
		bindView();
	}

	public void addViews(BaseAdapter adapter) {
		if (this.adapter != null && mDataSetObserver != null) {
			this.adapter.unregisterDataSetObserver(mDataSetObserver);
			mDataSetObserver = null;
		}

		this.adapter = adapter;

		if (this.adapter != null && mDataSetObserver == null) {
			mDataSetObserver = new AdapterDataSetObserver();
			this.adapter.registerDataSetObserver(mDataSetObserver);
		}
	}

	private class AdapterDataSetObserver extends DataSetObserver {
		@Override
		public void onChanged() {
			requestLayout();
		}

		@Override
		public void onInvalidated() {
			requestLayout();
		}

	}

	public void setOnGestureDetector(GestureDetector gestureDetector) {
		this.gestureDetector = gestureDetector;
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	/**
	 * 绑定 adapter 中所有的 view
	 */
	private void bindView() {
		if (adapter == null) {
			return;
		}
		removeAllViews();
		for (int i = 0; i < adapter.getCount(); i++) {

			final View v = adapter.getView(i, null, null);
			final int tmp = i;
			final Object obj = adapter.getItem(i);

			// view 点击事件触发时回调我们自己的接口
			v.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (onItemClickListener != null) {
						onItemClickListener.onItemClicked(v, obj, tmp);
					}
				}
			});

			addView(v, i);
		}
	}

	public void reBindView() {
		removeAllViews();
		for (int i = 0; i < adapter.getCount(); i++) {

			final View v = adapter.getView(i, null, null);
			final int tmp = i;
			final Object obj = adapter.getItem(i);

			// view 点击事件触发时回调我们自己的接口
			v.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (onItemClickListener != null) {
						onItemClickListener.onItemClicked(v, obj, tmp);
					}
				}
			});

			addView(v, i);
		}
	}

	/**
	 * 
	 * 回调接口
	 */
	public interface OnItemClickListener {
		/**
		 * 
		 * @param v
		 *            点击的 view
		 * @param obj
		 *            点击的 view 所绑定的对象
		 * @param position
		 *            点击位置的 index
		 */
		public void onItemClicked(View v, Object obj, int position);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		// gestureDetector.onTouchEvent(ev);
		super.dispatchTouchEvent(ev);
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		super.onTouchEvent(ev);
		return gestureDetector.onTouchEvent(ev);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// setMeasuredDimension(widthMeasureSpec,300);
		
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		super.onLayout(changed, l, t, r, b);
	}
}
