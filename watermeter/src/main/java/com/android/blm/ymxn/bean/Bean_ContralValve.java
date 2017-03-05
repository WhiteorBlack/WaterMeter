package com.android.blm.ymxn.bean;/**
 * Created by Administrator on 2016/6/18.
 */

import java.util.List;

/**
 * author:${白曌勇} on 2016/6/18
 * TODO:
 */
public class Bean_ContralValve {
    public String Result;
    public String Message;

    public List<ContralValve> Data;

    public static class ContralValve {

        public boolean isSelect=false;
        public boolean isVisiable=false;
    }
}
