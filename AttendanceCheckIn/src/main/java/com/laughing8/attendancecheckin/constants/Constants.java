package com.laughing8.attendancecheckin.constants;

import java.io.File;

/**
 * Created by Laughing8 on 2016/4/6.
 */
public class Constants {
    public static final String CodingKey = "localKey";
    public static final String AESDecryptKey = "aesDecryptKey";
    public static final String APPKey = "10abd47770dd0";
    public static final String APPSecret = "fccdd12d027f91c993e3ddc4086ce9a5";
    public static final String SharedPrefKey = "CheckInPref";
    public static final String InstanceStatus = "instanceStatus";
    public static final String AlphaStatus = "alphaStatus";
    public static final String UserTable = "user";
    public static final File IconFile = new File("sdcard/aikaoqin/icon.jpg");
    public static final String SettingKey = "settingKey";

    public static final String CodeSeed = "codeSeed";
    public static final String LoggedKey = "loggedKey";
    public static final String UserNameKey = "nameKey";
    public static final String PasswordKey = "passwordKey";

    public static final Integer Root_Administrator = 0;
    public static final Integer Administrator = 1;
    public static final Integer User = 2;

    public static final int RequestLogout = 1;
    public static final int RequestFormCapture = 2;

    public static final int ValidateFailed = 1;
    public static final int ValidateSucceed = 2;
}
