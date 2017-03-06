package com.android.blm.watermeter.activity;
/**
 * Created by Administrator on 2016/6/2.
 */

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.blm.watermeter.R;
import com.android.blm.watermeter.fragment.SystemHasReadFragment;
import com.android.blm.watermeter.fragment.SystemUnreadFragment;

/**
 * author:${白曌勇} on 2016/6/2
 * TODO:
 */
public class SystemMessage extends FragmentActivity {
    private FragmentManager fm;
    private TextView txtUnread, txtHasRead;
    private SystemUnreadFragment unreadFragment;
    private SystemHasReadFragment hasReadFragment;
    FragmentTransaction transaction;
    private LinearLayout llBottom;
    private boolean isVisiableUnread = false;
    private boolean isVisiableHasRead = false;
    private ImageView imgLine;
    private FrameLayout flRead;
    private String type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system_message);
        type = getIntent().getStringExtra("type");
        initView();
        setChoice(0);
    }

    private void initView() {
        fm = getSupportFragmentManager();
        txtHasRead = (TextView) findViewById(R.id.txt_has_read);
        txtUnread = (TextView) findViewById(R.id.txt_unread);
        imgLine = (ImageView) findViewById(R.id.img_line);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom_bar);
        llBottom.setVisibility(View.GONE);
        flRead = (FrameLayout) findViewById(R.id.fl_set_read);
    }

    private void setChoice(int pos) {
        transaction = fm.beginTransaction();
        clearChoice(transaction);
        txtHasRead.setTextColor(Color.BLACK);
        txtUnread.setTextColor(Color.BLACK);
        switch (pos) {
            case 0:
                if (unreadFragment == null) {
                    unreadFragment = new SystemUnreadFragment();
                    unreadFragment.setType(type);
                    transaction.add(R.id.fl_content, unreadFragment);
                } else {
                    transaction.show(unreadFragment);
                }
                txtUnread.setTextColor(getResources().getColor(R.color.orange));
                flRead.setVisibility(View.VISIBLE);
                imgLine.setVisibility(View.VISIBLE);
                break;
            case 1:
                if (hasReadFragment == null) {
                    hasReadFragment = new SystemHasReadFragment();
                    hasReadFragment.setType(type);
                    transaction.add(R.id.fl_content, hasReadFragment);
                } else {
                    transaction.show(hasReadFragment);
                }
                txtHasRead.setTextColor(getResources().getColor(R.color.orange));
                flRead.setVisibility(View.GONE);
                imgLine.setVisibility(View.GONE);
                break;
        }
        transaction.commit();
    }

    private void clearChoice(FragmentTransaction transaction) {
        if (unreadFragment != null) {
            transaction.hide(unreadFragment);
        }
        if (hasReadFragment != null) {
            transaction.hide(hasReadFragment);
        }
    }

    public void waterClick(View v) {
        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;
            case R.id.fl_edit:
                if (unreadFragment != null && unreadFragment.isVisible()) {
                    unreadFragment.setSelectState();
                    isVisiableUnread = !isVisiableUnread;

                }
                if (hasReadFragment != null && hasReadFragment.isVisible()) {
                    hasReadFragment.setSelectState();
                    isVisiableHasRead = !isVisiableHasRead;
                }
                if (!isVisiableHasRead && !isVisiableUnread) {
                    llBottom.setVisibility(View.GONE);
                } else {
                    llBottom.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.txt_unread:
                setChoice(0);
                if (!isVisiableUnread && llBottom.getVisibility() == View.VISIBLE) {
                    llBottom.setVisibility(View.GONE);
                    return;
                }
                if (isVisiableUnread && llBottom.getVisibility() == View.GONE)
                    llBottom.setVisibility(View.VISIBLE);
                break;
            case R.id.txt_has_read:
                setChoice(1);
                if (!isVisiableHasRead && llBottom.getVisibility() == View.VISIBLE) {
                    llBottom.setVisibility(View.GONE);
                    return;
                }
                if (isVisiableHasRead && llBottom.getVisibility() == View.GONE) {
                    llBottom.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.fl_set_read:
                if (unreadFragment != null && unreadFragment.isVisible()) {
                    unreadFragment.readItem();
                }

                break;
            case R.id.fl_delete:
                if (hasReadFragment != null && hasReadFragment.isVisible()) {
                    hasReadFragment.deleteItem();
                    return;
                }
                if (unreadFragment != null && unreadFragment.isVisible()) {
                    unreadFragment.deleteItem();
                }
                break;
        }
    }

    public void setTxtUnread(int count) {
        txtUnread.setText("未读(" + count + ")");
    }

    public void setTxtHasRead(int count) {
        txtHasRead.setText("全部(" + count + ")");
    }
}
