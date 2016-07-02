package com.laughing8.attendancecheckin.utils.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Laughing8 on 2016/4/16.
 */
public class UserDbHelper extends SQLiteOpenHelper {
    public UserDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists user(" +
                "_id integer primary key autoincrement," +
                "username text not null," +
                "password text not null," +
                "type text not null," +
                "validated integer," +
                "settingKey text," +
                "name text not null," +
                "sex integer not null," +
                "number text key not null," +
                "company text," +
                "department text," +
                "position text," +
                "phone text," +
                "email text," +
                "icon text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
