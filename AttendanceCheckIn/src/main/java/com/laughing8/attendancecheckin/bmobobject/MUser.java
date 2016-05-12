package com.laughing8.attendancecheckin.bmobobject;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Laughing8 on 2016/4/11.
 */
public class MUser extends BmobUser {
    //    private String username;用户的用户名（必需）。
    //    private String password;用户的密码（必需）。
    //    private String email;用户的电子邮件地址（可选）。
    //    private Boolean emailVerified;邮箱认证状态（可选）。
    //    private String sessionToken;
    //    private String mobilePhoneNumber;手机号码（可选）。
    //    private Boolean mobilePhoneNumberVerified;手机号码的认证状态（可选）。

    private Integer userType;
    private String settingKey;
    private String number;
    private String company;
    private String department;
    private String position;
    private BmobFile icon;
    private String name;
    private Integer sex;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public BmobFile getIcon() {
        return icon;
    }

    public void setIcon(BmobFile icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getSettingKey() {
        return settingKey;
    }

    public void setSettingKey(String settingKey) {
        this.settingKey = settingKey;
    }
}
