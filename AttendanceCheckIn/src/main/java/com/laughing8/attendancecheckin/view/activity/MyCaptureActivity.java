package com.laughing8.attendancecheckin.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.google.zxing.Result;
import com.google.zxing.client.android.CaptureActivity;
import com.laughing8.attendancecheckin.bmobobject.RecordByMouth;
import com.laughing8.attendancecheckin.constants.Actions;
import com.laughing8.attendancecheckin.constants.Constants;
import com.laughing8.attendancecheckin.utils.encryption.AESEncryptor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Laughing8 on 2016/5/4.
 */
public class MyCaptureActivity extends CaptureActivity {

    private int timeInterval = 50;
    private int resultDelay = 2;

    private final int StatusOutOfTime = 0;
    private final int StatusSucceed = 1;
    private final int statusErrorCode = 2;

    private View resultView;
    private TextView resultTitle;
    private TextView resultContent;
    private TextView resultName;

    /**
     * 上班时间
     */
    private int[] swTime = new int[]{9, 30};
    /**
     * 下班时间
     */
    private int[] owTime = new int[]{18, 30};

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        resultTitle = (TextView) findViewById(com.google.zxing.client.android.R.id.capture_result_title);
        resultContent = (TextView) findViewById(com.google.zxing.client.android.R.id.capture_result_number);
        resultName = (TextView) findViewById(com.google.zxing.client.android.R.id.capture_result_name);
        resultView = findViewById(com.google.zxing.client.android.R.id.capture_result);
        findViewById(com.google.zxing.client.android.R.id.capture_btn_back).setOnClickListener(myClick);
        findViewById(com.google.zxing.client.android.R.id.capture_result_close).setOnClickListener(myClick);
    }

    @Override
    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        super.handleDecode(rawResult, barcode, scaleFactor);
        String resultString = rawResult.getText();
        String[] result = resultString.split("_");
        String number = "";
        int scanStatus;
        Date now = new Date();
        try {

            String key = new SimpleDateFormat("yyyy-MM-dd").format(now);
            String time = AESEncryptor.decrypt(key, result[0]);
            number = result[1];
            Date date = new SimpleDateFormat("yyyy-MM-ddHHmmss").parse(key + time);
            if (Math.abs(now.getTime() - date.getTime()) / 1000 < timeInterval) {
                scanStatus = StatusSucceed;
            } else {
                scanStatus = StatusOutOfTime;
            }

            if (scanStatus == StatusSucceed) {
                RecordByMouth record = new RecordByMouth();
                record.setUserName(result[1]);
                record.setName(result[2]);
                record.setDate(new BmobDate(now));
                record.setLocation(new BmobGeoPoint());
                record.setImei(result[3]);
                record.setMac(result[4]);
                record.setType(defineRecordType(now));
                record.setMonth(new SimpleDateFormat("yyyy_MM").format(now));
                record.save(this, mSaveListener);
            }

        } catch (Exception e) {
            e.printStackTrace();
            scanStatus = statusErrorCode;
        }
        showResult(scanStatus, number, result[2]);
    }

    private int defineRecordType(Date date) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.setTime(date);
        int time = c.get(Calendar.HOUR_OF_DAY) * 3600 + c.get(Calendar.MINUTE) * 60 + c.get(Calendar.SECOND);
        int noon = 12 * 3600;
        int startTime = (swTime[0] * 60 + swTime[1]) * 60;
        int ofWorkTime = (owTime[0] * 60 + owTime[1]) * 60;

        if (time <= startTime) {
            return 0;
        } else if (time > startTime && time < noon) {
            return 2;
        } else if (time > noon && time < ofWorkTime) {
            return 3;
        } else if (time > ofWorkTime) {
            return 1;
        }
        return 0;
    }

    private void showResult(int status, String number, String name) {
        resultView.setVisibility(View.VISIBLE);
        viewfinderView.setShowResult(true);
        mHandler.sendEmptyMessageDelayed(0, resultDelay * 1000);
        String content;
        switch (status) {
            case StatusSucceed:
                resultTitle.setText("签到成功");
                resultName.setVisibility(View.VISIBLE);
                resultName.setText("姓名："+name);
                content = "编号：" + number;
                resultContent.setText(content);
                break;
            case StatusOutOfTime:
                resultTitle.setText("签到失败");
                content = "二维码已过期，请校对时间";
                resultName.setVisibility(View.GONE);
                resultContent.setText(content);
                break;
            case statusErrorCode:
                resultTitle.setText("签到失败");
                content = "二维码识别失败";
                resultName.setVisibility(View.GONE);
                resultContent.setText(content);
                break;
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 0) {
                viewfinderView.setShowResult(false);
                resultView.setVisibility(View.GONE);
                restartPreviewAfterDelay(1000L);
            }
            return false;
        }
    });

    public int getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(int timeInterval) {
        this.timeInterval = timeInterval;
    }

    private SaveListener mSaveListener = new SaveListener() {
        @Override
        public void onSuccess() {

        }

        @Override
        public void onFailure(int i, String s) {

        }
    };

    private View.OnClickListener myClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case com.google.zxing.client.android.R.id.capture_result_close:
                    if (resultView != null) {
                        resultView.setVisibility(View.GONE);
                    }
                    break;
                case com.google.zxing.client.android.R.id.capture_btn_back:
                    quit();
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (Constants.RequestFormCapture == requestCode && Constants.ValidateSucceed == resultCode) {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            quit();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void quit() {
        Intent i = new Intent(MyCaptureActivity.this, ValidateActivity.class);
        i.setAction(Actions.ValidatePassword);
        startActivityForResult(i, Constants.RequestFormCapture);
    }

}
