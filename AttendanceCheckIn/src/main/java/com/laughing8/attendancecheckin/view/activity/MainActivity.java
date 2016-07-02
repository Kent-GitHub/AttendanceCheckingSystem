package com.laughing8.attendancecheckin.view.activity;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.laughing8.attendancecheckin.R;
import com.laughing8.attendancecheckin.application.MyApplication;
import com.laughing8.attendancecheckin.bmobobject.MUser;
import com.laughing8.attendancecheckin.constants.Actions;
import com.laughing8.attendancecheckin.view.custom.IndicatorView;
import com.laughing8.attendancecheckin.view.fragment.QRCodeFragment;
import com.laughing8.attendancecheckin.view.fragment.checkin.CheckInFragment;
import com.laughing8.attendancecheckin.view.fragment.found.FoundFragment;
import com.laughing8.attendancecheckin.view.fragment.me.MeFragment;
import com.laughing8.attendancecheckin.view.fragment.request.RequestFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;
    private List<Fragment> mFragments;
    private FragmentPagerAdapter mAdapter;
    private List<IndicatorView> mIndicatorViews;
    private TextView mTitle;
    private ImageView mIconIV;
    private Bitmap mIcon;
    private MUser mUser;
    private String action;
    private boolean isAdmin;
    private View titleBar;
    private Fragment fragmentOne;
    private Fragment fragmentTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        initLayout();
        init();
    }

    public MUser getUser() {
        return mUser;
    }

    private void init() {
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mTitle = (TextView) findViewById(R.id.actionbar_title);
        mIconIV = (ImageView) findViewById(R.id.title_icon);

        mFragments = new ArrayList<>();

        MyApplication mApplication = (MyApplication) getApplication();
        mUser = mApplication.getUser();
        if (mUser == null) {
            Log.d("", "");
            reLogin(mApplication);
            return;
        }
        createBitmapByName(mUser.getName());
        fragmentOne = null;
        action = getIntent().getAction();
        if (Actions.FromNormalUser.equals(action)) {
            fragmentOne = new QRCodeFragment();
        } else if (Actions.FromAdminUser.equals(action) || Actions.FromRootUser.equals(action)) {
            isAdmin = true;
            mTitle.setText("签到管理");
            fragmentOne = new CheckInFragment();
        }
        fragmentTwo = new RequestFragment();
        FoundFragment fragmentThree = new FoundFragment();
        fragmentThree.setArguments(new Bundle());
        MeFragment fragmentFour = new MeFragment();
        mFragments.add(fragmentOne);
        mFragments.add(fragmentTwo);
        mFragments.add(fragmentThree);
        mFragments.add(fragmentFour);

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }
        };

        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(this);
        mIndicatorViews = new ArrayList<>();

        IndicatorView one = (IndicatorView) findViewById(R.id.indicator1);
        IndicatorView two = (IndicatorView) findViewById(R.id.indicator2);
        IndicatorView three = (IndicatorView) findViewById(R.id.indicator3);
        IndicatorView four = (IndicatorView) findViewById(R.id.indicator4);
        mIndicatorViews.add(one);
        mIndicatorViews.add(two);
        mIndicatorViews.add(three);
        mIndicatorViews.add(four);
        one.setOnClickListener(this);
        one.setImageResource(R.drawable.icon_qr_code);
        one.setText("签到");
        two.setOnClickListener(this);
        two.setImageResource(R.drawable.ic_request);
        two.setText("申请");
        three.setOnClickListener(this);
        three.setImageResource(R.drawable.icon_found);
        four.setOnClickListener(this);
        four.setImageResource(R.drawable.icon_me);
        one.setIconAlpha(1.0f);
    }

    private void reLogin(MyApplication mApplication) {
        Toast.makeText(this, "登录信息已失效,请重新登录", Toast.LENGTH_SHORT).show();
        mApplication.getPref().edit().putBoolean("logged", false).apply();
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    private void initLayout() {
        MyApplication mApplication = (MyApplication) getApplication();
        //TitleBar部分
        int screenHeight = mApplication.getScreenHeight();
        titleBar = findViewById(R.id.titleBar);
        ViewGroup.LayoutParams titleLp = titleBar.getLayoutParams();
        titleLp.height = (int) (screenHeight * (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? 0.1158 : 0.08));
        titleHeight = titleLp.height;
        titleBar.setLayoutParams(titleLp);
    }

    private int titleHeight;

    public int getTitleHeight() {
        return titleHeight;
    }

    public Bitmap getIcon() {
        return mIcon;
    }

    private void createBitmapByName(String name) {
        int width = titleHeight;
        int height = titleHeight;
        mIcon = Bitmap.createBitmap((width), (width), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mIcon);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#c48dd0"));
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setDither(true);
        canvas.drawCircle(width / 2, width / 2, width / 2, paint);
        Paint textPaint = new Paint();
        textPaint.setColor(Color.parseColor("#ffffff"));
        textPaint.setTextSize(width * 0.7f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        if (name == null) {
            name = "佚名";
        }
        canvas.drawText(name, 0, 1, width / 2, width * 0.75f, textPaint);
    }

    public void setIcon(Bitmap mIcon) {
        this.mIcon = mIcon;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.indicator1:
                mIndicatorViews.get(0).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(0);
                break;
            case R.id.indicator2:
                mIndicatorViews.get(1).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(1);
                break;
            case R.id.indicator3:
                mIndicatorViews.get(2).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(2);
                break;
            case R.id.indicator4:
                mIndicatorViews.get(3).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(3);
                break;
        }

    }

    private ArgbEvaluator evaluator = new ArgbEvaluator();

    //ViewPager.OnPageChangeListener
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset > 0) {
            IndicatorView left = mIndicatorViews.get(position);
            IndicatorView right = mIndicatorViews.get(position + 1);
            left.setIconAlpha((1 - positionOffset));
            right.setIconAlpha(positionOffset);
        }
        if (position == 0 && isAdmin) {
            int color = (Integer) evaluator.evaluate(positionOffset, 0XFF1782ef, 0XFF21292b);
            titleBar.setBackgroundColor(color);
        }
    }

    //2295.9-369.96=1925.94
    //32196.53
    //ViewPager.OnPageChangeListener
    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                mTitle.setText("签到");
                break;
            case 1:
                mTitle.setText("申请");
                break;
            case 2:
                mTitle.setText("发现");
                break;
            case 3:
                mTitle.setText("我");
                break;
        }
    }

    //ViewPager.OnPageChangeListener
    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            int position = mViewPager.getCurrentItem();
            for (int i = 0; i < mIndicatorViews.size(); i++) {
                if (i != position) {
                    mIndicatorViews.get(i).setIconAlpha(0f);
                }
            }
        }
    }

}
