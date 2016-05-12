package com.laughing8.attendancecheckin.view.fragment.me;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.avast.android.dialogs.iface.ISimpleDialogListener;
import com.laughing8.attendancecheckin.R;
import com.laughing8.attendancecheckin.bmobobject.MUser;
import com.laughing8.attendancecheckin.utils.network.NetStatus;
import com.laughing8.attendancecheckin.view.fragment.SecondFragment;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Laughing8 on 2016/5/12.
 */
public class ChangePassword extends SecondFragment implements View.OnClickListener, ISimpleDialogListener {

    private EditText oldPassword, newPassword, confirmPassword;
    private MUser mUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = mApplication.getUser();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, null);
        oldPassword = (EditText) view.findViewById(R.id.oldPasswordEt);
        newPassword = (EditText) view.findViewById(R.id.newPasswordEt);
        confirmPassword = (EditText) view.findViewById(R.id.confirmPasswordEt);
        view.findViewById(R.id.btnChangePassword).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (!NetStatus.netWorkAccess(mActivity)) {
            Toast.makeText(mActivity, "无网络连接", Toast.LENGTH_SHORT).show();
            return;
        }
        String oldP = oldPassword.getText().toString();
        String newP = newPassword.getText().toString();
        String confirmP = confirmPassword.getText().toString();
        if (oldP.equals("")) {
            Toast.makeText(mActivity, "旧密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (newP.equals("")) {
            Toast.makeText(mActivity, "新密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        } else if (newP.length() < 6) {
            Toast.makeText(mActivity, "请设置6位或以上长度密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newP.equals(confirmP)) {
            Toast.makeText(mActivity, "两次输入密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (v.getId()) {
            case R.id.btnChangePassword:
                SimpleDialogFragment.createBuilder(mActivity, mActivity.getSupportFragmentManager()).
                        setTargetFragment(this, 0).setTitle("确定修改密码吗？").
                        setPositiveButtonText("确定").setNegativeButtonText("取消").show();
                break;
        }
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
    public void onPositiveButtonClicked(int requestCode) {
        String oldP = oldPassword.getText().toString();
        final String newP = newPassword.getText().toString();
        MUser.loginByAccount(mActivity, mUser.getUsername(), oldP, new LogInListener<MUser>() {
            @Override
            public void done(MUser mUser, BmobException e) {
                passwordValidate(mUser != null, newP);
            }
        });
    }

    private void passwordValidate(boolean succeed, final String newP) {
        if (succeed) {
            mUser.setPassword(newP);
            mUser.save(mActivity, new SaveListener() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFailure(int i, String s) {
                    Toast.makeText(mActivity,"code:"+i+", s:"+s, Toast.LENGTH_SHORT).show();
                    //Toast.makeText(mActivity, "密码修改失败", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(mActivity, "旧密码错误", Toast.LENGTH_SHORT).show();
        }
    }
}
