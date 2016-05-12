package com.laughing8.attendancecheckin.view.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.laughing8.attendancecheckin.R;
import com.laughing8.attendancecheckin.application.MyApplication;
import com.laughing8.attendancecheckin.bmobobject.MUser;
import com.laughing8.attendancecheckin.constants.Actions;
import com.laughing8.attendancecheckin.constants.Constants;
import com.laughing8.attendancecheckin.utils.encryption.AESEncryptor;
import com.laughing8.attendancecheckin.utils.network.NetStatus;
import com.laughing8.attendancecheckin.view.fragment.LoadingDialog;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.LogInListener;

/**
 * Created by Laughing8 on 2016/4/12.
 */
public class LoginActivity extends FragmentActivity implements View.OnClickListener {

    private SharedPreferences mPref;
    private Button mLoginBtn;
    private EditText mUserNameEt;
    private EditText mPasswordEt;
    private MyApplication mApplication;
    private MUser mUser;

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
        mUserNameEt = (EditText) findViewById(R.id.et_login_userName);
        mPasswordEt = (EditText) findViewById(R.id.et_login_password);

        mApplication = (MyApplication) getApplication();

        mPref = getSharedPreferences(Constants.SharedPrefKey, Context.MODE_PRIVATE);
        boolean mLogged = mPref.getBoolean("logged", false);
        boolean validated = false;
        if (mLogged) {
            mUserNameEt.setVisibility(View.GONE);
            mPasswordEt.setVisibility(View.GONE);
            mLoginBtn.setVisibility(View.GONE);
            MUser user = new MUser();
            Cursor c = mApplication.getDbHelper().getWritableDatabase().query(Constants.UserTable,
                    null, null, null, null, null, null);
            int userType = 2;
            if (c.moveToNext()) {
                //username
                user.setUsername(c.getString(c.getColumnIndex("username")));
                //password
                String password = "";
                try {
                    password = AESEncryptor.decrypt(Constants.AESDecryptKey, c.getString(c.getColumnIndex("password")));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("LoginActivity", "password验证错误");
                }
                user.setPassword(password);
                //userType
                try {
                    userType = Integer.parseInt(
                            AESEncryptor.decrypt(Constants.AESDecryptKey, c.getString(c.getColumnIndex("type"))));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("LoginActivity", "userType验证错误");
                }
                user.setUserType(userType);
                //validated
                validated = c.getInt(c.getColumnIndex("validated")) == 1;
                //settingKey
                String settingKey = "";
                try {
                    settingKey = AESEncryptor.decrypt(Constants.AESDecryptKey, c.getString(c.getColumnIndex("settingKey")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                user.setSettingKey(settingKey);
                //name
                user.setName(c.getString(c.getColumnIndex("name")));
                //sex
                user.setSex(c.getInt(c.getColumnIndex("sex")));
                //number
                user.setNumber(c.getString(c.getColumnIndex("number")));
                //company
                user.setCompany(c.getString(c.getColumnIndex("company")));
                //department
                user.setDepartment(c.getString(c.getColumnIndex("department")));
                //position
                user.setPosition(c.getString(c.getColumnIndex("position")));
                //phone
                user.setMobilePhoneNumber(c.getString(c.getColumnIndex("phone")));
                //email
                user.setEmail(c.getString(c.getColumnIndex("email")));
            }
            mApplication.setUser(user);
            Intent i = null;
            if (userType == 2) {
                i = new Intent(LoginActivity.this, MainActivity.class);
                i.setAction(Actions.FromNormalUser);
            } else if (userType == 0 || userType == 1) {
                i = new Intent(LoginActivity.this, ValidateActivity.class);
                if (validated) {
                    i.setAction(Actions.ValidateFromLogin);
                } else {
                    i.setAction(Actions.CreatePassword);
                }
            }
            startActivity(i);
            finish();
            return;
        }
        mLoginBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                showLoading();
                break;
        }
    }

    private void showLoading() {
        final String userName = mUserNameEt.getText().toString();
        final String password = mPasswordEt.getText().toString();
        if (!NetStatus.netWorkAccess(this)) {
            Toast.makeText(this, "无网络连接...", Toast.LENGTH_SHORT).show();
            return;
        }
        final LoadingDialog dialog = new LoadingDialog();
        dialog.show(getSupportFragmentManager(), "loadingDialog");
        MUser.loginByAccount(this, userName, password, new LogInListener<MUser>() {
            @Override
            public void done(MUser mUser, BmobException e) {
                dialog.dismiss();
                if (mUser == null) {
                    if (e.getErrorCode() == 9015) {
                        Toast.makeText(LoginActivity.this, "网络连接不稳定...", Toast.LENGTH_SHORT).show();
                    } else if (e.getErrorCode() == 101) {
                        Toast.makeText(LoginActivity.this, "用户名或密码错误...", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    mApplication.setUser(mUser);
                    loginSucceed(mUser, password);
                }
            }
        });
    }

    private int dbVersion = 1;

    private void loginSucceed(MUser mUser, String password) {
        SQLiteDatabase db = mApplication.getDbHelper().getWritableDatabase();
        final ContentValues values = new ContentValues();
        values.put("username", mUser.getUsername());
        try {
            values.put("password", AESEncryptor.encrypt(Constants.AESDecryptKey, password));
        } catch (Exception e) {
            e.printStackTrace();
        }
        int userType = mUser.getUserType();
        try {
            values.put("type", AESEncryptor.encrypt(Constants.AESDecryptKey, String.valueOf(userType)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        values.put("validated", 0);
        values.put("name", mUser.getName());
        values.put("sex", mUser.getSex());
        values.put("number", mUser.getNumber());
        values.put("company", mUser.getCompany());
        values.put("department", mUser.getDepartment());
        values.put("position", mUser.getPosition());
        values.put("phone", mUser.getMobilePhoneNumber());
        values.put("email", mUser.getEmail());
        if (mUser.getIcon() != null) {
            BmobFile icon = mUser.getIcon();
            icon.download(getApplicationContext(), Constants.IconFile, new DownloadFileListener() {
                @Override
                public void onSuccess(String s) {
                    values.put("icon", Constants.IconFile.getPath());
                }

                @Override
                public void onFailure(int i, String s) {

                }
            });
        }
        db.insert(Constants.UserTable, null, values);
        db.close();
        mPref.edit().putBoolean("logged", true).apply();
        Intent i = null;
        if (userType == 2) {
            i = new Intent(LoginActivity.this, MainActivity.class);
            i.setAction(Actions.FromNormalUser);
        } else if (userType == 0 || userType == 1) {
            i = new Intent(LoginActivity.this, ValidateActivity.class);
            i.setAction(Actions.CreatePassword);
        }
        startActivity(i);
        finish();
    }
}
