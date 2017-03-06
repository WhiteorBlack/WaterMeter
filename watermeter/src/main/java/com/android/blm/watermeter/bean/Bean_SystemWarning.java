package com.android.blm.watermeter.bean;/**
 * Created by Administrator on 2016/6/19.
 */

import java.util.List;

/**
 * author:${白曌勇} on 2016/6/19
 * TODO:
 */
public class Bean_SystemWarning {
    public String Result;
    public String Message;
    public List<SystemWarnings> Data;

    public static class SystemWarnings {
        public boolean isSelect=false;
        public boolean isVisiable=false;
        public String ID;
        public String Content;
        public String AlarmTime;
        public String Type;
    }
}
