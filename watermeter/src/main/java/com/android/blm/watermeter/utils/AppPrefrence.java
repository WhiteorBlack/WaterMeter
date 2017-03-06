package com.android.blm.watermeter.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPrefrence {

    private static SharedPreferences setting;

    private static SharedPreferences getSp(Context context) {
        SharedPreferences sp = context.getSharedPreferences("carSchool",
                context.MODE_PRIVATE);
        return sp;
    }

    private static final String ISLOGIN = "isLogin";
    private static final String USERNAME = "userName";
    private static final String USERPWD = "userPwd";
    private static final String TOKEN = "token";
    private static final String USERCODE = "userCode";
    private static final String REALName = "realName";
    private static final String DOORNO = "doorNo";
    private static final String ADDRESS = "address";
    private static final String RESERVE = "reserve";
    private static final String LASTREADNO = "lastReadNo";
    private static final String LASTREADDATE = "lastReadDate";
    private static final String ACCOUNTCOUNT = "accountCount";
    private static final String USERPHONE = "userPhone";
    private static final String ISUSER = "isUser";
    private static final String ISBYMSG = "isByMsg";
    private static final String PHONENUM="phoneNum";
    private static final String METERNO="meterNo";

    public static void setMeterNo(Context context,String meterNo){
        setting=getSp(context);
        setting.edit().putString(METERNO,meterNo).apply();
    }

    public static String getMeterNo(Context context){
        setting=getSp(context);
        return setting.getString(METERNO,"");
    }

    public static void setIsMsg(Context context, boolean isMsg) {
        setting = getSp(context);
        setting.edit().putBoolean(ISBYMSG, isMsg).apply();
    }

    public static boolean getIsMsg(Context context) {
        setting = getSp(context);
        return setting.getBoolean(ISBYMSG, false);
    }

    public static void setIsUser(Context context, boolean isUser) {
        setting = getSp(context);
        setting.edit().putBoolean(ISUSER, isUser).commit();
    }

    public static boolean getIsUser(Context context) {
        setting = getSp(context);
        return setting.getBoolean(ISUSER, true);
    }

    public static void setUserPhone(Context context, String phone) {
        setting = getSp(context);
        Tools.debug("setPhone" + phone);
        setting.edit().putString(USERPHONE, phone).commit();
    }

    public static String getUserPhone(Context context) {
        setting = getSp(context);
        return setting.getString(USERPHONE, "");
    }

    public static void setPhone(Context context, String phone) {
        setting = getSp(context);
        setting.edit().putString(PHONENUM, phone).commit();
    }

    public static String getPhone(Context context) {
        setting = getSp(context);
        return setting.getString(PHONENUM, "");
    }

    public static void setAccountCount(Context context, int count) {
        setting = getSp(context);
        setting.edit().putInt(ACCOUNTCOUNT, count).commit();
    }

    public static int getAccountCount(Context context) {
        setting = getSp(context);
        return setting.getInt(ACCOUNTCOUNT, 1);
    }

    /**
     * 用户业主最后读表数
     *
     * @param context
     * @param name
     */
    public static void setUserReadNo(Context context, String name) {
        setting = getSp(context);
        setting.edit().putString(LASTREADNO, name).commit();
    }

    public static String getUserReadNo(Context context) {
        setting = getSp(context);
        return setting.getString(LASTREADNO, "");
    }

    /**
     * 用户业主最后读表日期
     *
     * @param context
     * @param name
     */
    public static void setUserReadDate(Context context, String name) {
        setting = getSp(context);
        setting.edit().putString(LASTREADDATE, name).commit();
    }

    public static String getUserReadDate(Context context) {
        setting = getSp(context);
        return setting.getString(LASTREADDATE, "");
    }

    /**
     * 用户业主余额
     *
     * @param context
     * @param name
     */
    public static void setUserReserve(Context context, float name) {
        setting = getSp(context);
        setting.edit().putFloat(RESERVE, name).commit();
    }

    public static float getUserReserve(Context context) {
        setting = getSp(context);
        return setting.getFloat(RESERVE, 0.00f);
    }

    /**
     * 用户业主地址
     *
     * @param context
     * @param name
     */
    public static void setUserAdd(Context context, String name) {
        setting = getSp(context);
        setting.edit().putString(ADDRESS, name).commit();
    }

    public static String getUserAdd(Context context) {
        setting = getSp(context);
        return setting.getString(ADDRESS, "");
    }

    /**
     * 用户业主编号
     *
     * @param context
     * @param name
     */
    public static void setUsercode(Context context, String name) {
        setting = getSp(context);
        setting.edit().putString(USERCODE, name).commit();
    }

    public static String getUsercode(Context context) {
        setting = getSp(context);
        return setting.getString(USERCODE, "");
    }

    /**
     * 用户门牌号
     *
     * @param context
     * @param name
     */
    public static void setDoorno(Context context, String name) {
        setting = getSp(context);
        setting.edit().putString(DOORNO, name).commit();
    }

    public static String getDoorNo(Context context) {
        setting = getSp(context);
        return setting.getString(DOORNO, "");
    }

    /**
     * 用户真是姓名
     *
     * @param context
     * @param name
     */
    public static void setRealName(Context context, String name) {
        setting = getSp(context);
        setting.edit().putString(REALName, name).commit();
    }

    public static String getRealName(Context context) {
        setting = getSp(context);
        return setting.getString(REALName, "");
    }

    /**
     * 用户token
     *
     * @param context
     * @param name
     */
    public static void setToken(Context context, String name) {
        Tools.debug("tttt" + name);
        setting = getSp(context);
        setting.edit().putString(TOKEN, name).commit();
    }

    public static String getToken(Context context) {
        setting = getSp(context);
        return setting.getString(TOKEN, "");
    }

    /**
     * 用户密码
     *
     * @param context
     */
    public static void setUserPwd(Context context, String pwd) {
        setting = getSp(context);
        setting.edit().putString(USERPWD, pwd).commit();
    }

    public static String getUserPwd(Context context) {
        setting = getSp(context);
        return setting.getString(USERPWD, "");
    }

    /**
     * 用户登录名
     *
     * @param context
     * @param name
     */
    public static void setUserName(Context context, String name) {
        setting = getSp(context);
        setting.edit().putString(USERNAME, name).commit();
    }

    public static String getUserName(Context context) {
        setting = getSp(context);
        return setting.getString(USERNAME, "");
    }

    public static void setIsLogin(Context context, boolean isLogin) {
        setting = getSp(context);
        setting.edit().putBoolean(ISLOGIN, isLogin).commit();
    }

    public static boolean getIsLogin(Context context) {
        setting = getSp(context);
        return setting.getBoolean(ISLOGIN, false);
    }
}
