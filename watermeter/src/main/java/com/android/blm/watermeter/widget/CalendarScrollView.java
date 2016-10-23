package com.android.blm.watermeter.widget;/**
 * Created by Administrator on 2016/6/10.
 */

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.android.blm.watermeter.utils.Tools;

/**
 * author:${白曌勇} on 2016/6/10
 * TODO:
 */
public class CalendarScrollView extends ScrollView {
    float mLastY = -1;
    private final static float OFFSET_RADIO = 1.8f;
    private OnScrollListener onScrollListener;

    public CalendarScrollView(Context context) {
        super(context);
    }

    public CalendarScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CalendarScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CalendarScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setOnScrollListener(OnScrollListener l) {
        this.onScrollListener = l;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                // System.out.println("数据监测：" + getFirstVisiblePosition() + "---->"
                // + getLastVisiblePosition());
                Tools.debug("mlastY" + mLastY + "--deltay" + deltaY + "---event" + ev.getRawX());
                updateHeaderHeight(deltaY);
//                            invokeOnScrolling();


                break;
            case MotionEvent.ACTION_SCROLL:

                break;
            case MotionEvent.ACTION_UP:
                resetHeight();
                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            updateHeaderHeight(deltaY);
        }
    };

    private int countTime = 400;

    private void resetHeight() {
        deltaY = (getChildAt(0).getHeight() - Math.abs(test)) / 20 * deltaY / Math.abs(deltaY);
        new Thread(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                while (count < countTime) {
                    handler.sendEmptyMessage(0);
                    count += 20;
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    public void collospView() {
        deltaY = -1;
        test = 0;
        resetHeight();
    }

    public void initView() {
        FrameLayout.LayoutParams param = (FrameLayout.LayoutParams) getChildAt(0).getLayoutParams();
        test = -getChildAt(0).getHeight() + Tools.dip2px(getContext(), 15);
        param.topMargin = (int) test;

        getChildAt(0).setLayoutParams(param);
    }

    float test = 0;
    float deltaY = 0;
    boolean isTop = false;

    private void updateHeaderHeight(float deltaY) {
        this.deltaY = deltaY;
        test += deltaY;
        if (test > 0) {
            test = 0;
        }
        if (test < -getChildAt(0).getHeight() + Tools.dip2px(getContext(), 15)) {
            test = -getChildAt(0).getHeight() + Tools.dip2px(getContext(), 15);
        }
        FrameLayout.LayoutParams param = (FrameLayout.LayoutParams) getChildAt(0).getLayoutParams();
        param.topMargin = (int) test;
        Tools.debug("scroll" + deltaY + "---test" + test);
        getChildAt(0).setLayoutParams(param);

        if (deltaY > 0) {
            isTop = false;
        } else {
            isTop = true;
        }
        if (onScrollListener != null)
            onScrollListener.onScroll(test, isTop);
    }


    public interface OnScrollListener {
        void onScroll(float scrollY, boolean bottomToTop);
    }
}
