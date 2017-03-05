package com.android.blm.ymxn.bean;/**
 * Created by Administrator on 2016/6/4.
 */

import java.util.List;

/**
 * author:${白曌勇} on 2016/6/4
 * TODO:
 */
public class Bean_SystemList {
    public int code;
    public String Result;
    public String Message;
    public List<SystemList> Data;

    public static class SystemList {
        public int ID;
        public String Title;
        public String Content;
        public String PublishTime;
        public String Type;
        public boolean isSelect = false;  //标示是否选择该item
        public boolean isVisiable = false;  //标示是否显示选择框
    }
}
