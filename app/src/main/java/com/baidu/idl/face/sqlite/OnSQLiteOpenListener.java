package com.baidu.idl.face.sqlite;

import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库打开监听，支持数据库的第一次
 * 打开或者创建监听、数据的升级监听。<br/>
 */
public interface OnSQLiteOpenListener {

    /**
     * 数据库第一次创建的时候调用
     *
     * @param db 数据库操作对象
     */
    void onCreate(SQLiteDatabase db);

    /**
     * 数据库修改需要升级的时候调用
     *
     * @param db         数据库操作对象
     * @param oldVersion 数据库旧版本
     * @param newVersion 数据库升级版本
     */
    void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
}
