package com.laughing8.attendancecheckingsystem.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.laughing8.attendancecheckingsystem.R;
import com.laughing8.attendancecheckingsystem.constants.Actions;
import com.laughing8.attendancecheckingsystem.constants.Constants;
import com.laughing8.attendancecheckingsystem.encryption.AESEncryptor;

/**
 * Created by Laughing8 on 2016/3/25.
 */
public class ValidateActivity extends DrenchedActivity implements View.OnClickListener {

    private final int Status_Set=0;
    private final int Status_First=1;
    private final int Status_Second=2;
    private final int Status_Error=3;
    private final int Status_Enter =4;
    private final int Status_Validate=5;
    private int status;

    /**
     * 提示出入密码的TextView
     */
    private TextView hintTv;
    /**
     * 保存密码字符串
     */
    private String passWord = "";
    /**
     * 加密后的密码
     */
    private String codedPassword;
    private SharedPreferences mPref;
    /**
     * RadioButton充当密码提示器
     */
    private RadioButton[] radioButtons;
    /**
     * 震动
     */
    private Vibrator vibrator;
    /**
     * 数字按键ID
     */
    private int[] btnIds = {R.id.btn_0, R.id.btn_1, R.id.btn_2,
            R.id.btn_3, R.id.btn_4, R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9};
    /**
     * 密码输入提示RadioButtonID
     */
    private int[] radioBtnIds = {R.id.radioButton0, R.id.radioButton1, R.id.radioButton2, R.id.radioButton3};

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        //透明状态栏
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_validate);
        init();
        drenchTitle(findViewById(R.id.titleBar));
        String action=getIntent().getAction();
        if (action!=null&& Actions.Create_Password.equals(action)){
            status=Status_Set;
        }else if (action!=null&& Actions.Validate_Password.equals(action)){
            status=Status_Enter;
        }
        dealValidate(null);
    }

    private String firstTry;
    private void dealValidate(String passWord) {
        switch (status){
            case Status_Set:
                hintTv.setText("初次使用，请输入管理密码");
                status=Status_First;
                break;
            case Status_First:
                hintTv.setText("请在输入一次");
                firstTry = passWord;
                status=Status_Second;
                break;
            case Status_Second:
                if (firstTry.equals(passWord)){
                    try {
                        codedPassword=AESEncryptor.encrypt(Constants.CodeSeed,passWord);
                        mPref.edit().putString(Constants.SettingKey,codedPassword).apply();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    finish();
                    Intent i=new Intent(ValidateActivity.this,SettingActivity.class);
                    startActivity(i);
                    return;
                }
                hintTv.setText("两次输入不一致，请重新输入");
                status=Status_First;
                break;
            case Status_Enter:
                hintTv.setText("请输入密码");
                status=Status_Validate;
                break;
            case Status_Validate:
                try {
                    if (codedPassword.equals(AESEncryptor.encrypt(Constants.CodeSeed,passWord))){
                        Intent i=new Intent(ValidateActivity.this,SettingActivity.class);
                        startActivity(i);
                        finish();
                        return;
                    }
                    hintTv.setText("密码错误，请重新输入");
                    vibrator.vibrate(668);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void init() {
        mPref=getCommonSharedPreferences();
        codedPassword=mPref.getString(Constants.SettingKey,"");
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenHeight =dm.heightPixels;
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        hintTv = (TextView) findViewById(R.id.hint_tv);
        //初始化数字按键
        for (int btnId : btnIds) {
            Button btn = (Button) findViewById(btnId);
            btn.setOnClickListener(this);
            btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, screenHeight/25);
        }
        //delete键绑定点击事件
        findViewById(R.id.btn_delete).setOnClickListener(this);
        findViewById(R.id.actionbar_back).setOnClickListener(this);
        //忘记密码点击事件
        TextView tvForget = (TextView) findViewById(R.id.forget_password);
        tvForget.setOnClickListener(this);
        //初始化RadioButton
        radioButtons=new RadioButton[4];
        for (int i = 0; i < radioBtnIds.length; i++) {
            radioButtons[i] = (RadioButton) findViewById(radioBtnIds[i]);
            radioButtons[i].setClickable(false);
        }
    }


    /**
     * 按下数字按键后更新输入的密码
     * @param add
     */
    private void passWordAdd(String add) {
        passWord+=add;
        int length=passWord.length();
        lightUpRadioButton(length);
        if (length==4){
            dealValidate(passWord);
            passWord="";
            lightUpRadioButton(0);
        }
    }

    /**
     * 删除密码的最后一位
     */
    private void passWordDelete() {
        int length = passWord.length();
        if (length > 0) {
            passWord = passWord.substring(0, length-1);
            lightUpRadioButton(length-1);
        }
    }

    /**
     * 密码输入完毕后判断密码是否正确
     * @param passWord
     * @return
     */
    private boolean passWordCorrect(String passWord) {
        Toast.makeText(getApplicationContext(),passWord,Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * 输入的密码更改后更新密码提示器
     * @param num
     */
    private void lightUpRadioButton(int num) {
        for (int i = 0; i < 4; i++) {
            if (i < num) {
                radioButtons[i].setChecked(true);
            } else {
                radioButtons[i].setChecked(false);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.actionbar_back:
                this.finish();
                break;
            case R.id.btn_1:
                passWordAdd("1");
                break;
            case R.id.btn_2:
                passWordAdd("2");
                break;
            case R.id.btn_3:
                passWordAdd("3");
                break;
            case R.id.btn_4:
                passWordAdd("4");
                break;
            case R.id.btn_5:
                passWordAdd("5");
                break;
            case R.id.btn_6:
                passWordAdd("6");
                break;
            case R.id.btn_7:
                passWordAdd("7");
                break;
            case R.id.btn_8:
                passWordAdd("8");
                break;
            case R.id.btn_9:
                passWordAdd("9");
                break;
            case R.id.btn_0:
                passWordAdd("0");
                break;
            case R.id.btn_delete:
                passWordDelete();
                break;
            case R.id.forget_password:
                Toast.makeText(this,"前联系管理员。。。",Toast.LENGTH_SHORT).show();
                break;
        }

    }
}
