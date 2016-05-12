package com.laughing8.attendancecheckin.view.fragment.me;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.laughing8.attendancecheckin.R;
import com.laughing8.attendancecheckin.application.MyApplication;
import com.laughing8.attendancecheckin.bmobobject.MUser;
import com.laughing8.attendancecheckin.view.activity.SecondActivity;
import com.laughing8.attendancecheckin.view.custom.SettingViewFroward;

/**
 * Created by Laughing8 on 2016/4/9.
 */
public class AccountFragment extends Fragment{

    private MUser mUser;
    private SecondActivity mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication mApplication = (MyApplication) getActivity().getApplication();
        mUser = mApplication.getUser();
        mActivity= (SecondActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_account,null);
        SettingViewFroward icon= (SettingViewFroward) view.findViewById(R.id.account_icon);
        icon.getImageR().setImageBitmap(mActivity.getBitmap());
        //name
        SettingViewFroward name= (SettingViewFroward) view.findViewById(R.id.account_name);
        name.getImageR().setImageBitmap(null);
        name.getTextR().setText(mUser.getName());
        //sex
        SettingViewFroward sex = (SettingViewFroward) view.findViewById(R.id.account_sex);
        sex.getImageR().setImageBitmap(null);
        if (mUser.getSex()==0){
            sex.getTextR().setText("男");
        }else {
            sex.getTextR().setText("女");
        }
        //company
        SettingViewFroward company = (SettingViewFroward) view.findViewById(R.id.account_company);
        company.getImageR().setImageBitmap(null);
        company.getTextR().setText(mUser.getCompany());
        //department
        SettingViewFroward department= (SettingViewFroward) view.findViewById(R.id.account_department);
        department.getImageR().setImageBitmap(null);
        String departmentString=null;
        if (mUser.getDepartment()!=null&&mUser.getPosition()!=null){
            departmentString=mUser.getDepartment()+"-"+mUser.getPosition();
        }else if (mUser.getDepartment()==null&&mUser.getPosition()==null){
            departmentString="";
        }else {
            departmentString=mUser.getDepartment()+mUser.getPosition();
        }
        department.getTextR().setText(departmentString);
        //phone
        SettingViewFroward phone = (SettingViewFroward) view.findViewById(R.id.account_phone);
        phone.getImageR().setImageBitmap(null);
        phone.getTextR().setText(mUser.getMobilePhoneNumber());
        //email
        SettingViewFroward email= (SettingViewFroward) view.findViewById(R.id.account_email);
        email.getImageR().setImageBitmap(null);
        email.getTextR().setText(mUser.getEmail());
        
        return view;
    }
}
