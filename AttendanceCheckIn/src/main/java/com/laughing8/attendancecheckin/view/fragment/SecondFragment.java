package com.laughing8.attendancecheckin.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.laughing8.attendancecheckin.application.MyApplication;
import com.laughing8.attendancecheckin.bmobobject.MUser;
import com.laughing8.attendancecheckin.view.activity.SecondActivity;

/**
 * Created by Laughing8 on 2016/5/9.
 */
public class SecondFragment extends Fragment {

    protected SecondActivity mActivity;
    protected MyApplication mApplication;
    protected MUser mUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (SecondActivity) getActivity();
        mApplication = (MyApplication) mActivity.getApplication();
        mUser = mApplication.getUser();
    }
}
