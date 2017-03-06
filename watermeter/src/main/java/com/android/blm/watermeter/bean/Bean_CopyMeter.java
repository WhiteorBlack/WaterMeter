package com.android.blm.watermeter.bean;/**
 * Created by Administrator on 2016/6/17.
 */

import java.util.List;

/**
 * author:${白曌勇} on 2016/6/17
 * TODO:
 */
public class Bean_CopyMeter {
    public String Result;
    public String Message;
    public List<CopyMeters> Data;

    public static class CopyMeters {
        public boolean isVisiable = false;
        public boolean isSelect = false;
        public String DeptName;
        public List<Bean_OwnMoneyUser.OwnUsers> User;
    }

    public static class CopyUsers {
        public boolean isVisiable = false;
        public boolean isSelect = false;
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
    }
}
