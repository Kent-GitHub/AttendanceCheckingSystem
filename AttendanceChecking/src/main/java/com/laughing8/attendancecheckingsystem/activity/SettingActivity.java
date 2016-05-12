package com.laughing8.attendancecheckingsystem.activity;

import android.os.Bundle;
import android.view.View;

import com.laughing8.attendancecheckingsystem.R;
import com.laughing8.attendancecheckingsystem.view.SettingViewFroward;

/**
 * Created by Laughing8 on 2016/3/27.
 */
public class SettingActivity extends DrenchedActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        drenchTitle(findViewById(R.id.setting_titleBar));
        init();
    }

    private void init() {
        SettingViewFroward account_setting= (SettingViewFroward) findViewById(R.id.account_setting);
        SettingViewFroward current_table= (SettingViewFroward) findViewById(R.id.current_table);
        SettingViewFroward table_setting= (SettingViewFroward) findViewById(R.id.table_setting);
        SettingViewFroward switch_table= (SettingViewFroward) findViewById(R.id.switch_table);
        SettingViewFroward phone_setting= (SettingViewFroward) findViewById(R.id.phone_setting);
        account_setting.setOnClickListener(this);
        current_table.setOnClickListener(this);
        table_setting.setOnClickListener(this);
        table_setting.hasTopLine(true);
        switch_table.setOnClickListener(this);
        switch_table.hasTopLine(true);
        phone_setting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.account_setting:

                break;
            case R.id.current_table:

                break;
            case R.id.table_setting:

                break;
            case R.id.switch_table:

                break;
            case R.id.phone_setting:

                break;
        }
    }
}
