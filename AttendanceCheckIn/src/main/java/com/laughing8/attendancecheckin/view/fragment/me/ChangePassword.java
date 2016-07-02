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
import com.laughing8.attendancecheckin.utils.showDialog.LoadingDialog;
import com.laughing8.attendancecheckin.view.fragment.SecondFragment;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Laughing8 on 2016/5/12.
 */
public class ChangePassword extends SecondFragment implements View.OnClickListener, ISimpleDialogListener {

    private EditText oldPassword, newPassword, confirmPassword;
    private MUser mUser;
    private LoadingDialog loadingDialog;

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
        if (oldP.equals(newP)) {
            Toast.makeText(mActivity, "新旧密码不能相同", Toast.LENGTH_SHORT).show();
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
        newP = newPassword.getText().toString();
        loadingDialog = new LoadingDialog();
        loadingDialog.show(mActivity.getSupportFragmentManager(), "loadingDialog");
        MUser.loginByAccount(mActivity, mUser.getUsername(), oldP, new LogInListener<MUser>() {
            @Override
            public void done(MUser mUser, BmobException e) {
                bmobException = e;
                if (bmobException != null) {
                    if (isResumed()) loginFailed(bmobException);
                    else doLoginFailed = true;
                } else {
                    onPasswordValidate(newP);
                }
            }
        });
    }

    private BmobException bmobException;
    private String newP;
    private boolean doLoginFailed;

    private void loginFailed(BmobException e) {
        doLoginFailed = false;
        loadingDialog.dismiss();
        String error = "修改失败";
        if (e.getErrorCode() == 9015) error = "网络不可用";
        else if (e.getErrorCode() == 101) error = "旧密码错误";
        Toast.makeText(mActivity, error, Toast.LENGTH_SHORT).show();
    }

    private boolean upDateSucceed;

    private void onPasswordValidate(final String newP) {
        MUser newUser = new MUser();
        newUser.setPassword(newP);
        newUser.update(mActivity, mUser.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                upDateSucceed = true;
                if (isResumed()) onPasswordUpdated();
                else doPasswordUpdated = true;
            }

            @Override
            public void onFailure(int i, String s) {
                upDateSucceed = false;
                if (isResumed()) onPasswordUpdated();
                else doPasswordUpdated = true;
            }
        });
    }

    private boolean doPasswordUpdated;

    private void onPasswordUpdated() {
        doPasswordUpdated = false;
        loadingDialog.dismiss();
        if (!upDateSucceed) {
            Toast.makeText(mActivity, "网络错误，修改失败", Toast.LENGTH_SHORT).show();
            return;
        }
        SimpleDialogFragment.createBuilder(mActivity, mActivity.getSupportFragmentManager()).
                setTargetFragment(this, 0).setTitle("密码修改成功").setNegativeButtonText("关闭").show();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (doLoginFailed) {
            loginFailed(bmobException);
        }
        if (doPasswordUpdated) {
            onPasswordUpdated();
        }

    }
}
