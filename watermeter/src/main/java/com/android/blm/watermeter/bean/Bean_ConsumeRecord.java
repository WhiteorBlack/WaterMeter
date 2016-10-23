package com.android.blm.watermeter.bean;/**
 * Created by Administrator on 2016/6/10.
 */

import java.util.List;

/**
 * author:${白曌勇} on 2016/6/10
 * TODO:
 */
public class Bean_ConsumeRecord {
    public String Result;
    public String Message;
    public List<ConsumeRecord> Data;

    public static class ConsumeRecord {
        public String OperID;
        public String OperDate;
        public String MeterType;
        public String BeginNumber;
        public String EndNumber;
        public String UseNumber;
        public String Price;
        public float FareMoney;
    }
}
