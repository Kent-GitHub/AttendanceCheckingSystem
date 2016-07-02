package com.laughing8.attendancecheckin.bmobobject;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;

public class RequestType extends BmobObject {

    /**
     * version:1
     * type:0 String:请假申请
     * type:1 String:外勤申请
     * type:2 String:出差申请
     * type:3 String:加班申请
     */

    public RequestType() {
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

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getVersion() {
        return version;
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
}
