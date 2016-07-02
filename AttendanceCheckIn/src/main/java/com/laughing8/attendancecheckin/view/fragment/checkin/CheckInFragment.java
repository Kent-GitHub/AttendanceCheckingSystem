package com.laughing8.attendancecheckin.view.fragment.checkin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.avast.android.dialogs.iface.ISimpleDialogListener;
import com.kent.view.CustomDigitalClock;
import com.laughing8.attendancecheckin.R;
import com.laughing8.attendancecheckin.application.MyApplication;
import com.laughing8.attendancecheckin.bmobobject.MUser;
import com.laughing8.attendancecheckin.bmobobject.RecordByMouth;
import com.laughing8.attendancecheckin.constants.Actions;
import com.laughing8.attendancecheckin.utils.network.DataShare;
import com.laughing8.attendancecheckin.view.activity.MainActivity;
import com.laughing8.attendancecheckin.view.activity.MyCaptureActivity;
import com.laughing8.attendancecheckin.view.activity.SecondActivity;
import com.laughing8.attendancecheckin.view.custom.SettingViewFroward;

import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;

import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Laughing8 on 2016/5/1.
 */
public class CheckInFragment extends Fragment implements View.OnClickListener, ISimpleDialogListener {

    private CustomDigitalClock mClock;
    private View topLayout;
    private View intoScan;
    private MainActivity mActivity;
    private TextView proofTv, travelTv, goOutTv, checkInTv;

    private MUser mUser;

    private String hint;

    private String IMEI;
    private String wlanMAC;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        MyApplication mApplication = (MyApplication) mActivity.getApplication();
        mUser = mApplication.getUser();
        //IMEI
        TelephonyManager telephonyMgr = (TelephonyManager) mApplication.getSystemService(Context.TELEPHONY_SERVICE);
        IMEI = telephonyMgr.getDeviceId().substring(8, 14);
        //wlanMAX
        WifiManager wm = (WifiManager) mApplication.getSystemService(Context.WIFI_SERVICE);
        wlanMAC = wm.getConnectionInfo().getMacAddress().replace(":", "").substring(6, 12);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_check_in, null);
        mClock = (CustomDigitalClock) view.findViewById(R.id.checkIn_clock);
        mClock.setTextColor(Color.parseColor("#000000"));
        SettingViewFroward intoCheckIn = (SettingViewFroward) view.findViewById(R.id.check_into_scan);
        intoCheckIn.setOnClickListener(onIntentClick);

        view.findViewById(R.id.checkInGoOut).setOnClickListener(this);
        view.findViewById(R.id.checkInTravel).setOnClickListener(this);
        view.findViewById(R.id.timeProof).setOnClickListener(this);
        view.findViewById(R.id.checkInIv).setOnClickListener(this);
        view.findViewById(R.id.statistics_check).setOnClickListener(onIntentClick);
        view.findViewById(R.id.statistics_late).setOnClickListener(onIntentClick);
        view.findViewById(R.id.statistics_lwe).setOnClickListener(onIntentClick);

        proofTv = (TextView) view.findViewById(R.id.timeProofTv);
        goOutTv = (TextView) view.findViewById(R.id.goOutTv);
        travelTv = (TextView) view.findViewById(R.id.travelTv);
        checkInTv = (TextView) view.findViewById(R.id.checkInTv);

        initLayout();
        return view;
    }

    private void initLayout() {
        mClock.setY(mActivity.getTitleHeight() * 0.2f);
    }

    public void setTopLayoutBackgroundColor(int color) {
        if (topLayout != null) {
            topLayout.setBackgroundColor(color);
        }
    }

    private View.OnClickListener onIntentClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = null;
            switch (v.getId()) {
                case R.id.check_into_scan:
                    i = new Intent(mActivity, MyCaptureActivity.class);
                    break;
                case R.id.statistics_check:
                    i = new Intent(mActivity, SecondActivity.class);
                    i.setAction(Actions.StatisticalCheck);
                    break;
                case R.id.statistics_late:
                    i = new Intent(mActivity, SecondActivity.class);
                    i.setAction(Actions.StatisticalCheckLate);
                    break;
                case R.id.statistics_lwe:
                    i = new Intent(mActivity, SecondActivity.class);
                    i.setAction(Actions.StatisticalCheckLWE);
                    break;
            }
            startActivity(i);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkInGoOut:
                showNotice(goOutRequest);
                break;
            case R.id.checkInTravel:
                showNotice(travelRequest);
                break;
            case R.id.checkInIv:
                showNotice(checkInRequest);
                break;
            case R.id.timeProof:
                if (!timeProofing) {
                    timeProofing = true;
                    proofTv.setText("校对中");
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                URLConnection conn = new URL("http://www.baidu.com").openConnection();
                                conn.connect();
                                CustomDigitalClock.setTime(conn.getDate());
                                mHandler.sendEmptyMessage(timeRefreshSucceed);
                            } catch (Exception e) {
                                e.printStackTrace();
                                mHandler.sendEmptyMessage(timeRefreshFailed);
                            }
                        }
                    }.start();
                }
                break;
        }
    }


    private final int timeRefreshSucceed = 2;
    private final int timeRefreshFailed = 3;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == timeRefreshSucceed) timeUpdated(true);
            else if (msg.what == timeRefreshFailed) timeUpdated(false);
            return false;
        }
    });

    private final int goOutRequest = 1;
    private final int travelRequest = 2;
    private final int checkInRequest = 3;
    private boolean savingRecord;

    private void showNotice(int request) {
        if (savingRecord) return;
        String title = null;
        if (request == goOutRequest) title = "确定要外勤签到吗？";
        else if (request == travelRequest) title = "确定要出差签到吗？";
        else if (request == checkInRequest) title = "确定要签到吗？";
        SimpleDialogFragment.createBuilder(mActivity, mActivity.getSupportFragmentManager()).
                setTargetFragment(this, request).setTitle(title).
                setPositiveButtonText("确定").setNegativeButtonText("取消").show();
    }


    private boolean timeProofing;

    private void timeUpdated(boolean succeed) {
        timeProofing = false;
        proofTv.setText("时间校对");
        if (succeed) Toast.makeText(mActivity, "时间校对成功", Toast.LENGTH_SHORT).show();
        else Toast.makeText(mActivity, "校对失败,请检查网络", Toast.LENGTH_SHORT).show();
    }

    //ISimpleDialogListener
    @Override
    public void onNegativeButtonClicked(int requestCode) {

    }

    //ISimpleDialogListener
    @Override
    public void onNeutralButtonClicked(int requestCode) {

    }

    //ISimpleDialogListener
    @Override
    public void onPositiveButtonClicked(final int requestCode) {
        if (requestCode == goOutRequest || requestCode == travelRequest || requestCode == checkInRequest) {
            savingRecord = true;
            RecordByMouth record = new RecordByMouth();
            record.setUserName(mUser.getUsername());
            record.setName(mUser.getName());
            record.setDate(new BmobDate(new Date()));
            record.setLocation(new BmobGeoPoint());
            record.setImei(IMEI);
            record.setMac(wlanMAC);
            if (requestCode == checkInRequest) {
                record.setType(defineRecordType(new Date()));
                checkInTv.setText("签到中");
            } else if (requestCode == goOutRequest) {
                record.setType(6);
                goOutTv.setText("签到中");
            } else {
                record.setType(7);
                travelTv.setText("签到中");
            }
            record.setMonth(DateFormat.format("yyyy_MM", Calendar.getInstance()).toString());
            record.save(mActivity, new SaveListener() {
                @Override
                public void onSuccess() {
                    recordSyncFinish(requestCode, true);
                }

                @Override
                public void onFailure(int i, String s) {
                    recordSyncFinish(requestCode, false);
                }
            });

        }
    }

    private void recordSyncFinish(int request, boolean succeed) {
        savingRecord = false;
        String title = null;
        if (request == checkInRequest) {
            checkInTv.setText("管理员签到");
            if (succeed) title = "签到成功";
            else title = "签到失败";
        } else if (request == goOutRequest) {
            goOutTv.setText("外勤签到");
            if (succeed) title = "外勤签到成功";
            else title = "外勤签到失败";
        } else if (request == travelRequest) {
            travelTv.setText("出差签到");
            if (succeed) title = "出差签到成功";
            else title = "出差签到失败";
        }
        if (isResumed()) {
            SimpleDialogFragment.createBuilder(mActivity, mActivity.getSupportFragmentManager()).
                    setTitle(title).setNegativeButtonText("关闭").show();
        } else {
            hint = title;
        }
    }

    private int defineRecordType(Date date) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.setTime(date);
        int time = c.get(Calendar.HOUR_OF_DAY) * 3600 + c.get(Calendar.MINUTE) * 60 + c.get(Calendar.SECOND);
        int noon = 12 * 3600;
        DataShare dataShare = new DataShare(mActivity);
        int startTime = dataShare.getSWSecond();
        int ofWorkTime = dataShare.getOWSecond();

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

    @Override
    public void onResume() {
        super.onResume();
        if (hint != null) {
            SimpleDialogFragment.createBuilder(mActivity, mActivity.getSupportFragmentManager()).
                    setTitle(hint).setNegativeButtonText("关闭").show();
            hint = null;
        }
    }
}
