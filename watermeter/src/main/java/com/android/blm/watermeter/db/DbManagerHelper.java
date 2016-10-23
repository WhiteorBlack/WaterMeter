package com.android.blm.watermeter.db;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.blm.watermeter.WaterApplication;
import com.android.blm.watermeter.bean.Bean_SystemList;
import com.android.blm.watermeter.bean.Bean_SystemWarning;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/6.
 */
public class DbManagerHelper {
    private static DbManagerHelper dbManagerHelper;
    private DbOpenHelper dbOpenHelper;

    private DbManagerHelper() {
        dbOpenHelper = DbOpenHelper.getInstance(WaterApplication.getInstance().getApplicationContext());
    }

    public static synchronized DbManagerHelper getInstance() {
        if (dbManagerHelper == null) {
            dbManagerHelper = new DbManagerHelper();
        }
        return dbManagerHelper;
    }

    /**
     * 保存消息通知
     *
     * @param systemLists
     */
    synchronized public void saveSystemList(List<Bean_SystemList.SystemList> systemLists, String userCode) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        if (db.isOpen()) {
            for (Bean_SystemList.SystemList system : systemLists) {
                ContentValues value = new ContentValues();

                value.put(DbParams.ID, system.ID);

                if (system.Content != null) {
                    value.put(DbParams.CONTENT, system.Content);
                }
                if (system.PublishTime != null) {
                    value.put(DbParams.PUBLISHTIME, system.PublishTime);
                }
                if (system.Title != null) {
                    value.put(DbParams.TITLE, system.Title);
                }
                if (system.Type != null) {
                    value.put(DbParams.TYPE, system.Type);
                }
                if (userCode != null) {
                    value.put(DbParams.USERCODE, userCode);
                }
                db.replace(DbParams.SYSTEM_TABLE, null, value);
            }
        }
    }

    /**
     * 分页获取系统消息信息
     *
     * @param userCode
     * @param pageIndex
     * @param pageSize
     * @return
     */
    synchronized public List<Bean_SystemList.SystemList> getSystemList(String userCode, int pageIndex, int pageSize) {
        List<Bean_SystemList.SystemList> systemList = new ArrayList<>();
        String sql = "select * from " + DbParams.SYSTEM_TABLE + " where " + DbParams.USERCODE + "=" + "\""+userCode+"\"" + " limit " + String.valueOf(pageSize) + " offset " + String.valueOf(pageIndex * pageSize);
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                Bean_SystemList.SystemList system = new Bean_SystemList.SystemList();
                system.Title = cursor.getString(cursor.getColumnIndex(DbParams.TITLE));
                system.Type = cursor.getString(cursor.getColumnIndex(DbParams.TYPE));
                system.PublishTime = cursor.getString(cursor.getColumnIndex(DbParams.PUBLISHTIME));
                system.ID = cursor.getInt(cursor.getColumnIndex(DbParams.ID));
                system.Content = cursor.getString(cursor.getColumnIndex(DbParams.CONTENT));
                systemList.add(system);
            }
            cursor.close();
        }
        return systemList;
    }

    /**
     * 获取系统消息数量
     *
     * @param userCode
     * @return
     */
    synchronized public int getSystemCount(String userCode) {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        int count = 0;
        if (db.isOpen()) {
            Cursor curosr = db.rawQuery("select * from " + DbParams.SYSTEM_TABLE + " where " + DbParams.USERCODE + "=" + "\""+userCode+"\"", null);
            count = curosr.getCount();
            curosr.close();
        }
        return count;
    }

    /**
     * 删除系统消息
     *
     * @param idList
     */
    synchronized public void deleteSystem(List<String> idList) {
        String[] ids = (String[]) idList.toArray(new String[idList.size()]);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(DbParams.SYSTEM_TABLE, DbParams.ID + "=?", ids);
        }
    }

    /**
     * 保存系统警告信息
     *
     * @param warningList
     * @param userCode
     */
    synchronized public void saveWarningList(List<Bean_SystemWarning.SystemWarnings> warningList, String userCode) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        if (db.isOpen()) {
            for (Bean_SystemWarning.SystemWarnings warning : warningList) {
                ContentValues value = new ContentValues();
                if (warning.AlarmTime != null) {
                    value.put(DbParams.ALERTTIME, warning.AlarmTime);
                }
                if (warning.Content != null) {
                    value.put(DbParams.CONTENT, warning.Content);
                }
                if (warning.ID != null) {
                    value.put(DbParams.ID, warning.ID);
                }
                if (warning.Type != null) {
                    value.put(DbParams.TYPE, warning.Type);
                }
                if (userCode != null) {
                    value.put(DbParams.USERCODE, userCode);
                }
                db.replace(DbParams.WARNING_TABLE, null, value);
            }
        }
    }

    /**
     * 获取系统警告信息
     *
     * @param userCode
     * @param pageIndex
     * @param pageSize
     * @return
     */
    synchronized public List<Bean_SystemWarning.SystemWarnings> getWarningList(String userCode, int pageIndex, int pageSize) {
        List<Bean_SystemWarning.SystemWarnings> warningList = new ArrayList<>();
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        String sql = "select * from " + DbParams.WARNING_TABLE + " where " + DbParams.USERCODE + "=" + "\""+userCode+"\"" + " limit " + String.valueOf(pageSize) + " offset " + String.valueOf(pageIndex * pageSize);
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                Bean_SystemWarning.SystemWarnings warning = new Bean_SystemWarning.SystemWarnings();
                warning.Type = cursor.getString(cursor.getColumnIndex(DbParams.TYPE));
                warning.ID = cursor.getString(cursor.getColumnIndex(DbParams.ID));
                warning.Content = cursor.getString(cursor.getColumnIndex(DbParams.CONTENT));
                warning.AlarmTime = cursor.getString(cursor.getColumnIndex(DbParams.ALERTTIME));
                warningList.add(warning);
            }
            cursor.close();
        }
        return warningList;
    }

}
