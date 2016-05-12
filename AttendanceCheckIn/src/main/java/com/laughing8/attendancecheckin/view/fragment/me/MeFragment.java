package com.laughing8.attendancecheckin.view.fragment.me;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.avast.android.dialogs.iface.ISimpleDialogListener;
import com.laughing8.attendancecheckin.R;
import com.laughing8.attendancecheckin.application.MyApplication;
import com.laughing8.attendancecheckin.bmobobject.MUser;
import com.laughing8.attendancecheckin.constants.Actions;
import com.laughing8.attendancecheckin.constants.Constants;
import com.laughing8.attendancecheckin.view.custom.SettingViewFroward;
import com.laughing8.attendancecheckin.view.activity.LoginActivity;
import com.laughing8.attendancecheckin.view.activity.MainActivity;
import com.laughing8.attendancecheckin.view.activity.SecondActivity;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Laughing8 on 2016/4/9.
 */
public class MeFragment extends Fragment implements View.OnClickListener, ISimpleDialogListener {

    private View mView;
    private ImageView mIcon;
    private TextView mName, mDepartment;
    private List<SettingViewFroward> settings;
    private MUser mUser;
    private MainActivity mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication mApplication = (MyApplication) getActivity().getApplication();
        mUser = mApplication.getUser();
        mActivity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_me, null);
        mIcon = (ImageView) mView.findViewById(R.id.me_icon);
        mIcon.setImageBitmap(((MainActivity) getActivity()).getIcon());
        mName = (TextView) mView.findViewById(R.id.me_name);
        mDepartment = (TextView) mView.findViewById(R.id.me_department);
        settings = new ArrayList<>();
        settings.add((SettingViewFroward) mView.findViewById(R.id.me_help));
        settings.add((SettingViewFroward) mView.findViewById(R.id.me_about));
        settings.add((SettingViewFroward) mView.findViewById(R.id.me_feedBack));
        settings.add((SettingViewFroward) mView.findViewById(R.id.me_change_password));
        settings.add((SettingViewFroward) mView.findViewById(R.id.me_logout));
        BmobFile icon = new BmobFile();
        init();
        return mView;
    }

    private void init() {
        mView.findViewById(R.id.me_info).setOnClickListener(onIntentClick);
        for (int i = 0; i < settings.size() - 1; i++) {
            settings.get(i).setOnClickListener(onIntentClick);
        }
        settings.get(settings.size() - 1).setOnClickListener(this);
        mName.setText(mUser.getName());
        mDepartment.setText(mUser.getCompany());
    }

    private View.OnClickListener onIntentClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(getActivity(), SecondActivity.class);
            switch (v.getId()) {
                case R.id.me_help:
                    i.setAction(Actions.MeHelp);
                    break;
                case R.id.me_about:
                    i.setAction(Actions.MeAbout);
                    break;
                case R.id.me_feedBack:
                    i.setAction(Actions.MeFeedBack);
                    break;
                case R.id.me_change_password:
                    i.setAction(Actions.MeChange_password);
                    break;
                case R.id.me_info:
                    i.setAction(Actions.MeInfo);
                    break;
            }
            startActivity(i);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.me_logout:
                SimpleDialogFragment.createBuilder(mActivity, mActivity.getSupportFragmentManager()).
                        setTargetFragment(this, Constants.RequestLogout).setTitle("你确定要注销吗？").setPositiveButtonText("确定").
                        setNegativeButtonText("取消").show();
                break;
        }
    }

    //ISimpleDialogListener
    @Override
    public void onPositiveButtonClicked(int requestCode) {
        MyApplication mApplication = (MyApplication) getActivity().getApplication();
        SQLiteDatabase db = mApplication.getDbHelper().getWritableDatabase();
        db.execSQL("DELETE FROM "+Constants.UserTable);
        mApplication.getPref().edit().putBoolean("logged",false).apply();
        Intent i=new Intent(mActivity, LoginActivity.class);
        startActivity(i);
        mActivity.finish();
    }

    //ISimpleDialogListener
    @Override
    public void onNegativeButtonClicked(int requestCode) {

    }

    //ISimpleDialogListener
    @Override
    public void onNeutralButtonClicked(int requestCode) {

    }
}
