package com.android.blm.watermeter.bean;/**
 * Created by Administrator on 2016/9/25.
 */

import java.util.List;

/**
 * author:${白曌勇} on 2016/9/25
 * TODO:
 */
public class Bean_ChatOrder {
    public int Result;
    public List<ChatData> Data;
    public static class ChatData{
        public double LastBalance;
        public double ThisBalance;
        public String OrderInfo;
    }
}
