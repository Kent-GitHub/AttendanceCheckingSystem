package com.laughing8.attendancecheckin.view.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.laughing8.attendancecheckin.R;
import com.laughing8.attendancecheckin.application.MyApplication;
import com.laughing8.attendancecheckin.bmobobject.MUser;
import com.laughing8.attendancecheckin.constants.Actions;
import com.laughing8.attendancecheckin.constants.Constants;
import com.laughing8.attendancecheckin.utils.db.ValidateKey;
import com.laughing8.attendancecheckin.utils.encryption.AESEncryptor;
import com.laughing8.attendancecheckin.utils.network.NetStatus;
import com.laughing8.attendancecheckin.utils.showDialog.LoadingDialog;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

/**
 * Created by Laughing8 on 2016/4/12.
 */
public class LoginActivity extends FragmentActivity implements View.OnClickListener {

    private Button mLoginBtn;
    private EditText mUserNameEt;
    private EditText mPasswordEt;
    private MyApplication mApplication;
    private MUser mUser;

    //----------------------------------------------------------------------------------------------
    private boolean testMode = false;
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        MyApplication mApplication = (MyApplication) getApplication();
        mApplication.setScreenHeight(dm.heightPixels);
        mApplication.setScreenWidth(dm.widthPixels);
        init();
    }

    private void init() {
        mLoginBtn = (Button) findViewById(R.id.btn_login);
        mLoginBtn.setOnClickListener(this);
        mUserNameEt = (EditText) findViewById(R.id.et_login_userName);
        mPasswordEt = (EditText) findViewById(R.id.et_login_password);
        mApplication = (MyApplication) getApplication();
        mUser = BmobUser.getCurrentUser(this, MUser.class);
        if (mUser != null) {
            mUserNameEt.setVisibility(View.GONE);
            mPasswordEt.setVisibility(View.GONE);
            mLoginBtn.setVisibility(View.GONE);
            mApplication.setUser(mUser);
            int userType = mUser.getUserType();
            Intent i = null;
            if (userType == 2) {
                i = new Intent(LoginActivity.this, MainActivity.class);
                i.setAction(Actions.FromNormalUser);
            } else if (userType == 0 || userType == 1) {
                if (testMode) {
                    i = new Intent(LoginActivity.this, MainActivity.class);
                    i.setAction(Actions.FromAdminUser);
                    startActivity(i);
                    finish();
                    return;
                }
                i = new Intent(LoginActivity.this, ValidateActivity.class);
                String validateKey = getValidatedKey(mUser.getUsername());
                if (validateKey != null && validateKey.length() == 4) {
                    i.setAction(Actions.ValidateFromLogin);
                    mApplication.setValidateKey(validateKey);
                } else {
                    i.setAction(Actions.CreatePassword);
                }
            }
            startActivity(i);
            finish();
        }
    }

    private String getValidatedKey(String userName) {
        String validatedWord = "";
        SQLiteDatabase db = mApplication.getValidateDbHelper().getReadableDatabase();
        try {
            Cursor c = db.query(ValidateKey.tableName, null, "username = ?",
                    new String[]{userName}, null, null, null);
            if (c.moveToNext()) {
                validatedWord = c.getString(c.getColumnIndex("key"));
                c.close();
            }
            validatedWord = AESEncryptor.decrypt(Constants.AESDecryptKey, validatedWord);
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return validatedWord;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                showLoading();
                break;
        }
    }

    private LoadingDialog dialog;

    private void showLoading() {
        final String userName = mUserNameEt.getText().toString();
        final String password = mPasswordEt.getText().toString();
        if (userName.equals("") || password.equals("")) {
            Toast.makeText(this, "用户名或密码不能为空...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!NetStatus.netWorkAccess(this)) {
            Toast.makeText(this, "无网络连接...", Toast.LENGTH_SHORT).show();
            return;
        }
        dialog = new LoadingDialog();
        dialog.show(getSupportFragmentManager(), "loadingDialog");
        MUser.loginByAccount(this, userName, password, new LogInListener<MUser>() {
            @Override
            public void done(MUser user, BmobException e) {
                mUser = user;
                bmobException = e;
                if (activityStop) doLoginDone = true;
                else loginDone(mUser, bmobException);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (doLoginDone) {
            loginDone(mUser, bmobException);
        }
    }

    private BmobException bmobException;
    private boolean doLoginDone;

    private void loginDone(MUser mUser, BmobException e) {
        doLoginDone = false;
        if (dialog != null) {
            dialog.dismiss();
        }
        if (mUser == null) {
            if (e.getErrorCode() == 9015) {
                Toast.makeText(LoginActivity.this, "网络连接不稳定...", Toast.LENGTH_SHORT).show();
            } else if (e.getErrorCode() == 101) {
                Toast.makeText(LoginActivity.this, "用户名或密码错误...", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
            mApplication.setUser(mUser);
            loginSucceed(mUser);
        }
    }

    private void loginSucceed(MUser mUser) {
        int userType = mUser.getUserType();
        Intent i = null;
        if (userType == 2) {
            i = new Intent(LoginActivity.this, MainActivity.class);
            i.setAction(Actions.FromNormalUser);
        } else if (userType == 0 || userType == 1) {
            i = new Intent(LoginActivity.this, ValidateActivity.class);
            String validateKey = getValidatedKey(mUser.getUsername());
            if (validateKey != null && validateKey.length() == 4) {
                i.setAction(Actions.ValidateFromLogin);
                mApplication.setValidateKey(validateKey);
            } else {
                i.setAction(Actions.CreatePassword);
            }
        }
        startActivity(i);
        finish();
    }

    private boolean activityStop;

    @Override
    protected void onStart() {
        super.onStart();
        activityStop = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        activityStop = true;
    }
}
