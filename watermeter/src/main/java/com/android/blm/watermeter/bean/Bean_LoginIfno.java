package com.android.blm.watermeter.bean;/**
 * Created by Administrator on 2016/6/7.
 */

import java.util.List;

/**
 * author:${白曌勇} on 2016/6/7
 * TODO:
 */
public class Bean_LoginIfno {
    public String Result;
    public String Message;
    public List<LoginData> Data;

    public static class LoginData {

        public String LoginType;
        public String OperatorCode;
        public String OperatorName;
        public String Token;
        public String UserCode;
        public String UserName;
        public String Doorplate;
        public String Address;
        public float Reserve;
        public String MeterAddr;
        public String LastReadNumber;
        public String LastReadDate;
        public String Phone;

    }
}
