package com.laughing8.attendancecheckingsystem.bmobobject;

import cn.bmob.v3.BmobUser;

/**
 * Created by Laughing8 on 2016/3/27.
 */
public class Administrator extends BmobUser{
    private String nickName;
    private Integer userType;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }
}
