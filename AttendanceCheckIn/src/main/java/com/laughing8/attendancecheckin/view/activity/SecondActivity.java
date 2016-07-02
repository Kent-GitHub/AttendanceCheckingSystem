package com.laughing8.attendancecheckin.view.activity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.laughing8.attendancecheckin.R;
import com.laughing8.attendancecheckin.application.MyApplication;
import com.laughing8.attendancecheckin.bmobobject.MUser;
import com.laughing8.attendancecheckin.constants.Actions;
import com.laughing8.attendancecheckin.view.fragment.DevelopingPage;
import com.laughing8.attendancecheckin.view.fragment.found.FoundFragmentMouth;
import com.laughing8.attendancecheckin.view.fragment.me.AccountFragment;
import com.laughing8.attendancecheckin.view.fragment.me.ChangePassword;
import com.laughing8.attendancecheckin.view.fragment.request.InitiateRequest;
import com.laughing8.attendancecheckin.view.fragment.request.MyApprove;
import com.laughing8.attendancecheckin.view.fragment.request.MyRequest;
import com.laughing8.attendancecheckin.view.fragment.statistical.CheckFragment;
import com.laughing8.attendancecheckin.view.fragment.statistical.LWEFragment;
import com.laughing8.attendancecheckin.view.fragment.statistical.LateFragment;

/**
 * Created by Laughing8 on 2016/4/10.
 */
public class SecondActivity extends FragmentActivity implements View.OnClickListener {
    private String action;
    private Bitmap mBitmap;
    private MUser mUser;
    private MyApplication mApplication;
    private TextView mTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        mApplication = (MyApplication) getApplication();
        mUser = mApplication.getUser();
        mTitle = (TextView) findViewById(R.id.actionbar_title);
        findViewById(R.id.title_back).setOnClickListener(this);
        initLayout();
        initFragment();
        createBitmapByName(mUser.getName(), (int) (screenHeight * 0.1));
    }

    private void initFragment() {
        Fragment fragment = null;
        Bundle bundle = null;
        if (getIntent().getAction() == null) {
            return;
        }
        action = getIntent().getAction();
        switch (action) {
            case Actions.MeHelp:

                break;
            case Actions.MeAbout:

                break;
            case Actions.MeFeedBack:

                break;
            case Actions.MeChange_password:
                fragment = new ChangePassword();
                setTitle("修改密码");
                break;
            case Actions.MeInfo:
                fragment = new AccountFragment();
                setTitle("我的信息");
                break;
            case Actions.FoundCheckMonth:
                fragment = new FoundFragmentMouth();
                setTitle("签到记录");
                break;
            case Actions.StatisticalCheck:
                fragment = new CheckFragment();
                setTitle("签到查询");
                break;
            case Actions.StatisticalCheckLate:
                fragment = new LateFragment();
                setTitle("月迟到榜");
                break;
            case Actions.StatisticalCheckLWE:
                fragment = new LWEFragment();
                setTitle("月早退榜");
                break;
            case Actions.MyRequest:
                fragment = new MyRequest();
                setTitle("我申请的");
                break;
            case Actions.MyApprove:
                setTitle("我审批的");
                fragment = new MyApprove();
                break;
            case Actions.RequestAFL:
                fragment = new InitiateRequest();
                bundle = new Bundle();
                bundle.putInt("requestType", 1);
                setTitle("请假申请");
                break;
            case Actions.RequestGoOut:
                fragment = new InitiateRequest();
                bundle = new Bundle();
                bundle.putInt("requestType", 2);
                setTitle("外勤申请");
                break;
            case Actions.RequestTravel:
                fragment = new InitiateRequest();
                bundle = new Bundle();
                bundle.putInt("requestType", 3);
                setTitle("出差申请");
                break;
            case Actions.RequestOT:
                fragment = new InitiateRequest();
                bundle = new Bundle();
                bundle.putInt("requestType", 4);
                setTitle("加班申请");
                break;
        }
        if (fragment == null) {
            fragment = new DevelopingPage();
        }
        if (bundle != null) fragment.setArguments(bundle);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragmentContainer, fragment, fragment.getClass().getName());
        ft.commit();
    }

    private int screenHeight;

    private void initLayout() {
        screenHeight = mApplication.getScreenHeight();
        //TitleBar部分
        View titleBar = findViewById(R.id.titleBar);
        ViewGroup.LayoutParams titleLp = titleBar.getLayoutParams();
        titleLp.height = (int) (screenHeight * (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? 0.1158 : 0.08));
        titleBar.setLayoutParams(titleLp);
    }

    private void setTitle(String title) {
        mTitle.setText(title);
    }

    public MUser getUser() {
        return mUser;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    private void createBitmapByName(String name, int iconHeight) {

        mBitmap = Bitmap.createBitmap(iconHeight, iconHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#c48dd0"));
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setDither(true);
        canvas.drawCircle(iconHeight / 2, iconHeight / 2, iconHeight / 2, paint);
        Paint textPaint = new Paint();
        textPaint.setColor(Color.parseColor("#ffffff"));
        textPaint.setTextSize(iconHeight * 0.7f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        if (name == null) {
            name = "佚名";
        }
        canvas.drawText(name, 0, 1, iconHeight / 2, iconHeight * 0.75f, textPaint);
    }

    public void jumpToFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_slide_right_in, R.anim.fragment_slide_left_out,
                R.anim.fragment_slide_left_in, R.anim.fragment_slide_right_out);
        ft.replace(R.id.fragmentContainer, fragment, fragment.getClass().getName());
        if (addToBackStack) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
        }
    }
}
