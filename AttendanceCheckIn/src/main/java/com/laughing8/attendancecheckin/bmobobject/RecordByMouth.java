package com.laughing8.attendancecheckin.bmobobject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * Created by Laughing8 on 2016/4/21.
 */
public class RecordByMouth extends BmobObject {
    private String userName;
    private String name;
    private BmobDate date;
    private BmobGeoPoint location;
    private String imei;
    private String mac;
    private int type;
    private String month;

    public RecordByMouth() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM");
        month = sdf.format(new Date());
    }

    public static String getStaticMonth() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM");
        return sdf.format(new Date());
    }

    public static String getStaticMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.clear();
        calendar.set(year, month - 1, day);
        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM");
        return sdf.format(date);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public BmobDate getDate() {
        return date;
    }

    public void setDate(BmobDate date) {
        this.date = date;
    }

    public BmobGeoPoint getLocation() {
        return location;
    }

    public void setLocation(BmobGeoPoint location) {
        this.location = location;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getMac() {
        return mac;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}
