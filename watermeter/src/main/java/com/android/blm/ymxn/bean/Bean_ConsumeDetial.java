package com.android.blm.ymxn.bean;/**
 * Created by Administrator on 2016/6/10.
 */

import java.util.List;

/**
 * author:${白曌勇} on 2016/6/10
 * TODO:
 */
public class Bean_ConsumeDetial {
    public String Result;
    public String Message;
    public List<ConsumeDetial> Data;

    public static class ConsumeDetial {
        public String UseNumber1;
        public String UseNumber2;
        public String UseNumber3;
        public double ThisBalance;
        public double LastBalance;
        public List<FareDetial> FareDetail;
    }

    public static class FareDetial {
        public String FareName;
        public double FareMoney;
    }

    public static class UsedDetial {
        public String UseType;
        public String UseCount;
        public double price;
    }
}
