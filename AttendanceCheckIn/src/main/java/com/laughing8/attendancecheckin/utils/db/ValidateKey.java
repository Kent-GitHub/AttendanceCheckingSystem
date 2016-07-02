package com.laughing8.attendancecheckin.utils.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Laughing8 on 2016/5/15.
 */
public class ValidateKey extends SQLiteOpenHelper {

    public ValidateKey(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static final String tableName = "validateKey";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists validateKey(" +
                "_id integer primary key autoincrement," +
                "username text unique," +
                "key text " +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
