package com.laughing8.attendancecheckin.view.fragment.statistical;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.laughing8.attendancecheckin.R;
import com.laughing8.attendancecheckin.application.MyApplication;
import com.laughing8.attendancecheckin.bmobobject.MUser;
import com.laughing8.attendancecheckin.constants.Actions;
import com.laughing8.attendancecheckin.view.activity.MainActivity;
import com.laughing8.attendancecheckin.view.activity.SecondActivity;

/**
 * Created by Laughing8 on 2016/5/8.
 */
public class StatisticalFragment extends Fragment {

    private MainActivity mActivity;
    private MyApplication mApplication;
    private MUser mUser;

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
        View view = inflater.inflate(R.layout.fragment_statistical, null);
        view.findViewById(R.id.statistics_check).setOnClickListener(onIntentClick);
        view.findViewById(R.id.statistics_late).setOnClickListener(onIntentClick);
        view.findViewById(R.id.statistics_lwe).setOnClickListener(onIntentClick);
        return view;
    }

    private View.OnClickListener onIntentClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(mActivity, SecondActivity.class);
            switch (v.getId()) {
                case R.id.statistics_check:
                    i.setAction(Actions.StatisticalCheck);
                    break;
                case R.id.statistics_late:
                    i.setAction(Actions.StatisticalCheckLate);
                    break;
                case R.id.statistics_lwe:
                    i.setAction(Actions.StatisticalCheckLWE);
                    break;
            }
            startActivity(i);
        }
    };

}
