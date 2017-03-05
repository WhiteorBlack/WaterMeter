package com.android.blm.ymxn.bean;/**
 * Created by Administrator on 2016/7/10.
 */

import java.util.List;

/**
 * author:${白曌勇} on 2016/7/10
 * TODO:
 */
public class Bean_CopyResult {
    public String Result;
    public String Message;
    public List<CopyResult> Data;
    public static class  CopyResult{
        public String MeterAddr;
        public String MeterNumber;
        public String ValveName;
        public String ReadDate;
        public String Status;
    }
}
