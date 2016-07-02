package com.laughing8.attendancecheckin.bmobobject;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;

public class RecordType extends BmobObject {

    /**
     * version:1
     * type:0 String:上班签到
     * type:1 String:下班签到
     * type:2 String:上班迟到
     * type:3 String:下班早退
     * type:4 String:早班签到
     * type:5 String:晚班签到
     * type:6 String:外勤签到
     * type:7 String:出差签到
     */

    public RecordType() {
        values = new ArrayList<>();
    }

    private Integer version;
    private List<String> values;
    private Integer typeCount;

    public String getTypeString(int type) {
        if (type > values.size()-1) {
            Log.e("getTypeString","type out of range");
            return "未知类型";
        }
        return values.get(type);
    }

    public void addTypeString(String typeString) {
        values.add(typeString);
        typeCount = values.size();
    }

    public void setTypeString(int type, String typeString) {
        if (type>values.size()-1){
            Log.e("setTypeString","type out of range");
            return;
        }
        values.set(type,typeString);
        typeCount = values.size();
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
        typeCount = values.size();
    }

    public Integer getTypeCount() {
        return typeCount;
    }

    public void setTypeCount(Integer typeCount) {
        this.typeCount = typeCount;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
