package com.laughing8.attendancecheckingsystem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.laughing8.attendancecheckingsystem.R;
import com.laughing8.attendancecheckingsystem.constants.Actions;
import com.laughing8.attendancecheckingsystem.constants.Constants;

public class WelcomeActivity extends DrenchedActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        mThread.start();
    }

    private Thread mThread=new Thread(){
        @Override
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mHandler.sendEmptyMessage(0);
        }
    };

    private void init() {
        boolean logged = mPref.getBoolean(Constants.LoggedKey,false);
        Intent i ;
        if (logged){
            i=new Intent(WelcomeActivity.this,ValidateActivity.class);
            i.setAction(Actions.Validate_Password);
        }else {
            i=new Intent(WelcomeActivity.this,LoginActivity.class);
        }
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        startActivity(i);
        finish();
    }

    private Handler mHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what==0){
                init();
            }
            return false;
        }
    });
}
