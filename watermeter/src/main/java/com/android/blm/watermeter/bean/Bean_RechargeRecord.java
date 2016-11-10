package com.android.blm.watermeter.bean;/**
 * Created by Administrator on 2016/6/10.
 */

import java.util.List;

/**
 * author:${白曌勇} on 2016/6/10
 * TODO:
 */
public class Bean_RechargeRecord {
    public String Result;
    public String Message;
    public List<RechargeRecord> Data;
    public static class RechargeRecord{
        public String OperID;
        public String OperDate;
        public String LastBalance;
        public double PayMoney;
        public String ThisBalance;
    }
}
