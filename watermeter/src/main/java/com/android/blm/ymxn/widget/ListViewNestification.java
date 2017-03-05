package com.android.blm.ymxn.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

/**
 * 重写listview 解决listview嵌套只显示一行的问题 com.xiaodao.view.ListViewNestification
 * 
 * @author 白曌勇 Create at 2015-4-14 上午10:33:40
 */
public class ListViewNestification extends ListView {

	boolean isVisiable = true;

	public ListViewNestification(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ListViewNestification(Context context, AttributeSet attrs,
								 int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub

	}

	public ListViewNestification(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public boolean isVisiable() {
		return isVisiable;
	}

	public void setVisiable(boolean isVisiable) {
		this.isVisiable = isVisiable;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
//		int expandSpec = 0;
//		for (int i = 0; i < this.getAdapter().getCount(); i++) {
//			View item = this.getAdapter().getView(i, null, this);
//			item.measure(0, 0);
//
//			expandSpec += item.getMeasuredHeight();
//
//			Tools.debug("height" + expandSpec);
//		}
		super.onMeasure(widthMeasureSpec, expandSpec);

	}

	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		// TODO Auto-generated method stub
		if (isVisiable) {
			visibility = View.GONE;
		} else {
			visibility = View.VISIBLE;
		}
		super.onVisibilityChanged(changedView, visibility);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		super.onLayout(changed, l, t, r, b);
	}
}
