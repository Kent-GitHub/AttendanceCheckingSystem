package com.laughing8.attendancecheckin.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;

import com.laughing8.attendancecheckin.bmobobject.MUser;
import com.laughing8.attendancecheckin.constants.Constants;
import com.laughing8.attendancecheckin.utils.db.MDbHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import cn.bmob.v3.Bmob;

/**
 * Created by Laughing8 on 2016/4/6.
 */
public class MyApplication extends Application {

    private MUser mUser;
    private MDbHelper mDbHelper;
    private int dbVersion = 1;
    private SharedPreferences mPref;
    private int screenWidth;
    private int screenHeight;
    @Override
    public void onCreate() {
        super.onCreate();
        Bmob.initialize(this, "5cf3623d543b2f6e7b639d73f2a58588");
        mDbHelper = new MDbHelper(this, "user.db", null, dbVersion);
        mPref = getSharedPreferences(Constants.SharedPrefKey, Context.MODE_PRIVATE);
        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(config);
        DisplayMetrics dm = new DisplayMetrics();
    }

    public MUser getUser() {
        return mUser;
    }

    public void setUser(MUser mUser) {
        this.mUser = mUser;
    }

    public MDbHelper getDbHelper() {
        return mDbHelper;
    }

    public SharedPreferences getPref() {
        return mPref;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }
}
