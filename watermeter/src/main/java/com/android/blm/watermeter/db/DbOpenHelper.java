package com.android.blm.watermeter.db;
/**
 * Created by Administrator on 2016/7/5.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * author:${白曌勇} on 2016/7/5
 * TODO:
 */
public class DbOpenHelper extends SQLiteOpenHelper {
    private static DbOpenHelper instance;
    private static int DBVERSION = 1;
    private String CREATE_SYSTEM_TABLE = "CREATE TABLE "
            + DbParams.SYSTEM_TABLE + " ("
            + DbParams.ID + " INTEGER PRIMARY KEY, "
            + DbParams.CONTENT + " TEXT, "
            + DbParams.PUBLISHTIME + " TEXT, "
            + DbParams.TITLE + " TEXT, "
            + DbParams.USERCODE + " TEXT, "
            + DbParams.TYPE + " TEXT);";

    private String CREATE_WARNING_TABLE = "CREATE TABLE "
            + DbParams.WARNING_TABLE + " ("
            + DbParams.ID + " TEXT PRIMARY KEY,"
            + DbParams.CONTENT + " TEXT,"
            + DbParams.ALERTTIME + " TEXT,"
            + DbParams.USERCODE + " TEXT, "
            + DbParams.TYPE + " TEXT);";

    public static DbOpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DbOpenHelper(context, "waterMeter.db", null, DBVERSION);
        }
        return instance;
    }

    public DbOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SYSTEM_TABLE);
        db.execSQL(CREATE_WARNING_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
