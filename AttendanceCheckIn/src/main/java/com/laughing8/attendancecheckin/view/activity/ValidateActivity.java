package com.laughing8.attendancecheckin.view.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.avast.android.dialogs.iface.ISimpleDialogListener;
import com.laughing8.attendancecheckin.R;
import com.laughing8.attendancecheckin.application.MyApplication;
import com.laughing8.attendancecheckin.bmobobject.MUser;
import com.laughing8.attendancecheckin.constants.Actions;
import com.laughing8.attendancecheckin.constants.Constants;
import com.laughing8.attendancecheckin.utils.db.ValidateKey;
import com.laughing8.attendancecheckin.utils.encryption.AESEncryptor;

/**
 * Created by Laughing8 on 2016/3/25.
 */
public class ValidateActivity extends FragmentActivity implements View.OnClickListener, ISimpleDialogListener {

    private final int Status_Set = 0;
    private final int Status_First = 1;
    private final int Status_Second = 2;
    private final int Status_Error = 3;
    private final int Status_Enter = 4;
    private final int Status_Validate = 5;
    private int status;
    private String action;
    private MUser mUser;

    /**
     * 提示出入密码的TextView
     */
    private TextView hintTv;
    /**
     * 保存密码字符串
     */
    private String passWord = "";
    /**
     *
     */
    private String key;

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

    private MyApplication mApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        setContentView(R.layout.activity_validate);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        mApplication = (MyApplication) getApplication();
        action = getIntent().getAction();
        init();
        if (action != null && Actions.CreatePassword.equals(action)) {
            status = Status_Set;
        } else if (action != null &&
                (Actions.ValidatePassword.equals(action) || Actions.ValidateFromLogin.equals(action))) {
            key = mApplication.getValidateKey();
            status = Status_Enter;
        }
        dealValidate(null);
    }

    /**
     * 保存创建密码时第一次输入的密码
     */
    private String firstTry;

    private void dealValidate(String passWord) {
        switch (status) {
            case Status_Set:
                hintTv.setText("初次登录，请输入管理密码");
                status = Status_First;
                break;
            case Status_First:
                hintTv.setText("请在输入一次");
                firstTry = passWord;
                status = Status_Second;
                break;
            case Status_Second:
                if (firstTry.equals(passWord)) {
                    Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show();
                    MUser user = mApplication.getUser();
                    mApplication.setValidateKey(passWord);
                    SQLiteDatabase db = mApplication.getValidateDbHelper().getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("username", user.getUsername());
                    try {
                        values.put("key", AESEncryptor.encrypt(Constants.AESDecryptKey, passWord));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Cursor c = db.query(ValidateKey.tableName, null, "username = ?",
                            new String[]{user.getName()}, null, null, null);
                    if (c.moveToNext()) {
                        db.update(ValidateKey.tableName, values, "username = ?", new String[]{user.getName()});
                        c.close();
                    } else {
                        db.insert(ValidateKey.tableName, null, values);
                    }
                    db.close();
                    int type = user.getUserType();
                    Intent i = new Intent(ValidateActivity.this, MainActivity.class);
                    if (type == 0) {
                        i.setAction(Actions.FromRootUser);
                    } else if (type == 1) {
                        i.setAction(Actions.FromAdminUser);
                    }
                    startActivity(i);
                    finish();
                    return;
                }
                hintTv.setText("两次输入不一致，请重新输入");
                status = Status_First;
                break;
            case Status_Enter:
                hintTv.setText("请输入验证密码");
                status = Status_Validate;
                break;
            case Status_Validate:
                MUser user = mApplication.getUser();
                if (passWord.equals(key)) {
                    int type = user.getUserType();
                    Intent i = new Intent(ValidateActivity.this, MainActivity.class);
                    if (type == 0) i.setAction(Actions.FromRootUser);
                    else if (type == 1) i.setAction(Actions.FromAdminUser);
                    if (Actions.ValidatePassword.equals(action)) {
                        setResult(Constants.ValidateSucceed);
                        finish();
                        return;
                    }
                    startActivity(i);
                    finish();
                    return;
                }
                hintTv.setText("密码错误，请重新输入");
                vibrator.vibrate(668);
                break;
        }
    }

    private void init() {
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        hintTv = (TextView) findViewById(R.id.hint_tv);
        //titleBar
        int screenHeight = mApplication.getScreenHeight();
        View titleBar = findViewById(R.id.titleBar);
        ViewGroup.LayoutParams titleLp = titleBar.getLayoutParams();
        titleLp.height = (int) (screenHeight * (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? 0.1158 : 0.08));
        titleBar.setLayoutParams(titleLp);
        //初始化数字按键
        for (int btnId : btnIds) {
            Button btn = (Button) findViewById(btnId);
            btn.setOnClickListener(this);
            btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, screenHeight / 25);
        }
        //delete键绑定点击事件
        findViewById(R.id.btn_delete).setOnClickListener(this);
        findViewById(R.id.title_back).setOnClickListener(this);
        //忘记密码点击事件
        TextView tvForget = (TextView) findViewById(R.id.forget_password);
        tvForget.setOnClickListener(this);
        //初始化RadioButton
        radioButtons = new RadioButton[4];
        for (int i = 0; i < radioBtnIds.length; i++) {
            radioButtons[i] = (RadioButton) findViewById(radioBtnIds[i]);
            radioButtons[i].setClickable(false);
        }
    }


    /**
     * 按下数字按键后更新输入的密码
     */
    private void passWordAdd(String add) {
        passWord += add;
        int length = passWord.length();
        lightUpRadioButton(length);
        if (length == 4) {
            dealValidate(passWord);
            passWord = "";
            lightUpRadioButton(0);
        }
    }

    /**
     * 删除密码的最后一位
     */
    private void passWordDelete() {
        int length = passWord.length();
        if (length > 0) {
            passWord = passWord.substring(0, length - 1);
            lightUpRadioButton(length - 1);
        }
    }

    /**
     * 输入的密码更改后更新密码提示器
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
            case R.id.title_back:
                quit();
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
                Toast.makeText(this, "开发升级中。。。", Toast.LENGTH_SHORT).show();
                break;
        }

    }

    private void quit() {
        if (Actions.CreatePassword.equals(action) || Actions.ValidateFromLogin.equals(action)) {
            SimpleDialogFragment.createBuilder(ValidateActivity.this, getSupportFragmentManager()).
                    setTitle("确定要退出吗？").setPositiveButtonText("确定").
                    setNegativeButtonText("取消").show();
        } else if (Actions.ValidatePassword.equals(action)) {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            quit();
        }
        return super.onKeyDown(keyCode, event);
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
        this.finish();
    }
}
