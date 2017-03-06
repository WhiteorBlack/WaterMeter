package com.android.blm.watermeter.bean;/**
 * Created by Administrator on 2016/6/19.
 */

import java.util.List;

/**
 * author:${白曌勇} on 2016/6/19
 * TODO:
 */
public class Bean_OwnMoneyUser {
    public String Result;
    public String Message;
    public List<OwnUsers> Data;

    public static class OwnUsers {
        public int position;
        public int parentPos;
        public boolean isCopy=false;
        public boolean isSelect = false;
        public boolean isVisiable = false;
        public String MeterAddr;
        public String MeterType;
        public String ValveName;
        public String UserCode;
        public String UserName;
        public String Phone;
        public String Doorplate;
        public String Address;
        public String Reserve;
        public String DeptName;
        public String ValveStatus;
        public String LastReadDate;
        public String LastReadNumber;
    }
}
