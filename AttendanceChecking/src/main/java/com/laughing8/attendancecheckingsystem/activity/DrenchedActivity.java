package com.laughing8.attendancecheckingsystem.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Laughing8 on 2016/3/27.
 */
public class DrenchedActivity extends Activity{

    protected SharedPreferences mPref;
    protected int screenWidth,screenHeight;


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        //透明状态栏
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        mPref=getSharedPreferences("pref",MODE_PRIVATE);
    }

    public SharedPreferences getCommonSharedPreferences(){
        return mPref;
    }

    public void drenchTitle(View title){
        if (title==null||!(title instanceof View)){
            return;
        }
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
//        int min=Math.min(screenHeight,screenWidth);
//        int max=Math.max(screenHeight,screenWidth);
        ViewGroup.LayoutParams titleLp = title.getLayoutParams();
        titleLp.height = (int) (screenHeight * (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? 0.1125 : 0.0778));
        title.setLayoutParams(titleLp);
    }

}
