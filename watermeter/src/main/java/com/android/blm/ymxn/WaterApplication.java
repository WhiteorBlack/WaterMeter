package com.android.blm.ymxn;

import android.app.Application;

/**
 * Created by Administrator on 2016/7/6.
 */
public class WaterApplication extends Application {
    private static WaterApplication instance;

    public static WaterApplication getInstance() {

        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
    }
}
