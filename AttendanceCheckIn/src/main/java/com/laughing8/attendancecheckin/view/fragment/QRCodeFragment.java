package com.laughing8.attendancecheckin.view.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.avast.android.dialogs.iface.ISimpleDialogListener;
import com.kent.view.CustomDigitalClock;
import com.laughing8.attendancecheckin.R;
import com.laughing8.attendancecheckin.application.MyApplication;
import com.laughing8.attendancecheckin.bmobobject.MUser;
import com.laughing8.attendancecheckin.bmobobject.RecordByMouth;
import com.laughing8.attendancecheckin.bmobobject.RequestRecord;
import com.laughing8.attendancecheckin.constants.Constants;
import com.laughing8.attendancecheckin.utils.encryption.AESEncryptor;
import com.laughing8.attendancecheckin.utils.network.NetStatus;
import com.laughing8.attendancecheckin.utils.qr_code.QRCoderUtils;
import com.laughing8.attendancecheckin.view.activity.MainActivity;

import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

/**
 * Created by Laughing8 on 2016/4/5.
 */
public class QRCodeFragment extends Fragment implements View.OnClickListener, ISimpleDialogListener {

    private ImageView mImageView;

    private boolean mValidated;
    private boolean mLogged;

    private MainActivity mActivity;

    private MyApplication mApplication;
    private MUser mUser;

    private View mView;

    private String number = "1212470119";
    private String phone;
    private int refreshInterval = 8000;
    private SharedPreferences mPref;

    private TextView proofTv, goOutTv, travelTv;

    private String IMEI;
    private String wlanMAC;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mApplication = (MyApplication) getActivity().getApplication();
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
        mView = inflater.inflate(R.layout.fragment_qr_code, null);
        mView.findViewById(R.id.checkInGoOut).setOnClickListener(this);
        mView.findViewById(R.id.checkInTravel).setOnClickListener(this);
        mView.findViewById(R.id.timeProof).setOnClickListener(this);
        mImageView = (ImageView) mView.findViewById(R.id.iv_qr_code);
        proofTv = (TextView) mView.findViewById(R.id.timeProofTv);
        goOutTv = (TextView) mView.findViewById(R.id.goOutTv);
        travelTv = (TextView) mView.findViewById(R.id.travelTv);
        return mView;
    }

    private void initLayout(View view) {
        mPref = getActivity().getSharedPreferences(Constants.SharedPrefKey, Context.MODE_PRIVATE);
        //檢查APP是否已經驗證過
        mValidated = mPref.getBoolean("validated", false);
        mLogged = mPref.getBoolean("logged", false);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        int min = Math.min(screenHeight, screenWidth);
        int max = Math.max(screenHeight, screenWidth);
        //ImageView
        ViewGroup.LayoutParams imageLp = mImageView.getLayoutParams();
        imageLp.height = (int) (min * 0.78);
        imageLp.width = (int) (min * 0.78);
        mImageView.setLayoutParams(imageLp);
    }

    private String encrypted;

    @Override
    public void onResume() {
        super.onResume();
        initLayout(mView);
        threadRun = true;
        fragmentPaused = false;
        refreshInfo();
        if (!mThread.isAlive()) {
            mThread.start();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        fragmentPaused = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        threadRun = false;
    }

    private final int messageRefreshInfo = 1;
    private final int timeRefreshSucceed = 2;
    private final int timeRefreshFailed = 3;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == messageRefreshInfo) refreshInfo();
            else if (msg.what == timeRefreshSucceed) timeUpdated(true);
            else if (msg.what == timeRefreshFailed) timeUpdated(false);
            return false;
        }
    });

    private boolean threadRun;
    private boolean fragmentPaused;
    private Thread mThread = new Thread() {
        @Override
        public void run() {
            while (threadRun) {
                try {
                    Thread.sleep(refreshInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (mHandler != null && !fragmentPaused) {
                    mHandler.sendEmptyMessage(messageRefreshInfo);
                }
            }
        }
    };

    /**
     * 刷新显示数据
     */
    private void refreshInfo() {
//        String key = DateFormat.format("yyyy-MM-dd", CustomDigitalClock.getTime()).toString();
        String key = new SimpleDateFormat("yyyy-MM-dd").format(CustomDigitalClock.getTime().getTime());
//        String time = DateFormat.format("HHmmss", CustomDigitalClock.getTime()).toString();
        String time = new SimpleDateFormat("HHmmss").format(CustomDigitalClock.getTime().getTime());
        encrypted = null;
        try {
            encrypted = AESEncryptor.encrypt(key, time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        encrypted += "_" + mUser.getNumber() + "_" + mUser.getName() + "_" + IMEI + "_" + wlanMAC;
        mImageView.setImageBitmap(QRCoderUtils.enCode(encrypted));
    }

    private void doRegister() {
        //初始化SMSSDK
        SMSSDK.initSDK(getActivity(), Constants.APPKey, Constants.APPSecret);
        //实例化注册界面
        RegisterPage registerPage = new RegisterPage();
        //注册回调事件
        registerPage.setRegisterCallback(new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {

                if (result == SMSSDK.RESULT_COMPLETE) {
                    //从data中获取数据
                    HashMap<String, Object> map = (HashMap<String, Object>) data;
                    String country = (String) map.get("country");
                    phone = (String) map.get("phone");
                    String codedNumber = null;
                    String codedPhone = null;
                    try {
                        codedNumber = AESEncryptor.encrypt(Constants.CodingKey, number);
                        codedPhone = AESEncryptor.encrypt(Constants.CodingKey, phone);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mPref.edit().putString("number", codedNumber)
                            .putString("phone", codedPhone).apply();
                    setValidated(true);
                    refreshInfo();

                    //自定义的数据
                    String uid = 0 + "";
                    String nickName = "John";
                    //提交请求
                    SMSSDK.submitUserInfo(uid, nickName, null, country, phone);
                }
            }
        });
        //显示注册界面
        registerPage.show(getActivity());
    }

    private void setValidated(boolean validated) {
        mValidated = validated;
        mPref.edit().putBoolean("validated", validated).apply();
    }

    private final int goOutRequest = 1;
    private final int travelRequest = 2;
    private boolean savingRecord;

    @Override
    public void onClick(View v) {
        if (!NetStatus.netWorkAccess(mActivity)) {
            Toast.makeText(mActivity, "无网络连接", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (v.getId()) {
            case R.id.checkInGoOut:
                showDialog(goOutRequest);
                break;
            case R.id.checkInTravel:
                showDialog(travelRequest);
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

    private void showDialog(int request) {
        if (savingRecord) return;
        String title = null;
        if (request == goOutRequest) title = "确定要外勤签到吗？";
        else if (request == travelRequest) title = "确定要出差签到吗？";
        SimpleDialogFragment.createBuilder(mActivity, mActivity.getSupportFragmentManager()).
                setTargetFragment(this, request).setTitle(title).
                setPositiveButtonText("确定").setNegativeButtonText("取消").show();
    }

    private boolean timeProofing;

    private void timeUpdated(boolean succeed) {
        timeProofing = false;
        proofTv.setText("时间校对");
        if (succeed) {
            refreshInfo();
            Toast.makeText(mActivity, "时间校对成功", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(mActivity, "校对失败,请检查网络", Toast.LENGTH_SHORT).show();
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
        String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        date = date.replace("-0", "-");
        BmobQuery<RequestRecord> query = new BmobQuery<>();
        query.addWhereEqualTo("username", mUser.getUsername());
        query.addWhereMatches("dates", date);
        if (requestCode == goOutRequest) query.addWhereEqualTo("requestType", 1);
        else if (requestCode == travelRequest) query.addWhereEqualTo("requestType", 2);
        query.findObjects(mActivity, new FindListener<RequestRecord>() {
            @Override
            public void onSuccess(List<RequestRecord> list) {
                boolean hasRight = false;
                if (list.size() > 0) {
                    for (RequestRecord record : list) {
                        if (record.getStatus() == 2) {
                            createRecord(requestCode);
                            hasRight = true;
                        }
                    }
                }
                if (!hasRight) {
                    Toast.makeText(mActivity, "没有签到权限", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(mActivity, "网络受限，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void createRecord(final int requestCode) {
        if (requestCode == goOutRequest || requestCode == travelRequest) {
            savingRecord = true;
            RecordByMouth record = new RecordByMouth();
            record.setUserName(mUser.getUsername());
            record.setName(mUser.getName());
            record.setDate(new BmobDate(new Date()));
            record.setLocation(new BmobGeoPoint());
            record.setImei(IMEI);
            record.setMac(wlanMAC);
            if (requestCode == goOutRequest) {
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

    private void testAddRecord() {
        MUser user = mActivity.getUser();
        Calendar calendar = Calendar.getInstance();
        int dayCount = calendar.getActualMaximum(Calendar.DATE);
        for (int i = 0; i < dayCount; i++) {
            calendar.set(Calendar.DATE, i);
            calendar.set(Calendar.HOUR_OF_DAY, 18);
            int minute = (int) (Math.random() * 20);
            calendar.set(Calendar.MINUTE, minute);
            int second = (int) (Math.random() * 60);
            calendar.set(Calendar.SECOND, second);
            Date date = calendar.getTime();
            RecordByMouth record = new RecordByMouth();
            record.setUserName(user.getUsername());
            record.setName(user.getName());
            record.setDate(new BmobDate(date));
            record.setLocation(new BmobGeoPoint());
            record.setImei(IMEI);
            record.setMac(wlanMAC);
            record.setType(1);
            record.setMonth("2016_05");
            record.save(mActivity);
        }
    }

    private void recordSyncFinish(int request, boolean succeed) {
        savingRecord = false;
        String title = null;
        if (request == goOutRequest) {
            goOutTv.setText("外勤签到");
            if (succeed) title = "外勤签到成功";
            else title = "外勤签到失败";
        } else if (request == travelRequest) {
            travelTv.setText("出差签到");
            if (succeed) title = "出差签到成功";
            else title = "出差签到失败";
        }
        SimpleDialogFragment.createBuilder(mActivity, mActivity.getSupportFragmentManager()).
                setTargetFragment(this, 0).setTitle(title).setNegativeButtonText("关闭").show();
    }

}
