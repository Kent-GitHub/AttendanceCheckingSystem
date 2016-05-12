package com.laughing8.attendancecheckingsystem;

import android.app.Application;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;

/**
 * Created by Laughing8 on 2016/3/27.
 */
public class MyApplication extends Application{
    public static BmobUser user;

    @Override
    public void onCreate() {
        super.onCreate();
        Bmob.initialize(this, "5cf3623d543b2f6e7b639d73f2a58588");
    }
}
