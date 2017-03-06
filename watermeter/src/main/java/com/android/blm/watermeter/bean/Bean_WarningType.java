package com.android.blm.watermeter.bean;/**
 * Created by Administrator on 2016/7/4.
 */

import java.util.List;

/**
 * author:${白曌勇} on 2016/7/4
 * TODO:
 */
public class Bean_WarningType {
    public String Result;
    public String Message;
    public List<WarningType> Data;
    public static class WarningType{
        public String TypeID;
        public String TypeName;
    }
}
