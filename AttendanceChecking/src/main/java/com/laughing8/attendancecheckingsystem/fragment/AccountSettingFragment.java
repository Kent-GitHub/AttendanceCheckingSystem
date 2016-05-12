package com.laughing8.attendancecheckingsystem.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.laughing8.attendancecheckingsystem.R;
import com.laughing8.attendancecheckingsystem.view.SettingViewFroward;

/**
 * Created by Laughing8 on 2016/3/27.
 */
public class AccountSettingFragment extends Fragment implements View.OnClickListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_account_setting, container, false);
        //修改密码
        SettingViewFroward resetPassword=new SettingViewFroward(getActivity());
        resetPassword.getTextL().setText("修改密码");
        resetPassword.getImageR().setTag("resetPassword");
        resetPassword.getImageR().setOnClickListener(this);
        view.addView(resetPassword);
        //修改数字密码
        SettingViewFroward resetNumPassWord=new SettingViewFroward(getActivity());
        resetNumPassWord.getTextL().setText("修改数字密码");
        resetNumPassWord.getImageR().setTag("resetNumPassWord");
        resetNumPassWord.getImageR().setOnClickListener(this);
        view.addView(resetNumPassWord);
        //注销当前账号
        SettingViewFroward logout=new SettingViewFroward(getActivity());
        logout.getTextL().setText("注销当前账号");
        logout.getImageR().setTag("logout");
        logout.getImageR().setOnClickListener(this);
        view.addView(logout);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch ((String)v.getTag()){
            case "resetPassword":

                break;
            case "resetNumPassWord":

                break;
            case "logout":

                break;
        }
    }
}
