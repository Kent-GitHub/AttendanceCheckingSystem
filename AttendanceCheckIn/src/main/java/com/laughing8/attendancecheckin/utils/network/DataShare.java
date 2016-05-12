package com.laughing8.attendancecheckin.utils.network;

import android.content.Context;

/**
 * Created by Laughing8 on 2016/5/9.
 */
public class DataShare {
    private Context mContext;

    /**
     * 上班时间
     */
    private int[] swTime = new int[]{9, 30};
    /**
     * 下班时间
     */
    private int[] owTime = new int[]{18, 30};


    public DataShare(Context context) {
        mContext = context;
    }

    public int[] getSwTime() {
        return swTime;
    }

    public int getSWSecond() {
        return swTime[0] * 3600 + swTime[1] * 60;
    }

    public int getOWSecond() {
        return owTime[0] * 3600 + owTime[1] * 60;
    }

    public void setSwTime(int[] swTime) {
        this.swTime = swTime;
    }

    public int[] getOwTime() {
        return owTime;
    }

    public void setOwTime(int[] owTime) {
        this.owTime = owTime;
    }
}
