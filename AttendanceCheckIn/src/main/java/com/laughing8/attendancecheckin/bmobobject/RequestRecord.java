package com.laughing8.attendancecheckin.bmobobject;

import java.io.Serializable;
import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by Laughing8 on 2016/5/21.
 */
public class RequestRecord extends BmobObject implements Serializable{
    private String username;
    private Integer requestType;
    private Integer startTime;
    private Integer endTime;
    private List<String> dates;
    private String content;
    private String executeByUsername;
    private Integer status;

    public String getUsername() {
        return username;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setUsername(String userName) {
        this.username = userName;
    }

    public Integer getRequestType() {
        return requestType;
    }

    public void setRequestType(Integer requestType) {
        this.requestType = requestType;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    public Integer getEndTime() {
        return endTime;
    }

    public void setEndTime(Integer endTime) {
        this.endTime = endTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExecuteByUsername() {
        return executeByUsername;
    }

    public void setExecuteByUsername(String executeByUsername) {
        this.executeByUsername = executeByUsername;
    }

    public List<String> getDates() {
        return dates;
    }

    public void setDates(List<String> dates) {
        this.dates = dates;
    }
}
