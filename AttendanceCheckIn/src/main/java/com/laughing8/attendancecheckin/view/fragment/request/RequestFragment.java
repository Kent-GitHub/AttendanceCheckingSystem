package com.laughing8.attendancecheckin.view.fragment.request;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.laughing8.attendancecheckin.R;
import com.laughing8.attendancecheckin.application.MyApplication;
import com.laughing8.attendancecheckin.bmobobject.MUser;
import com.laughing8.attendancecheckin.constants.Actions;
import com.laughing8.attendancecheckin.view.activity.MainActivity;
import com.laughing8.attendancecheckin.view.activity.SecondActivity;

/**
 * Created by Laughing8 on 2016/4/9.
 */
public class RequestFragment extends Fragment {

    private MUser mUser;
    private MyApplication mApplication;
    private MainActivity mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mApplication = (MyApplication) mActivity.getApplication();
        mUser = mApplication.getUser();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request, null);
        view.findViewById(R.id.request_request).setOnClickListener(onIntentCLick);
        view.findViewById(R.id.request_approve).setOnClickListener(onIntentCLick);
        view.findViewById(R.id.request_afl).setOnClickListener(onIntentCLick);
        view.findViewById(R.id.request_go_out).setOnClickListener(onIntentCLick);
        view.findViewById(R.id.request_travel).setOnClickListener(onIntentCLick);
        view.findViewById(R.id.request_ot).setOnClickListener(onIntentCLick);
        initLayout(view);
        return view;
    }

    private void initLayout(View view) {
        //iconRequest
        View iconRequest = view.findViewById(R.id.icon_request);
        ViewGroup.LayoutParams requestLp = iconRequest.getLayoutParams();
        requestLp.height = mApplication.getScreenWidth() / 8;
        requestLp.width = mApplication.getScreenWidth() / 8;
        iconRequest.setLayoutParams(requestLp);
        //iconApprove
        View iconApprove = view.findViewById(R.id.icon_approve);
        ViewGroup.LayoutParams approveLp = iconApprove.getLayoutParams();
        approveLp.height = mApplication.getScreenWidth() / 8;
        approveLp.width = mApplication.getScreenWidth() / 8;
        iconApprove.setLayoutParams(approveLp);
        //container1
        View container1 = view.findViewById(R.id.request_container1);
        ViewGroup.LayoutParams container1Lp = container1.getLayoutParams();
        container1Lp.height = (int) (mApplication.getScreenWidth() / 3.2);
        container1.setLayoutParams(container1Lp);
        //container2
        View container2 = view.findViewById(R.id.request_container2);
        ViewGroup.LayoutParams container2Lp = container2.getLayoutParams();
        container2Lp.height = (int) (mApplication.getScreenWidth() / 3);
        container2.setLayoutParams(container2Lp);
        //container3
        View container3 = view.findViewById(R.id.request_container3);
        ViewGroup.LayoutParams container3Lp = container3.getLayoutParams();
        container3Lp.height = (int) (mApplication.getScreenWidth() / 3);
        container3.setLayoutParams(container3Lp);
    }


    private View.OnClickListener onIntentCLick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(getActivity(), SecondActivity.class);
            switch (v.getId()) {
                case R.id.request_request:
                    i.setAction(Actions.MyRequest);
                    break;
                case R.id.request_approve:
                    if (mUser.getUserType() == 2) {
                        i = null;
                        SimpleDialogFragment.createBuilder(mActivity, mActivity.getSupportFragmentManager()).
                                setTitle("没有管理员权限").setNegativeButtonText("关闭").show();

                    } else {
                        i.setAction(Actions.MyApprove);
                    }
                    break;
                case R.id.request_afl:
                    i.setAction(Actions.RequestAFL);
                    break;
                case R.id.request_go_out:
                    i.setAction(Actions.RequestGoOut);
                    break;
                case R.id.request_travel:
                    i.setAction(Actions.RequestTravel);
                    break;
                case R.id.request_ot:
                    i.setAction(Actions.RequestOT);
                    break;
            }
            if (i != null) {
                startActivity(i);
            }
        }
    };
}
