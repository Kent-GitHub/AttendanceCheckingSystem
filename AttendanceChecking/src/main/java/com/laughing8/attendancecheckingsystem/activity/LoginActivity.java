package com.laughing8.attendancecheckingsystem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.laughing8.attendancecheckingsystem.MyApplication;
import com.laughing8.attendancecheckingsystem.R;
import com.laughing8.attendancecheckingsystem.bmobobject.Administrator;
import com.laughing8.attendancecheckingsystem.constants.Actions;
import com.laughing8.attendancecheckingsystem.constants.Constants;
import com.laughing8.attendancecheckingsystem.encryption.AESEncryptor;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

/**
 * Created by Laughing8 on 2016/3/26.
 */
public class LoginActivity extends DrenchedActivity implements View.OnClickListener, TextView.OnEditorActionListener {

    private EditText userNameEdTv, passwordEdTv;
    private String userName, password,codedPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        initLayout();
    }

    private void initLayout() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
//        int screenHeight = dm.heightPixels;
        View contentView = findViewById(R.id.content_view);
        ViewGroup.LayoutParams contentLp = contentView.getLayoutParams();
        contentLp.width = screenWidth * 4 / 5;
        contentView.setLayoutParams(contentLp);
    }

    private void init() {
        userNameEdTv = (EditText) findViewById(R.id.login_user_name);
        passwordEdTv = (EditText) findViewById(R.id.login_password);
        passwordEdTv.setOnEditorActionListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_register).setOnClickListener(this);
        findViewById(R.id.forget_login_password).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                doLogin();
                break;
            case R.id.btn_register:
                Toast.makeText(this, "暂未开放。。。", Toast.LENGTH_SHORT).show();
                break;
            case R.id.forget_login_password:
                String adminPhone = "188-8888-8888";
                Toast.makeText(this, "请联系管理员:" + adminPhone + ".", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void doLogin() {
        userName = userNameEdTv.getText().toString();
        password = passwordEdTv.getText().toString();
        codedPassword=null;
        try {
            codedPassword= AESEncryptor.encrypt(Constants.CodeSeed,password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        login(userName, codedPassword);
    }

    private void dealLoginResult(boolean succeed){
        if (succeed) {
            mPref.edit().putBoolean(Constants.LoggedKey,true)
                    .putString(Constants.UserNameKey,userName)
                    .putString(Constants.PasswordKey,codedPassword).apply();
            Intent i=new Intent(LoginActivity.this,ValidateActivity.class);
            i.setAction(Actions.Create_Password);
            finish();
            startActivity(i);
        } else {
            Toast.makeText(this, "用户名或密码错误，请重新输入", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean login(String userName, String password) {
        Administrator admin=new Administrator();
        admin.setUsername(userName);
        admin.setPassword(password);
        Log.e(userName,password);
        Administrator.loginByAccount(this, userName, password, new LogInListener<Administrator>() {
            @Override
            public void done(Administrator user, BmobException e) {
                if (user!=null){
                    Toast.makeText(LoginActivity.this,"user!=null",Toast.LENGTH_SHORT).show();
                    MyApplication.user=user;
                    dealLoginResult(true);
                }else {
                    Toast.makeText(LoginActivity.this,"user==null",Toast.LENGTH_SHORT).show();
                    dealLoginResult(false);
                }
            }
        });
        return false;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId== EditorInfo.IME_ACTION_GO&&v.getId()==R.id.login_password){
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),0);
            }
            doLogin();
        }
        return false;
    }
}
